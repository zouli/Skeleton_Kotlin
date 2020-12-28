package com.riverside.skeleton.kotlin.db

import android.database.Cursor
import com.riverside.skeleton.kotlin.db.DatabaseTypeHelper.toBasicObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * 数据库工具类   1.0
 *
 * b_e  2020/12/28
 */
object DatabaseUtil {
    /**
     * 实例化Bean
     */
    fun <T> toObject(cursor: Cursor, kClazz: KClass<*>, columnName: String = ""): T? {
        if (cursor.isBeforeFirst) cursor.moveToNext()
        if (!cursor.isAfterLast) return cursor.toBasicObject(kClazz, columnName) ?: run {
            val argsMap = mutableListOf<Pair<Class<*>, Any?>>()
            val clazz = kClazz.java as Class<T>
            //Class.getDeclaredFields()取得变量名列表的顺序不稳定，只能通过KClass的取得构造函数取得变量顺序
            val fields = kClazz.constructors.first().parameters
            fields.forEach { field ->
                //反射还需要使用Class完成，需要根据变量名取得Field对象
                field.name?.let { clazz.getDeclaredField(it) }?.let { javaField ->
                    getColumnIndex(cursor, javaField.name).takeIf { it > -1 }?.let { columnIndex ->
                        argsMap.add(
                            javaField.type to
                                    DatabaseTypeHelper.getFieldValue(javaField, cursor, columnIndex)
                        )
                    } ?: Unit.apply {
                        argsMap.add(javaField.type to null)
                    }
                } ?: Unit
            }
            return if (argsMap.isNotEmpty())
                clazz.getDeclaredConstructor(*argsMap.map { it.first }.toTypedArray())
                    .newInstance(*argsMap.map { it.second }.toTypedArray())
            else null
        } else return null
    }

    /**
     * 取得创建表Sql
     */
    fun KClass<*>.getCreateSql(): String =
        this.getAnnotation<SCreateSql>()?.sql ?: buildCreateSql(this)

    /**
     * 取得删除表Sql
     */
    fun KClass<*>.getDropSql(): String = buildDropSql(this)

    /**
     * 取得字段列表
     */
    fun KClass<*>.getFields(): List<KParameter> = this.constructors.first().parameters

    /**
     * 取得字段名列表
     */
    fun KClass<*>.getFieldNames(): List<String> =
        this.getFields().map { param -> param.name?.let { getSnakeCaseName(it) } ?: "" }

    /**
     * 生成Create语句
     */
    private fun buildCreateSql(clazz: KClass<*>): String {
        val keys = clazz.getPrimaryKeys()
        val indexMap = mutableMapOf<String, MutableList<String>>()
        val fields = clazz.getFields().joinToString(", ") { param ->
            val fieldName = param.name?.let { getSnakeCaseName(it) } ?: ""
            val type = DatabaseTypeHelper.getTableType(param.type)
            val isNull = if (param.type.isMarkedNullable) "" else " NOT NULL"
            val primary =
                if (keys.size > 1) "" else
                    keys.firstOrNull { (field, _) -> field == param.name }?.let { (_, hasAuto) ->
                        val autoincrement = if (hasAuto) " AUTOINCREMENT" else ""
                        " PRIMARY KEY$autoincrement"
                    } ?: ""
            val unique = clazz.getUnique(param)?.let { " UNIQUE" } ?: ""
            val default = clazz.getDefault(param)?.let { " DEFAULT ${it.value}" } ?: ""
            val check = clazz.getCheck(param)?.let { " CHECK(${it.value})" } ?: ""
            clazz.getIndexes(param).forEach {
                val field = fieldName + if (it.desc) " DESC" else ""
                if (indexMap.containsKey(it.indexName))
                    indexMap[it.indexName]?.add(field)
                else
                    indexMap[it.indexName] = mutableListOf(field)
            }
            "[$fieldName] $type$isNull$unique$primary$default$check"
        }
        val primaries =
            if (keys.size > 1) ", PRIMARY KEY(${keys.joinToString(", ") { it.first }})" else ""
        val indexes = indexMap.map { (name, fields) ->
            "CREATE INDEX ${clazz.getTableName()}_$name ON ${clazz.getTableName()} (${
            fields.joinToString(", ")});"
        }.joinToString("")
        return "CREATE TABLE [${clazz.getTableName()}] ($fields$primaries);$indexes"
    }

    /**
     * 生成Drop语句
     */
    private fun buildDropSql(clazz: KClass<*>): String =
        "DROP TABLE IF EXISTS [${clazz.getTableName()}]"

    /**
     * 取得字段和值列表
     */
    inline fun <reified T> getFieldValueArray(
        bean: T, hasNull: Boolean = false
    ): Array<Pair<String, Any?>> =
        T::class.java.declaredFields.map { field ->
            field.isAccessible = true
            getSnakeCaseName(field.name) to field.get(bean)
        }.filter { it.second != null || hasNull }.toTypedArray()

    /**
     * 取得列Index
     */
    private fun getColumnIndex(cursor: Cursor, name: String): Int {
        var index = cursor.getColumnIndexIgnoreCase(name)
        if (index == -1) {
            index = cursor.getColumnIndexIgnoreCase(getSnakeCaseName(name))
        }
        return index
    }

    /**
     * 取得蛇形变量名
     */
    fun getSnakeCaseName(name: String) = name.toCharArray().joinToString("") {
        if (it in 'A'..'Z') "_${it + 32}" else it.toString()
    }
}

fun Cursor.getColumnIndexIgnoreCase(columnName: String) = (0 until this.columnCount).firstOrNull {
    this.getColumnName(it).equals(columnName, true)
} ?: -1

inline fun <reified T> Cursor.toObject(columnName: SField = SField.Empty): T? =
    DatabaseUtil.toObject<T>(this, T::class, columnName.toString()).also {
        this.close()
    }

inline fun <reified T> Cursor.toList(columnName: SField = SField.Empty): List<T> {
    val result = mutableListOf<T>()
    while (this.moveToNext()) {
        DatabaseUtil.toObject<T>(this, T::class, columnName.toString())?.let { result.add(it) }
    }
    this.close()
    return result
}
