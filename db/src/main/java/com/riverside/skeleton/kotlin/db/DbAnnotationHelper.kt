package com.riverside.skeleton.kotlin.db

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Target(AnnotationTarget.CLASS)
annotation class STable(val name: String = "")

@Target(AnnotationTarget.CLASS)
annotation class SCreateSql(val sql: String)

@Target(AnnotationTarget.FIELD)
annotation class Id(val autoincrement: Boolean = true)

@Target(AnnotationTarget.FIELD)
annotation class Unique

inline fun <reified T> KClass<*>.hasAnnotation(): T? =
    this.annotations.filterIsInstance<T>().firstOrNull()

inline fun <reified T> KClass<*>.fieldHasAnnotation(param: KParameter): T? =
    param.name?.let {
        this.java.getDeclaredField(it).annotations.filterIsInstance<T>().firstOrNull()
    }
