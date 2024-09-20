package mealplanner

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

object Connection {
    private const val DB_CONNECTION_STR = "jdbc:sqlite:meals.db"
    private val INSTANCE: Connection = DriverManager.getConnection(DB_CONNECTION_STR)

    val statement: Statement
        get() = INSTANCE.createStatement()

    fun close() {
        INSTANCE.close()
    }
}
