package mealplanner

import java.io.File
import java.sql.SQLException
import kotlin.io.path.Path
import kotlin.io.path.appendLines
import kotlin.io.path.appendText
import kotlin.io.path.writeLines

private val categories = listOf("breakfast", "lunch", "dinner")

enum class DayOfWeek(val intValue: Int, val displayName: String) {
    MONDAY(0, "Monday"),
    TUESDAY(1, "Tuesday"),
    WEDNESDAY(2, "Wednesday"),
    THURSDAY(3, "Thursday"),
    FRIDAY(4, "Friday"),
    SATURDAY(5, "Saturday"),
    SUNDAY(6, "Sunday");

    companion object {
        fun getDayByIntValue(value: Int): DayOfWeek {
            return when (value) {
                MONDAY.intValue -> MONDAY
                TUESDAY.intValue -> TUESDAY
                WEDNESDAY.intValue -> WEDNESDAY
                THURSDAY.intValue -> THURSDAY
                FRIDAY.intValue -> FRIDAY
                SATURDAY.intValue -> SATURDAY
                SUNDAY.intValue -> SUNDAY
                else -> throw IllegalArgumentException("Invalid day value")
            }
        }
    }
}


private const val ACTION_ADD = "add"
private const val ACTION_SHOW = "show"
private const val ACTION_EXIT = "exit"
private const val ACTION_PLAN = "plan"
private const val ACTION_SAVE = "save"

fun main() {
    createMealTable()
    createIngredientTable()
    createMealPlanTable()

    while (true) {
        println("What would you like to do (add, show, plan, save, exit)?")
        when (readUserInput()) {
            ACTION_ADD -> {
                val newMealId = generateMealId()
                if (newMealId == -1) {
                    throw IllegalStateException("Unable to generate meal ID")
                }

                val mealCreated = createMeal(newMealId)
                insertMeal(mealCreated)
                insertIngredients(mealCreated)

                println("The meal has been added!")
            }

            ACTION_SHOW -> {
                val category = getCategory()

                val meals = getMealsByCategory(category)
                if (meals.isEmpty()) {
                    println("No meals found.")
                    continue
                }

                val builder = StringBuilder()
                builder.append("Category: ${meals.first().category}")
                builder.appendLine()

                meals.forEach {
                    builder.appendLine()
                    builder.append(it.toString())
                    builder.appendLine()
                }
                println(builder.toString())
            }

            ACTION_PLAN -> {
                val weeklyMeanPlan = arrayOfNulls<DailyMealPlan>(7)
                IntRange(0, 6).forEach { idx ->
                    val day = DayOfWeek.getDayByIntValue(idx)
                    println(day.displayName)
                    weeklyMeanPlan[idx] = generateMealPlanForDay(day)
                    println("Yeah! We planned the meals for ${day.displayName}.")
                    println()
                }

                insertWeeklyMealPlan(weeklyMeanPlan)

                println(weeklyMeanPlan.joinToString(separator = "\n\n") {
                    it.toString()
                })
            }

            ACTION_SAVE -> {
                if (hasMealPlan().not()) {
                    println("Unable to save. Plan your meals first.")
                    continue
                }
                println("Input a filename:")
                val filename = readUserInput()
                saveMealPlanToFile(filename)

                println("Saved!")
            }

            ACTION_EXIT -> {
                println("Bye!")
                break
            }

            else -> {
                continue
            }
        }
    }
}

fun saveMealPlanToFile(filename: String) {
    try {
        val shoppingListMap = getTotalIngredientsNeeded()
        val file = File(filename)
        if(file.exists()) {
            file.delete()
        }
        shoppingListMap.forEach { (ingredient, count) ->
            file.appendText("$ingredient ${if (count > 1) "x${count}" else ""}\n")
        }
    } catch (e: AccessDeniedException) {
        println(e.message)
    } catch (e: Exception) {
        println(e.message)
    }
}

private fun getCategory(): String {
    var category: String
    while (true) {
        println("Which category do you want to print (breakfast, lunch, dinner)?")
        category = readUserInput()
        if (category !in categories) {
            println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
            continue
        }
        break
    }
    return category
}

fun generateMealPlanForDay(day: DayOfWeek): DailyMealPlan {
    val breakfast = generateMealFor(day, "breakfast")
    val lunch = generateMealFor(day, "lunch")
    val dinner = generateMealFor(day, "dinner")

    return DailyMealPlan(day, breakfast, lunch, dinner)
}

fun generateMealFor(day: DayOfWeek, mealType: String): Meal {
    val meal: Meal

    val meals = getMealsByCategory(mealType, shouldBeOrdered = true)
    println(meals.joinToString(separator = "\n") { it.mealName })
    println("Choose the $mealType for ${day.displayName} from the list above:")

    while (true) {
        val selectedMenu = readUserInput()
        val mealFound = meals.find { it.mealName == selectedMenu }
        if (mealFound == null) {
            println("This meal doesn’t exist. Choose a meal from the list above.")
            continue
        }
        meal = mealFound
        break
    }
    return meal
}


fun createMeal(mealId: Int): Meal {
    var mealName: String
    var mealCategory: String
    var ingredients: List<String>

    while (true) {
        println("Which meal do you want to add (breakfast, lunch, dinner)?")
        mealCategory = readUserInput()
        if (mealCategory !in categories) {
            println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
            continue
        }
        break
    }

    while (true) {
        println("Input the meal's name:")
        mealName = readUserInput()
        if (mealName.isEmpty() || mealName.all { it.isLetter() || it.isWhitespace() }.not()) {
            println("Wrong format. Use letters only!")
            continue
        }
        break
    }

    while (true) {
        println("Input the ingredients:")
        ingredients = readUserInput().split(",").map { it.trim() }

        if (ingredients.isEmpty() || ingredients.all { item ->
                item.isNotEmpty() && item.all { it.isWhitespace() || it.isLetter() }
            }.not()) {
            println("Wrong format. Use letters only!")
            continue
        }
        break
    }

    return Meal(mealId, mealCategory, mealName, ingredients)
}

private fun readUserInput() = readln().trim()

