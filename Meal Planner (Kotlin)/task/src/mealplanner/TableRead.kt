package mealplanner

import java.sql.SQLException

fun generateMealId(): Int {
    return try {
        val sqlStr = "SELECT COUNT($COL_MEAL_ID) + 1  as $COL_MEAL_ID FROM $TB_MEALS"
        Connection.statement.executeQuery(sqlStr).use { ts ->
            ts.next()
            ts.getInt(COL_MEAL_ID)
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        -1
    }
}

fun getMealsByCategory(category: String, shouldBeOrdered: Boolean = false): List<Meal> {
    return try {
        val sqlStr = """
            SELECT * FROM $TB_MEALS WHERE $COL_MEAL_CATEGORY = '$category'
            ${if (shouldBeOrdered) "ORDER BY $COL_MEAL_NAME" else ""}
        """.trimIndent()
        Connection.statement.executeQuery(sqlStr)
            .use { rs ->
                val meals = mutableListOf<Meal>()
                while (rs.next()) {
                    val name = rs.getString(COL_MEAL_NAME)
                    val mealId = rs.getInt(COL_MEAL_ID)
                    val ingredients = getIngredients(mealId)
                    meals.add(Meal(mealId,category, name, ingredients))
                }
                meals
            }
    } catch (e: SQLException) {
        println(e.message)
        emptyList()
    }
}

fun getIngredients(mealId: Int): List<String> {
    return try {
        Connection.statement.executeQuery("SELECT * FROM $TB_INGREDIENTS WHERE $COL_MEAL_ID = $mealId").use { rs ->
            val ingredients = mutableListOf<String>()
            while (rs.next()) {
                val ingredient = rs.getString(COL_MEAL_INGREDIENTS)
                ingredients.add(ingredient)
            }
            ingredients
        }
    } catch (e: SQLException) {
        println(e.message)
        return emptyList()
    }
}

fun hasMealPlan(): Boolean {
    return try {
        val sqlStr = "SELECT * FROM $TB_PLAN"
        Connection.statement.executeQuery(sqlStr).use { rs ->
            rs.next()
        }
    } catch (e: SQLException) {
        println(e.message)
        false
    }
}


fun getTotalIngredientsNeeded(): Map<String, Int> {
    return try {
        val result = mutableMapOf<String, Int>()
        val sqlStr = """select ${COL_MEAL_INGREDIENTS}, count(*) as count from $TB_INGREDIENTS
                        inner join $TB_PLAN on $TB_PLAN.$COL_MEAL_ID = $TB_INGREDIENTS.$COL_MEAL_ID
                        group by ${COL_MEAL_INGREDIENTS}"""
        Connection.statement.executeQuery(sqlStr).use { rs ->
            while (rs.next()) {
                result[rs.getString(COL_MEAL_INGREDIENTS)] = rs.getInt("count")
            }
            result
        }
    } catch (e: SQLException) {
        println(e.message)
        emptyMap()
    }
}