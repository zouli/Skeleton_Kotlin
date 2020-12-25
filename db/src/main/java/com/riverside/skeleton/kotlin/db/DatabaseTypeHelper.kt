package com.riverside.skeleton.kotlin.db

/**
 * 字段类
 */
class SField(val name: String, pre: String = "") {
    private var alias = ""
    private var prefix: String = pre
        get() = if (field.isNotEmpty()) "$field." else field

    private var order: String = ""

    /**
     * 设置别名
     */
    infix fun As(alias: String): SField = this.apply {
        this@SField.alias = " AS $alias"
    }

    /**
     * 设置前缀
     */
    fun prefix(pre: String): SField = this.apply { prefix = pre }

    /**
     * 倒序
     */
    fun desc(): SField = this.apply { order = " DESC" }

    /**
     * 取得含排序的字段名
     */
    fun getOrder() = toString() + order

    fun notEmpty() = name.isNotEmpty()

    override fun toString(): String = if (Regex("""[A-Za-z]+""").matches(name)) {
        "$prefix${DatabaseUtil.getSnakeCaseName(name)}${alias}"
    } else "$prefix$name"

    companion object {
        /**
         * 空对象
         */
        val Empty = SField("")
    }
}