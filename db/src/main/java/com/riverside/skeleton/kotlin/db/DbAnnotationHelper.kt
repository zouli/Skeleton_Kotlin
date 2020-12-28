package com.riverside.skeleton.kotlin.db

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * 数据库注解帮助类 1.0
 *
 * b_e  2020/12/28
 */
@Target(AnnotationTarget.CLASS)
annotation class STable(val name: String = "")

@Target(AnnotationTarget.CLASS)
annotation class SCreateSql(val sql: String)

@Target(AnnotationTarget.FIELD)
annotation class Id(val autoincrement: Boolean = true)

@Target(AnnotationTarget.FIELD)
annotation class Unique

@Target(AnnotationTarget.FIELD)
annotation class Default(val value: String)

@Target(AnnotationTarget.FIELD)
annotation class Check(val value: String)

@Target(AnnotationTarget.FIELD)
annotation class Index(val desc: Boolean = false, val indexName: String = "index_1")

@Target(AnnotationTarget.FIELD)
annotation class Indexes(vararg val value: Index)

/**
 * 类是否含有指定的注解
 */
inline fun <reified T> KClass<*>.getAnnotation(): T? =
    this.annotations.filterIsInstance<T>().firstOrNull()

/**
 * 字段是否含有指定的注解
 */
inline fun <reified T> KClass<*>.fieldHasAnnotation(param: KParameter): T? =
    param.name?.let {
        this.java.getDeclaredField(it).annotations.filterIsInstance<T>().firstOrNull()
    }

/**
 * 字段是否含有指定的注解
 */
inline fun <reified T> KClass<*>.fieldHasAnnotations(param: KParameter): List<T> =
    param.name?.let {
        this.java.getDeclaredField(it).annotations.filterIsInstance<T>()
    } ?: listOf()

/**
 * 取得表名
 */
fun KClass<*>.getTableName(): String =
    this.getAnnotation<STable>()?.name?.orNull() ?: this.simpleName ?: ""

/**
 * 取得关键字
 */
fun KClass<*>.getPrimaryKeys(): Array<Pair<String, Boolean>> =
    this.constructors.first().parameters.mapNotNull { param ->
        this.getId(param)?.let { id -> param.name?.let { field -> Pair(field, id.autoincrement) } }
    }.toTypedArray()

fun KClass<*>.getCheck(param: KParameter): Check? = this.fieldHasAnnotation<Check>(param)
fun KClass<*>.getDefault(param: KParameter): Default? = this.fieldHasAnnotation<Default>(param)
fun KClass<*>.getUnique(param: KParameter): Unique? = this.fieldHasAnnotation<Unique>(param)
fun KClass<*>.getId(param: KParameter): Id? = this.fieldHasAnnotation<Id>(param)
fun KClass<*>.getIndexes(param: KParameter): List<Index> = mutableListOf<Index>().also { indexes ->
    indexes.addAll(this.fieldHasAnnotations(param))
    this.fieldHasAnnotation<Indexes>(param)?.let { indexes.addAll(it.value) }
}

/**
 * 返回本身或null
 */
private fun String.orNull() = if (this.isNotEmpty()) this else null