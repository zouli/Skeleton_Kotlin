package com.riverside.skeleton.kotlin.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
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
            SLog.e(e)
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
            SLog.e(e)
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
        T::class.getTableName(), *DatabaseUtil.getFieldValueArray(bean)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> insert(beans: List<T>) = beans.forEach { insert(it) }

    /**
     * 插入
     */
    fun insert(tableName: String, vararg values: Pair<String, Any?>): Long =
        if (db.inTransaction()) {
            db.insertOrThrow(tableName, null, values.toContentValues())
        } else {
            db.insert(tableName, null, values.toContentValues())
        }.apply {
            SLog.i("INSERT INTO $tableName VALUES ${values.contentToString()}")
        }

    /**
     * 插入（Bean）
     */
    inline fun <reified T> replace(bean: T): Long = replace(
        T::class.getTableName(), *DatabaseUtil.getFieldValueArray(bean)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> replace(beans: List<T>) = beans.forEach { replace(it) }

    /**
     * 插入或更新
     */
    fun replace(tableName: String, vararg values: Pair<String, Any?>): Long =
        db.insertWithOnConflict(tableName, null, values.toContentValues(), CONFLICT_REPLACE).apply {
            SLog.i("INSERT OR REPLACE INTO $tableName VALUES ${values.contentToString()}")
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
    inline fun <reified T> select(
        alias: String = "", init: SelectBuilder.() -> Unit = {}
    ): List<T> =
        SelectBuilder(alias).run {
            init()
            tableName = T::class.getTableName()

            SLog.i(createSql())
            SLog.i(selectionArgs)

            if (join.isNotEmpty())
                select(createSql(), selectionArgs)
            else
                db.query(
                    distinct, tableName, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit
                ).toList()
        }

    /**
     * 取得子查询
     */
    inline fun <reified T> subSelect(
        alias: String = "", init: SelectBuilder.() -> Unit = {}
    ): SelectBuilder =
        SelectBuilder(alias).apply {
            init()
            tableName = T::class.getTableName()

            SLog.i(createSql())
            SLog.i(selectionArgs)
        }

    /**
     * 交叉连接
     */
    inline fun <reified T> crossJoin(
        alias: String = "", noinline init: ConditionsBuilder.() -> Unit = {}
    ): JoinBuilder = JoinBuilder("CROSS", alias).apply {
        tableName = T::class.getTableName()
        on(init)
    }

    /**
     * 内连接
     */
    inline fun <reified T> innerJoin(
        alias: String = "", noinline init: ConditionsBuilder.() -> Unit = {}
    ): JoinBuilder = JoinBuilder("INNER", alias).apply {
        tableName = T::class.getTableName()
        on(init)
    }

    /**
     * 左连接
     */
    inline fun <reified T> leftJoin(
        alias: String = "", noinline init: ConditionsBuilder.() -> Unit = {}
    ): JoinBuilder = JoinBuilder("LEFT", alias).apply {
        tableName = T::class.getTableName()
        on(init)
    }

    /**
     * 删除(DCL)
     */
    inline fun <reified T> delete(init: ConditionsBuilder.() -> Unit = {}): Int =
        ConditionsBuilder().run {
            init()

            delete(
                T::class.getTableName(),
                condition, conditionArgs.map { it.toString() }.toTypedArray()
            )
        }

    /**
     * 删除
     */
    fun delete(tableName: String, where: String = "", whereArg: Array<String> = arrayOf()): Int =
        db.delete(tableName, where, whereArg).apply {
            SLog.i("DELETE FROM $tableName WHERE $where")
            SLog.i(whereArg)
        }

    /**
     * 更新(Bean)
     */
    inline fun <reified T> update(
        bean: T, updateNull: Boolean = false,
        init: UpdateBuilder.() -> Unit = {}
    ): Int =
        UpdateBuilder().run {
            init()

            update(
                T::class.getTableName(),
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
                T::class.getTableName(), *values,
                where = selection, whereArg = selectionArgs
            )
        }

    /**
     * 更新
     */
    fun update(
        tableName: String, vararg values: Pair<String, Any?>, where: String, whereArg: Array<String>
    ): Int =
        db.update(tableName, values.toContentValues(), where, whereArg).apply {
            SLog.i("UPDATE $tableName SET ${values.contentToString()} WHERE $where")
            SLog.i(whereArg)
        }

    /**
     * Union
     */
    inline fun <reified T> union(
        select1: () -> SelectBuilder, select2: () -> SelectBuilder
    ): List<T> {
        val selectBuilder1 = select1()
        val selectBuilder2 = select2()

        return select(
            sql = "${selectBuilder1.createSql()} UNION ${selectBuilder2.createSql()}",
            selectionArgs = arrayOf(*selectBuilder1.selectionArgs, *selectBuilder2.selectionArgs)
        )
    }

    /**
     * Union All
     */
    inline fun <reified T> unionAll(
        select1: () -> SelectBuilder, select2: () -> SelectBuilder
    ): List<T> {
        val selectBuilder1 = select1()
        val selectBuilder2 = select2()

        return select(
            sql = "${selectBuilder1.createSql()} UNION ALL ${selectBuilder2.createSql()}",
            selectionArgs = arrayOf(*selectBuilder1.selectionArgs, *selectBuilder2.selectionArgs)
        )
    }

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
    inner class SelectBuilder(private val alias: String = "") {
        var tableName = ""
            get() = if (alias.isNotEmpty()) "$field AS $alias" else field
        var columns = arrayOf<String>()
            private set
        var distinct = false
        var join = ""
            private set
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

        fun column(vararg column: SField) {
            this.columns = column.map { it.toString() }.toTypedArray()
        }

        fun join(vararg init: JoinBuilder) = init.forEach { join ->
            this.join += "$join"
            this.selectionArgs =
                this.selectionArgs.union(join.onArgs.map { it }).toTypedArray()
        }

        fun where(selection: String, vararg args: Any) {
            this.selection = selection
            this.selectionArgs = args.map { it.toString() }.toTypedArray()
        }

        fun where(init: ConditionsBuilder.() -> Unit) {
            val whereBuilder = ConditionsBuilder(alias)
            whereBuilder.init()
            this.selection = whereBuilder.condition
            this.selectionArgs =
                this.selectionArgs.union(whereBuilder.conditionArgs.map { it.toString() })
                    .toTypedArray()
        }

        fun groupBy(groupBy: String, having: String = "") {
            this.groupBy = groupBy
            this.having = having
        }

        fun groupBy(vararg column: SField, init: ConditionsBuilder.() -> Unit = {}) {
            val havingBuilder = ConditionsBuilder(alias)
            havingBuilder.init()
            this.groupBy = column.joinToString(", ") { it.toString() }
            this.having = havingBuilder.condition
            this.selectionArgs =
                this.selectionArgs.union(havingBuilder.conditionArgs.map { it.toString() })
                    .toTypedArray()
        }

        fun orderBy(vararg column: SField) {
            this.orderBy =
                column.joinToString(", ") { it.getOrder() }
        }

        fun limit(page: Int, size: Int) {
            limit = "${page - 1},$size"
        }

        /**
         * 生成完成SQL文
         */
        fun createSql() =
            "SELECT${if (distinct) " DISTINCT" else ""} ${
            if (columns.isEmpty()) "*" else columns.joinToString(", ")} FROM $tableName$join${
            c("WHERE", selection)}${
            c("GROUP BY", groupBy)}${c("HAVING", having)}${
            c("ORDER BY", orderBy)}${c("LIMIT", limit)}"

        private fun c(name: String, clause: String): String =
            if (clause.isNotEmpty()) " $name $clause" else ""

        operator fun String.invoke(): SField = SField(this, alias)
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

        fun values(vararg values: Pair<SField, Any>) {
            this.values = values.map { it.first.toString() to it.second }
                .toTypedArray()
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
     * Join构造器
     */
    inner class JoinBuilder(val join: String, private val alias: String = "") {
        var tableName: String = ""
            get() = if (alias.isNotEmpty()) "$field AS $alias" else field
        var on = ""
            get() = if (field.isNotEmpty()) " ON $field" else field
            private set
        var onArgs = arrayOf<String>()
            private set

        fun on(init: ConditionsBuilder.() -> Unit) {
            val onBuilder = ConditionsBuilder()
            onBuilder.init()
            this.on = onBuilder.condition
            this.onArgs = onBuilder.conditionArgs.map { it.toString() }.toTypedArray()
        }

        override fun toString(): String = " $join JOIN $tableName$on"
    }

    /**
     * Conditions构造器
     */
    inner class ConditionsBuilder(private val alias: String = "") {
        var condition = ""
            private set
        var conditionArgs = mutableListOf<Any>()
            private set

        infix fun SField.lt(value: Any): String = getExpression(this, "<", value)
        infix fun SField.le(value: Any): String = getExpression(this, "<=", value)
        infix fun SField.gt(value: Any): String = getExpression(this, ">", value)
        infix fun SField.ge(value: Any): String = getExpression(this, ">=", value)
        infix fun SField.ne(value: Any): String = getExpression(this, "<>", value)
        infix fun SField.eq(value: Any): String = getExpression(this, "=", value)

        fun and(condition1: String, condition2: Any, vararg conditionN: Any): String =
            getClauses("AND", condition1, condition2, *conditionN)

        fun SField.between(start: Any, end: Any): String =
            getExpression(this, "BETWEEN", start, "AND", end)

        fun SField.notBetween(start: Any, end: Any): String =
            getExpression(this, "NOT BETWEEN", start, "AND", end)

        fun exists(init: () -> SelectBuilder): String = getSubQuery(SField.Empty, "EXISTS", init)
        fun notExists(init: () -> SelectBuilder): String =
            getSubQuery(SField.Empty, "NOT EXISTS", init)

        fun exists(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(SField.Empty, "EXISTS", sql, sqlArgs)

        fun notExists(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(SField.Empty, "NOT EXISTS", sql, sqlArgs)

        fun SField.In(vararg value: Any): String = getInValues(this, "IN", value)
        fun SField.notIn(vararg value: Any): String = getInValues(this, "NOT IN", value)
        fun SField.In(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(this, "IN", sql, sqlArgs)

        fun SField.notIn(sql: String, vararg sqlArgs: Any): String =
            getSubQuery(this, "NOT IN", sql, sqlArgs)

        fun SField.In(init: () -> SelectBuilder): String = getSubQuery(this, "IN", init)
        fun SField.notIn(init: () -> SelectBuilder): String = getSubQuery(this, "NOT IN", init)

        infix fun SField.like(pattern: String): String = getExpression(this, "LIKE '$pattern'")
        infix fun SField.notLike(pattern: String): String =
            getExpression(this, "NOT LIKE '$pattern'")

        infix fun SField.glob(pattern: String): String = getExpression(this, "GLOB '$pattern'")
        infix fun SField.notGlob(pattern: String): String =
            getExpression(this, "NOT GLOB '$pattern'")

        fun or(condition1: String, condition2: Any, vararg conditionN: Any): String =
            getClauses("OR", condition1, condition2, *conditionN)

        fun SField.isNull(): String = getExpression(this, "IS NULL")
        fun SField.isNotNull(): String = getExpression(this, "IS NOT NULL")
        infix fun SField.Is(value: Any): String = getExpression(this, "IS", value)
        infix fun SField.isNot(value: Any): String = getExpression(this, "IS NOT", value)

        /**
         * 取得逻辑表达式
         */
        private fun getClauses(operator: String, vararg cond: Any): String =
            "(${cond.joinToString(" $operator ")})".apply { condition = this }

        /**
         * 取得表达式字符串
         */
        @Suppress("UNCHECKED_CAST")
        private fun getExpression(
            field: SField, operator: String, value: Any? = null,
            operator2: String = "", value2: Any? = null
        ): String {
            val fov = fov(field, operator, value)
            val fov2 =
                if (operator2.isNotEmpty()) fov(SField.Empty, operator2, value2)
                else ""
            return "$fov$fov2".apply { if (condition.isEmpty()) condition = this }
        }

        /**
         * 取得IN语句的值
         */
        private fun getInValues(field: SField, operator: String, value: Array<out Any>): String =
            "${fo(field, operator)} (${value.joinToString(" , ") { "?" }})".apply {
                conditionArgs.addAll(value)
                condition = this
            }

        /**
         * 取得子查询
         */
        private fun getSubQuery(
            field: SField, operator: String, sql: String, sqlArgs: Array<out Any>
        ): String = "${fo(field, operator)} ($sql)".apply {
            conditionArgs.addAll(sqlArgs)
            condition = this
        }

        /**
         * 取得子查询
         */
        private fun getSubQuery(
            field: SField, operator: String, init: () -> SelectBuilder
        ): String = init().let {
            getSubQuery(field, operator, it.createSql(), it.selectionArgs)
        }

        /**
         * 生成字段+操作符
         */
        private fun fo(field: SField, operator: String) =
            if (field.notEmpty()) "$field $operator" else " $operator"

        /**
         * 生成字段+操作符+值
         */
        @Suppress("UNCHECKED_CAST")
        private fun fov(field: SField, operator: String, value: Any?): String = when (value) {
            null -> fo(field, operator)
            is SField -> "${fo(field, operator)} $value"
            value as? () -> SelectBuilder -> getSubQuery(field, operator, value)
            else -> "${fo(field, operator)} ?".apply {
                conditionArgs.add(value)
            }
        }

        operator fun String.invoke(): SField = SField(this, alias)
    }
}