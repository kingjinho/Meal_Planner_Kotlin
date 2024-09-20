package mealplanner

data class Meal(
    val mealId: Int,
    val category: String,
    val mealName: String,
    val ingredients: List<String>,
) {

    override fun toString(): String {
        return """
Name: $mealName
Ingredients:
${ingredients.joinToString(separator = "\n")}
    """.trimIndent()
    }
}

data class DailyMealPlan(
    val day: DayOfWeek,
    val breakfast: Meal,
    val lunch: Meal,
    val dinner: Meal,
) {
    override fun toString(): String {
        return """
            ${day.displayName}
            Breakfast: ${breakfast.mealName}
            Lunch: ${lunch.mealName}
            Dinner: ${dinner.mealName}
        """.trimIndent()
    }
}

