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
        DatabaseUtil.getTableName(T::class), *DatabaseUtil.getFieldValueArray(bean)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> insert(beans: List<T>) = beans.forEach { insert(it) }

    /**
     * 插入
     */
    fun insert(table: String, vararg values: Pair<String, Any?>): Long = if (db.inTransaction()) {
        db.insertOrThrow(table, null, values.toContentValues())
    } else {
        db.insert(table, null, values.toContentValues())
    }

    /**
     * 查询
     */
    @SuppressLint("Recycle")
    inline fun <reified T> select(sql: String, selectionArgs: Array<String> = arrayOf()): List<T> =
        db.rawQuery(sql, selectionArgs).toList()

    /**
     * 查询(DCL)
     */
    @SuppressLint("Recycle")
    inline fun <reified T> select(init: SelectBuilder.() -> Unit = {}): List<T> =
        SelectBuilder().run {
            init()
            tableName = DatabaseUtil.getTableName(T::class)

            db.query(
                distinct, tableName, columns,
                selection, selectionArgs, groupBy, having, orderBy, limit
            ).toList()
        }

    /**
     * 取得查询Sql
     */
    @SuppressLint("Recycle")
    inline fun <reified T> selectSql(init: SelectBuilder.() -> Unit = {}): SelectBuilder =
        SelectBuilder().apply {
            init()
            tableName = DatabaseUtil.getTableName(T::class)
        }

    /**
     * 删除(DCL)
     */
    inline fun <reified T> delete(init: ConditionsBuilder.() -> Unit): Int =
        ConditionsBuilder().run {
            init()

            delete(
                DatabaseUtil.getTableName(T::class),
                condition, conditionArgs.map { it.toString() }.toTypedArray()
            )
        }

    /**
     * 删除
     */
    fun delete(tableName: String, where: String, whereArg: Array<String>): Int =
        db.delete(tableName, where, whereArg)

    /**
     * 更新(Bean)
     */
    inline fun <reified T> update(
        bean: T, updateNull: Boolean = false,
        init: UpdateBuilder.() -> Unit
    ): Int =
        UpdateBuilder().run {
            init()

            update(
                DatabaseUtil.getTableName(T::class),
                *DatabaseUtil.getFieldValueArray(bean, updateNull),
                where = selection, whereArg = selectionArgs
            )
        }

    /**
     * 更新(DCL)
     */
    inline fun <reified T> update(init: UpdateBuilder.() -> Unit): Int =
        UpdateBuilder().run {
            init()

            update(
                DatabaseUtil.getTableName(T::class), *values,
                where = selection, whereArg = selectionArgs
            )
        }

    /**
     * 更新
     */
    fun update(
        tableName: String, vararg values: Pair<String, Any?>, where: String, whereArg: Array<String>
    ): Int =
        db.update(tableName, values.toContentValues(), where, whereArg)

    /**
     * 生成ContentValues
     */
    private fun Array<out Pair<String, Any?>>.toContentValues() =
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
                        //TODO:这里需要重新弄
                        is List<*> -> contentValues.put(key, it.joinToString(","))
                        else -> contentValues.put(key, it.toString().toByteArray())
                    }
                } ?: contentValues.putNull(key)
            }
        }

    /**
     * 查询构造类
     */
    inner class SelectBuilder {
        var tableName = ""
        var columns = arrayOf<String>()
            private set
        var distinct = false
        var selection = ""
            private set
        var selectionArgs = arrayOf<String>()
            private set
        var groupBy = ""
            private set
        var having = ""
            private set
        var orderBy = ""
            private set
        var limit = ""
            private set

        fun column(vararg column: String) {
            this.columns = column.toList().toTypedArray()
        }

        fun where(selection: String, vararg args: Any) {
            this.selection = selection
            this.selectionArgs = args.map { it.toString() }.toTypedArray()
        }

        fun where(init: ConditionsBuilder.() -> Unit) {
            val whereBuilder = ConditionsBuilder()
            whereBuilder.init()
            this.selection = whereBuilder.condition
            this.selectionArgs = whereBuilder.conditionArgs.map { it.toString() }.toTypedArray()
        }

        fun groupBy(groupBy: String, having: String = "") {
            this.groupBy = groupBy
            this.having = having
        }

        fun groupBy(vararg column: String, init: ConditionsBuilder.() -> Unit) {
            val havingBuilder = ConditionsBuilder()
            havingBuilder.init()
            this.groupBy = column.joinToString(", ") { "[$it]" }
            this.having = havingBuilder.condition
            this.selectionArgs =
                this.selectionArgs.union(havingBuilder.conditionArgs.map { it.toString() })
                    .toTypedArray()
        }

        fun orderBy(vararg column: String) {
            this.orderBy = column.joinToString(", ")
        }

        fun limit(page: Int, size: Int) {
            limit = "${page - 1},$size"
        }

        fun getSql() =
            "SELECT${if (distinct) " DISTINCT" else ""} ${
            if (columns.isEmpty()) "*" else columns.joinToString(", ")} FROM [$tableName]${
            c("WHERE", selection)}${
            c("GROUP BY", groupBy)}${c("HAVING", having)}${
            c("ORDER BY", orderBy)}${c("LIMIT", limit)}"

        private fun c(name: String, clause: String): String =
            if (clause.isNotEmpty()) " $name $clause" else ""

        fun String.desc(): String = "$this DESC"
    }

    /**
     * 更新构造类
     */
    inner class UpdateBuilder {
        var values = arrayOf<Pair<String, Any>>()
            private set
        var selection = ""
            private set
        var selectionArgs = arrayOf<String>()
            private set

        fun values(vararg values: Pair<String, Any>) {
            this.values = values.toList().toTypedArray()
        }

        fun where(selection: String, vararg args: Any) {
            this.selection = selection
            this.selectionArgs = args.map { it.toString() }.toTypedArray()
        }

        fun where(init: ConditionsBuilder.() -> Unit) {
            val whereBuilder = ConditionsBuilder()
            whereBuilder.init()
            this.selection = whereBuilder.condition
            this.selectionArgs = whereBuilder.conditionArgs.map { it.toString() }.toTypedArray()
        }
    }

    /**
     * Conditions构造器
     */
    inner class ConditionsBuilder {
        var condition = ""
        var conditionArgs = mutableListOf<Any>()

        infix fun String.lt(value: Any): String = getExpression(this, "<", value)
        infix fun String.le(value: Any): String = getExpression(this, "<=", value)
        infix fun String.gt(value: Any): String = getExpression(this, ">", value)
        infix fun String.ge(value: Any): String = getExpression(this, ">=", value)
        infix fun String.ne(value: Any): String = getExpression(this, "<>", value)
        infix fun String.eq(value: Any): String = getExpression(this, "=", value)

        fun and(first: String, second: Any, vararg other: Any): String =
            getClauses("AND", first, second, *other)

        fun String.between(start: Any, end: Any): String =
            getExpression(this, "BETWEEN", start, "AND", end)

        fun String.notBetween(start: Any, end: Any): String =
            getExpression(this, "NOT BETWEEN", start, "AND", end)

        fun exists(init: SelectBuilder.() -> SelectBuilder): String = SelectBuilder().init().let {
            getSubQuery("", "EXISTS", it.getSql(), it.selectionArgs)
        }

        fun notExists(init: SelectBuilder.() -> SelectBuilder): String =
            SelectBuilder().init().let {
                getSubQuery("", "NOT EXISTS", it.getSql(), it.selectionArgs)
            }

        fun exists(sql: String, vararg sqlArgs: Any): String =
            getSubQuery("", "EXISTS", sql, sqlArgs)

        fun notExists(sql: String, vararg sqlArgs: Any): String =
            getSubQuery("", "NOT EXISTS", sql, sqlArgs)

        fun String.In(vararg value: Any): String = getInValues(this, "IN", value)
        fun String.notIn(vararg value: Any): String = getInValues(this, "NOT IN", value)
        fun String.In(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(this, "EXISTS", sql, sqlArgs)

        fun String.notIn(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(this, "NOT EXISTS", sql, sqlArgs)

        fun String.In(init: SelectBuilder.() -> SelectBuilder): String =
            SelectBuilder().init().let {
                getSubQuery(this, "IN", it.getSql(), it.selectionArgs)
            }

        fun String.notIn(init: SelectBuilder.() -> SelectBuilder): String =
            SelectBuilder().init().let {
                getSubQuery(this, "NOT IN", it.getSql(), it.selectionArgs)
            }

        infix fun String.like(pattern: String): String = getExpression(this, "LIKE '$pattern'")
        infix fun String.notLike(pattern: String): String =
            getExpression(this, "NOT LIKE '$pattern'")

        infix fun String.glob(pattern: String): String = getExpression(this, "GLOB '$pattern'")
        infix fun String.notGlob(pattern: String): String =
            getExpression(this, "NOT GLOB '$pattern'")

        fun or(first: String, second: Any, vararg other: Any): String =
            getClauses("OR", first, second, *other)

        fun String.isNull(): String = getExpression(this, "IS NULL")
        fun String.isNotNull(): String = getExpression(this, "IS NOT NULL")
        infix fun String.Is(value: Any): String = getExpression(this, "IS", value)
        infix fun String.isNot(value: Any): String = getExpression(this, "IS NOT", value)

        private fun getClauses(operator: String, vararg values: Any): String =
            "(${values.joinToString(" $operator ")})".apply { condition = this }

        private fun getExpression(
            field: String,
            operator: String, value: Any? = null, operator2: String = "", value2: Any? = null
        ): String = "${fo(field, operator)}${if (value == null) "" else " ?"}${
        if (operator2.isNotEmpty()) " $operator2${if (value2 == null) "" else " ?"}" else ""}".apply {
            value?.let { conditionArgs.add(it) }
            value2?.let { conditionArgs.add(it) }
            if (condition.isEmpty()) condition = this
        }

        private fun getInValues(field: String, operator: String, value: Array<out Any>): String =
            "${fo(field, operator)} (${value.joinToString(" , ") { "?" }})".apply {
                conditionArgs.addAll(value)
                condition = this
            }

        private fun getSubQuery(
            field: String, operator: String, sql: String, sqlArgs: Array<out Any>
        ): String = "${fo(field, operator)} ($sql)".apply {
            conditionArgs.addAll(sqlArgs)
            condition = this
        }

        private fun fo(field: String, operator: String) =
            "[${DatabaseUtil.getSnakeCaseName(field)}] $operator"
    }
}