package com.riverside.skeleton.kotlin.db

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Target(AnnotationTarget.CLASS)
annotation class STable(val name: String = "")

@Target(AnnotationTarget.CLASS)
annotation class SCreateSql(val sql: String)

@Target(AnnotationTarget.FIELD)
annotation class Id

@Target(AnnotationTarget.FIELD)
annotation class Autoincrement

object DbAnnotationHelper {
    fun getCreateSql(clazz: KClass<*>): String =
        clazz.hasAnnotation<SCreateSql>()?.sql ?: buildCreateSql(clazz)

    private fun buildCreateSql(clazz: KClass<*>): String {
        val tableName = clazz.hasAnnotation<STable>()?.name?.orNull() ?: clazz.simpleName
        val fields = clazz.constructors.first().parameters.joinToString(", ") { param ->
            val fieldName = param.name?.let { getSnakeCaseName(it) } ?: ""
            val type = when (param.type.classifier) {
                Byte::class -> "TINYINT"
                Short::class -> "SMALLINT"
                Int::class -> "INTEGER"
                Long::class -> "BIGINT"
                String::class -> "TEXT"
                Float::class -> "FLOAT"
                Double::class -> "REAL"
                Boolean::class -> "BOOLEAN"
                Date::class -> "DATETIME"
                else -> "BLOB"
            }
            val isNull = if (param.type.isMarkedNullable) "" else " NOT NULL"
            val primary =
                clazz.fieldHasAnnotation<Id>(param)?.let { " PRIMARY KEY" } ?: ""
            val autoincrement =
                clazz.fieldHasAnnotation<Autoincrement>(param)?.let { " AUTOINCREMENT" } ?: ""
            "[$fieldName] $type$isNull$primary$autoincrement"
        }
        return "CREATE TABLE [$tableName] ($fields)"
    }

    fun getSnakeCaseName(name: String) = name.toCharArray().joinToString("") {
        if (it in 'A'..'Z') "_${it + 32}" else it.toString()
    }

    private inline fun <reified T> KClass<*>.hasAnnotation(): T? =
        this.annotations.filterIsInstance<T>().firstOrNull()

    private inline fun <reified T> KClass<*>.fieldHasAnnotation(param: KParameter): T? =
        param.name?.let {
            this.java.getDeclaredField(it).annotations.filterIsInstance<T>().firstOrNull()
        }

    private fun String.orNull() = if (this.isNotEmpty()) this else null
}