package mealplanner

import java.sql.SQLException

const val TB_MEALS = "meals"
const val COL_MEAL_CATEGORY = "category"
const val COL_MEAL_NAME = "meal"

const val COL_MEAL_ID = "meal_id"

const val TB_INGREDIENTS = "ingredients"
const val COL_MEAL_INGREDIENTS = "ingredient"
const val COL_INGREDIENT_ID = "ingredient_id"

const val TB_PLAN = "plan"
const val COL_MEAL_OPTION = "meal_option"

private val createMealsTableSql = """create table if not exists $TB_MEALS (
        $COL_MEAL_ID INTEGER,
        $COL_MEAL_CATEGORY TEXT,
        $COL_MEAL_NAME TEXT,
        primary key ($COL_MEAL_ID, $COL_MEAL_CATEGORY, $COL_MEAL_NAME)
    )""".trimMargin()

private val createIngredientsTableSql = """create table if not exists $TB_INGREDIENTS (
        $COL_MEAL_INGREDIENTS TEXT,
        $COL_INGREDIENT_ID INTEGER,
        $COL_MEAL_ID INTEGER,        
        foreign key ($COL_MEAL_ID) references $TB_MEALS($COL_MEAL_ID)        
    )""".trimMargin()

private val createMealPlanTableSql = """create table if not exists $TB_PLAN (
        $COL_MEAL_OPTION TEXT,
        $COL_MEAL_CATEGORY TEXT,
        $COL_MEAL_ID INTEGER,        
        foreign key ($COL_MEAL_ID) references $TB_MEALS($COL_MEAL_ID)
    )""".trimMargin()

fun createMealTable() {
    try {
        Connection.statement.run {
            executeUpdate(createMealsTableSql)
        }
    } catch (e: SQLException) {
        println(e.message)
    }
}

fun createIngredientTable() {
    try {
        Connection.statement.run {
            executeUpdate(createIngredientsTableSql)
        }
    } catch (e: SQLException) {
        println(e.message)
    }
}

fun createMealPlanTable() {
    try {
        Connection.statement.run {
            executeUpdate(createMealPlanTableSql)
        }
    } catch (e: SQLException) {
        println(e.message)
    }
}