package com.riverside.skeleton.kotlin.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.riverside.skeleton.kotlin.db.DatabaseUtil.DATE_PATTERN
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.converter.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

fun sqlite(
    database: SQLiteDatabase = DatabaseHelper.defaultDatabase.currentDatabase,
    init: DatabaseDCL.() -> Unit
) {
    GlobalScope.launch(Dispatchers.IO) {
        val databaseDCL = DatabaseDCL(database)
        try {
            databaseDCL.init()
        } catch (e: Exception) {
            SLog.e("", throwable = e)
        } finally {
            databaseDCL.db.close()
        }
    }
}

class DatabaseDCL(val db: SQLiteDatabase) {
    /**
     * 事件
     */
    fun transaction(block: DatabaseDCL.() -> Unit) {
        db.beginTransaction()
        try {
            block()
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            SLog.e("", throwable = e)
        } finally {
            db.endTransaction()
        }
    }

    /**
     * 执行SQL
     */
    fun exec(vararg sql: String) {
        sql.forEach {
            db.execSQL(it)
        }
    }

    /**
     * 执行SQL
     */
    fun exec(sql: List<String>) {
        sql.forEach {
            db.execSQL(it)
        }
    }

    /**
     * 插入（Bean）
     */
    inline fun <reified T> insert(bean: T): Long = insert(
        DatabaseUtil.getTableName(T::class),
        *DatabaseUtil.getFieldValueArray(bean)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> insert(beans: List<T>) =
        beans.forEach { insert(it) }

    /**
     * 插入
     */
    fun insert(table: String, vararg values: Pair<String, Any>): Long {
        val contentValues = ContentValues()
        values.forEach { (key, value) ->
            when (value) {
                is Byte -> contentValues.put(key, value)
                is Short -> contentValues.put(key, value)
                is Int -> contentValues.put(key, value)
                is Long -> contentValues.put(key, value)
                is Float -> contentValues.put(key, value)
                is Double -> contentValues.put(key, value)
                is Boolean -> contentValues.put(key, value)
                is String -> contentValues.put(key, value)
                is Date -> contentValues.put(key, value.toString(DATE_PATTERN))
                //TODO:这里需要重新弄
                is List<*> -> contentValues.put(key, value.joinToString(","))
                else -> contentValues.put(key, value.toString().toByteArray())
            }
        }
        return db.insertOrThrow(table, null, contentValues)
    }

    /**
     * 查询
     */
    @SuppressLint("Recycle")
    inline fun <reified T> select(
        sql: String, selectionArgs: Array<String> = arrayOf()
    ): List<T> = db.rawQuery(sql, selectionArgs).toList()

    /**
     * 查询(DCL)
     */
    @SuppressLint("Recycle")
    inline fun <reified T> select(init: SelectBuilder.() -> Unit = {}): List<T> {
        val selectBuilder = SelectBuilder()
        selectBuilder.init()
        return db.query(
            selectBuilder.distinct,
            DatabaseUtil.getTableName(T::class), arrayOf<String>(),
            selectBuilder.selection, selectBuilder.selectionArgs,
            selectBuilder.groupBy, selectBuilder.having,
            selectBuilder.orderBy,
            selectBuilder.limit
        ).toList()
    }

    /**
     * 查询构造类
     */
    inner class SelectBuilder {
        var distinct = false
        var selection = ""
        var selectionArgs = arrayOf<String>()
        var groupBy = ""
        var having = ""
        var orderBy = ""
        var limit = ""

        fun where(selection: String, vararg args: Any) {
            this.selection = selection
            this.selectionArgs = args.map { it.toString() }.toTypedArray()
        }

        fun where(init: WhereBuilder.() -> Unit) {
            val whereBuilder = WhereBuilder()
            whereBuilder.init()
            this.selection = whereBuilder.selection
            this.selectionArgs = whereBuilder.selectionArgs.map { it.toString() }.toTypedArray()
        }

        fun limit(page: Int, size: Int) {
            limit = "${page - 1},$size"
        }
    }

    /**
     * Where构造器
     */
    inner class WhereBuilder {
        var selection = ""
        var selectionArgs = mutableListOf<Any>()

        infix fun String.lt(value: Any): String = setArg(this, "<", value)
        infix fun String.le(value: Any): String = setArg(this, "<=", value)
        infix fun String.gt(value: Any): String = setArg(this, ">", value)
        infix fun String.ge(value: Any): String = setArg(this, ">=", value)
        infix fun String.ne(value: Any): String = setArg(this, "<>", value)
        infix fun String.eq(value: Any): String = setArg(this, "=", value)
        infix fun String.In(value: Any): String = setArg(this, "IN", value)
        infix fun String.notIn(value: Any): String = setArg(this, "NOT IN", value)

        fun and(first: String, second: Any): String =
            "($first AND $second)".apply { selection = this }

        fun or(first: String, second: Any): String =
            "($first OR $second)".apply { selection = this }

        private fun setArg(field: String, operator: String, value: Any): String {
            selectionArgs.add(value)
            return "$field $operator ?".apply { if (selection.isEmpty()) selection = this }
        }
    }
}