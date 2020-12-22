package com.riverside.skeleton.kotlin.db

import android.database.Cursor
import com.riverside.skeleton.kotlin.util.converter.toDate
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass


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

object DatabaseUtil {
    private fun eqType(field: Field, clazz: KClass<*>) =
        field.type.simpleName.equals(clazz.java.simpleName, true)

    /**
     * 实例化Bean
     */
    fun <T> toObject(cursor: Cursor, kClazz: KClass<*>): T? {
        if (cursor.isBeforeFirst) cursor.moveToNext()
        if (!cursor.isAfterLast) {
            val argsMap = mutableMapOf<Class<*>, Any>()
            val clazz = kClazz.java as Class<T>
            //Class.getDeclaredFields()取得变量名列表的顺序不稳定，只能通过KClass的取得构造函数取得变量顺序
            val fields = kClazz.constructors.first().parameters
            fields.forEach { field ->
                //反射还需要使用Class完成，需要根据变量名取得Field对象
                field.name?.let { clazz.getDeclaredField(it) }?.let { javaField ->
                    getColumnIndex(cursor, javaField.name).takeIf { it > -1 }?.let { columnIndex ->
                        //比较基础类型的时候需要忽略大小写
                        when {
                            eqType(javaField, Byte::class) ->
                                argsMap[javaField.type] = cursor.getInt(columnIndex).toByte()
                            eqType(javaField, Short::class) ->
                                argsMap[javaField.type] = cursor.getShort(columnIndex)
                            eqType(javaField, Int::class) ->
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
                                    cursor.getString(columnIndex).toDate("yyyy-MM-dd HH:mm:ss")
                            javaField.genericType.toString() == "java.util.List<java.lang.String>" ->
                                argsMap[javaField.type] =
                                    cursor.getString(columnIndex).split(",").toList()
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

    /**
     * 取得列Index
     */
    private fun getColumnIndex(cursor: Cursor, name: String): Int {
        var index = cursor.getColumnIndexIgnoreCase(name)
        if (index == -1) {
            index = cursor.getColumnIndexIgnoreCase(DbAnnotationHelper.getSnakeCaseName(name))
        }
        return index
    }
}