package mealplanner

import java.sql.SQLException

fun insertMeal(meal: Meal): Int {
    try {
        val insertSQL = createMealInsertSql(meal)
        return Connection.statement.executeUpdate(insertSQL)
    } catch (e: SQLException) {
        println(e.message)
        return -1
    }
}

fun insertIngredients(meal: Meal): Int {
    try {
        val insertSQL = createMealIngredientsInsertSql(meal)
        return Connection.statement.executeUpdate(insertSQL)
    } catch (e: SQLException) {
        println(e.message)
        return -1
    }
}


fun insertWeeklyMealPlan(weeklyMealPlan: Array<DailyMealPlan?>) {
    try {
//        statement.executeUpdate("DELETE FROM $TB_PLAN")
        val insertSQL = createWeeklyMealPlanInsertSql(weeklyMealPlan.filterNotNull())
        Connection.statement.executeUpdate(insertSQL)
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

private fun createMealInsertSql(meal: Meal): String {
    return "INSERT INTO $TB_MEALS($COL_MEAL_ID, $COL_MEAL_CATEGORY, $COL_MEAL_NAME) values ( ${meal.mealId}, '${meal.category}', '${meal.mealName}')"
}

private fun createMealIngredientsInsertSql(meal: Meal): String {
    return """INSERT INTO $TB_INGREDIENTS($COL_MEAL_INGREDIENTS, $COL_INGREDIENT_ID, $COL_MEAL_ID) values
         ${meal.ingredients.joinToString { "('${it}', ${meal.ingredients.indexOf(it)}, ${meal.mealId})" }}
        """.trimMargin()
}

private fun createWeeklyMealPlanInsertSql(weeklyMealPlan: List<DailyMealPlan>): String {
    return with(StringBuilder()) {
        append("INSERT INTO $TB_PLAN($COL_MEAL_OPTION, $COL_MEAL_CATEGORY, $COL_MEAL_ID) values ")
        weeklyMealPlan.forEach  { dailyMealPlan ->
            append("('${dailyMealPlan.breakfast.mealName}', 'breakfast' , ${dailyMealPlan.breakfast.mealId}),")
            append("('${dailyMealPlan.lunch.mealName}', 'lunch', ${dailyMealPlan.lunch.mealId}),")
            append("('${dailyMealPlan.dinner.mealName}', 'dinner', ${dailyMealPlan.dinner.mealId})")
            if(weeklyMealPlan.indexOf(dailyMealPlan) != weeklyMealPlan.lastIndex) {
                append(",")
            }
        }
        this.toString()
    }
}
