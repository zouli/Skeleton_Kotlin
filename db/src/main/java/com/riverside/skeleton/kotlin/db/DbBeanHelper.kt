package com.riverside.skeleton.kotlin.db

import kotlin.reflect.KClass

object DbBeanHelper {
    private val beanList = mutableListOf<KClass<*>>()

    fun addBean(vararg classes: KClass<*>) {
        classes.filter { !beanList.contains(it) }.let {
            beanList.addAll(it)
        }
    }

    fun removeBean(clazz: KClass<*>) {
        beanList.remove(clazz)
    }

    fun getCreateSql(): List<String> =
        beanList.map { DbAnnotationHelper.getCreateSql(it) }
}