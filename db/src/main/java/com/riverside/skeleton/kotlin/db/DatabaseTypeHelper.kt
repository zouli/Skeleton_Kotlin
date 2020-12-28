package com.riverside.skeleton.kotlin.db

import android.content.ContentValues
import android.database.Cursor
import com.riverside.skeleton.kotlin.util.converter.DateUtils
import com.riverside.skeleton.kotlin.util.converter.toDate
import com.riverside.skeleton.kotlin.util.converter.toString
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * 数据库类型转换帮助类   1.0
 *
 * b_e  2020/12/28
 */
object DatabaseTypeHelper {
    /**
     * 取得表的类型
     */
    fun getTableType(type: KType) = when (type.classifier) {
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

    /**
     * 取得数据库的值
     */
    fun getFieldValue(javaField: Field, cursor: Cursor, columnIndex: Int): Any? = when {
        cursor.isNull(columnIndex) -> null
        //比较基础类型的时候需要忽略大小写
        eqType(javaField, Byte::class) -> cursor.getInt(columnIndex).toByte()
        eqType(javaField, Short::class) -> cursor.getShort(columnIndex)
        eqType(javaField, Int::class) -> cursor.getInt(columnIndex)
        eqType(javaField, Integer::class) -> cursor.getInt(columnIndex)
        eqType(javaField, Long::class) -> cursor.getLong(columnIndex)
        eqType(javaField, Float::class) -> cursor.getFloat(columnIndex)
        eqType(javaField, Double::class) -> cursor.getDouble(columnIndex)
        eqType(javaField, Boolean::class) -> cursor.getInt(columnIndex) == 1
        eqType(javaField, Date::class) -> cursor.getString(columnIndex)?.toDate(DATE_PATTERN)
        eqType(javaField, String::class) -> cursor.getString(columnIndex)
        else -> toObject(javaField, cursor.getBlob(columnIndex))
    }

    /**
     * 生成ContentValues
     */
    fun Array<out Pair<String, Any?>>.toContentValues() =
        ContentValues().also { contentValues ->
            this.forEach { (key, value) ->
                value?.also {
                    when (it) {
                        is Byte -> contentValues.put(key, it)
                        is Short -> contentValues.put(key, it)
                        is Int -> contentValues.put(key, it)
                        is Long -> contentValues.put(key, it)
                        is Float -> contentValues.put(key, it)
                        is Double -> contentValues.put(key, it)
                        is Boolean -> contentValues.put(key, it)
                        is String -> contentValues.put(key, it)
                        is Date -> contentValues.put(key, it.toString(DATE_PATTERN))
                        else -> contentValues.put(key, toDatabase(it))
                    }
                } ?: contentValues.putNull(key)
            }
        }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun <T> Cursor.toBasicObject(clazz: KClass<*>, columnName: String): T? =
        (if (columnName.isEmpty()) 0 else this.getColumnIndexIgnoreCase(columnName)).let { index ->
            when (clazz) {
                Byte::class -> this.getInt(index).toByte()
                Short::class -> this.getInt(index)
                Int::class -> this.getInt(index)
                Integer::class -> this.getInt(index)
                Long::class -> this.getLong(index)
                Float::class -> this.getFloat(index)
                Double::class -> this.getDouble(index)
                Boolean::class -> this.getInt(index) == 1
                Date::class -> this.getString(index)?.toDate(DATE_PATTERN)
                String::class -> this.getString(index)
                else -> null
            } as T?
        }

    private val converterList = mutableListOf<SFieldTypeConverter>(DefaultTypeConverter())

    fun addTypeConverter(converter: SFieldTypeConverter) {
        if (!converterList.contains(converter)) converterList.add(converter)
    }

    private fun toDatabase(value: Any): ByteArray? {
        for (converter in converterList) {
            converter.toDatabase(value)?.let {
                return it
            }
        }
        return null
    }

    private fun toObject(field: Field, value: ByteArray): Any? {
        for (converter in converterList) {
            converter.toObject(field, value)?.let {
                return it
            }
        }
        return null
    }

    private fun eqType(field: Field, clazz: KClass<*>) =
        field.type.simpleName.equals(clazz.java.simpleName, true)

    const val DATE_PATTERN = DateUtils.DATE_FORMAT_PATTERN2
}

/**
 * 字段类型转换器
 */
interface SFieldTypeConverter {
    fun toDatabase(value: Any): ByteArray?
    fun toObject(field: Field, value: ByteArray): Any?
}

/**
 * 默认转换器
 */
class DefaultTypeConverter : SFieldTypeConverter {
    override fun toDatabase(value: Any): ByteArray? = when (value) {
        is List<*> -> value.joinToString(",").toByteArray()
        is Array<*> -> value.toList().joinToString(",").toByteArray()
        else -> null
    }

    override fun toObject(field: Field, value: ByteArray): Any? =
        when {
            field.type == List::class.java ->
                value.getTypedList((field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>)
            field.type.isArray -> value.getTypedArray(field.type.componentType)
            else -> null
        }

    private fun ByteArray.getTypedList(type: Class<*>) =
        when {
            eqType(type, Byte::class.java) -> this.toAsciiList().map { it.toByte() }
            eqType(type, Short::class.java) -> this.toAsciiList().map { it.toShort() }
            eqType(type, Int::class.java) -> this.toAsciiList().map { it.toInt() }
            eqType(type, Integer::class.java) -> this.toAsciiList().map { it.toInt() }
            eqType(type, Long::class.java) -> this.toAsciiList().map { it.toLong() }
            eqType(type, Float::class.java) -> this.toAsciiList().map { it.toFloat() }
            eqType(type, Double::class.java) -> this.toAsciiList().map { it.toDouble() }
            eqType(type, Boolean::class.java) -> this.toAsciiList().map { it.toBoolean() }
            eqType(type, Date::class.java) -> this.toAsciiList()
                .map { it.toDate(DatabaseTypeHelper.DATE_PATTERN) }
            eqType(type, String::class.java) -> this.toAsciiList()
            else -> null
        }

    private fun ByteArray.getTypedArray(type: Class<*>) =
        when {
            eqType(type, Byte::class.java) -> this.toAsciiList().map { it.toByte() }.toTypedArray()
            eqType(type, Short::class.java) ->
                this.toAsciiList().map { it.toShort() }.toTypedArray()
            eqType(type, Int::class.java) -> this.toAsciiList().map { it.toInt() }.toTypedArray()
            eqType(type, Integer::class.java) ->
                this.toAsciiList().map { it.toInt() }.toTypedArray()
            eqType(type, Long::class.java) -> this.toAsciiList().map { it.toLong() }.toTypedArray()
            eqType(type, Float::class.java) ->
                this.toAsciiList().map { it.toFloat() }.toTypedArray()
            eqType(type, Double::class.java) ->
                this.toAsciiList().map { it.toDouble() }.toTypedArray()
            eqType(type, Boolean::class.java) ->
                this.toAsciiList().map { it.toBoolean() }.toTypedArray()
            eqType(type, Date::class.java) ->
                this.toAsciiList().map { it.toDate(DatabaseTypeHelper.DATE_PATTERN) }.toTypedArray()
            eqType(type, String::class.java) -> this.toAsciiList().toTypedArray()
            else -> null
        }

    private fun eqType(type: Class<*>, clazz: Class<*>) =
        type.simpleName.equals(clazz.simpleName, true)

    private fun ByteArray.toAsciiList() = this.map { it.toChar() }.joinToString("").split(",")
}