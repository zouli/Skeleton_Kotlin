package com.riverside.skeleton.kotlin.db

import android.database.Cursor
import com.riverside.skeleton.kotlin.util.converter.DateUtils
import com.riverside.skeleton.kotlin.util.converter.toDate
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass

object DatabaseUtil {
    /**
     * 实例化Bean
     */
    fun <T> toObject(cursor: Cursor, kClazz: KClass<*>): T? {
        if (cursor.isBeforeFirst) cursor.moveToNext()
        if (!cursor.isAfterLast) {
            val argsMap = mutableMapOf<Class<*>, Any?>()
            val clazz = kClazz.java as Class<T>
            //Class.getDeclaredFields()取得变量名列表的顺序不稳定，只能通过KClass的取得构造函数取得变量顺序
            val fields = kClazz.constructors.first().parameters
            fields.forEach { field ->
                //反射还需要使用Class完成，需要根据变量名取得Field对象
                field.name?.let { clazz.getDeclaredField(it) }?.let { javaField ->
                    getColumnIndex(cursor, javaField.name).takeIf { it > -1 }?.let { columnIndex ->
                        //比较基础类型的时候需要忽略大小写
                        //TODO:考虑把判断抽象出来，把List的情况单独抽象出来
                        when {
                            cursor.isNull(columnIndex) -> argsMap[javaField.type] = null
                            eqType(javaField, Byte::class) ->
                                argsMap[javaField.type] = cursor.getInt(columnIndex).toByte()
                            eqType(javaField, Short::class) ->
                                argsMap[javaField.type] = cursor.getShort(columnIndex)
                            eqType(javaField, Int::class) ->
                                argsMap[javaField.type] = cursor.getInt(columnIndex)
                            eqType(javaField, Integer::class) ->
                                argsMap[javaField.type] = cursor.getInt(columnIndex)
                            eqType(javaField, Long::class) ->
                                argsMap[javaField.type] = cursor.getLong(columnIndex)
                            eqType(javaField, Float::class) ->
                                argsMap[javaField.type] = cursor.getFloat(columnIndex)
                            eqType(javaField, Double::class) ->
                                argsMap[javaField.type] = cursor.getDouble(columnIndex)
                            eqType(javaField, Boolean::class) ->
                                argsMap[javaField.type] = cursor.getInt(columnIndex) == 1
                            eqType(javaField, Date::class) ->
                                argsMap[javaField.type] =
                                    cursor.getString(columnIndex)?.toDate(DATE_PATTERN)
                            //TODO:这里需要重新弄
                            javaField.genericType.toString() == "java.util.List<java.lang.String>" ->
                                argsMap[javaField.type] =
                                    cursor.getString(columnIndex)?.split(",")?.toList()
                            else -> argsMap[javaField.type] = cursor.getString(columnIndex)
                        }
                    }
                } ?: Unit
            }
            return if (argsMap.isNotEmpty())
                clazz.getDeclaredConstructor(*argsMap.keys.toTypedArray())
                    .newInstance(*argsMap.values.toTypedArray())
            else null
        } else return null
    }

    fun KClass<*>.getCreateSql(): String =
        this.getAnnotation<SCreateSql>()?.sql ?: buildCreateSql(this)

    /**
     * 生成Create语句
     */
    private fun buildCreateSql(clazz: KClass<*>): String {
        val keys = clazz.getPrimaryKeys()
        val fields = clazz.constructors.first().parameters.joinToString(", ") { param ->
            val fieldName = param.name?.let { getSnakeCaseName(it) } ?: ""
            //TODO:把类型判断抽象出来
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
                if (keys.size > 1) "" else
                    keys.firstOrNull { (field, _) -> field == param.name }?.let { (_, hasAuto) ->
                        val autoincrement = if (hasAuto) " AUTOINCREMENT" else ""
                        " PRIMARY KEY$autoincrement"
                    } ?: ""
            val unique = clazz.getUnique(param)?.let { " UNIQUE" } ?: ""
            val default = clazz.getDefault(param)?.let { " DEFAULT ${it.value}" } ?: ""
            val check = clazz.getCheck(param)?.let { " CHECK(${it.value})" } ?: ""
            "[$fieldName] $type$isNull$unique$primary$default$check"
        }
        val primaries =
            if (keys.size > 1) ", PRIMARY KEY(${keys.joinToString(", ") { it.first }})" else ""
        return "CREATE TABLE [${clazz.getTableName()}] ($fields$primaries)"
    }

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

    private fun eqType(field: Field, clazz: KClass<*>) =
        field.type.simpleName.equals(clazz.java.simpleName, true)

    const val DATE_PATTERN = DateUtils.DATE_FORMAT_PATTERN2
}

fun Cursor.getColumnIndexIgnoreCase(columnName: String) = (0 until this.columnCount).firstOrNull {
    this.getColumnName(it).equals(columnName, true)
} ?: -1

inline fun <reified T> Cursor.toObject(): T? = DatabaseUtil.toObject(this, T::class)

inline fun <reified T> Cursor.toList(): List<T> {
    val result = mutableListOf<T>()
    while (this.moveToNext()) {
        this.toObject<T>()?.let { result.add(it) }
    }
    this.close()
    return result
}
