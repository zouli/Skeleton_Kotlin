package com.riverside.skeleton.kotlin.db

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import com.riverside.skeleton.kotlin.db.DatabaseTypeHelper.toContentValues
import com.riverside.skeleton.kotlin.slog.SLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 数据库DCL类  1.0
 *
 * b_e  2020/12/28
 */
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
    fun String.exec(args: Array<Any?> = arrayOf()) {
        this.split(";").filter { it.isNotEmpty() }.forEach {
            db.execSQL(it, args)
        }
    }

    /**
     * 执行SQL
     */
    fun Array<String>.exec() {
        this.forEach { it.exec() }
    }

    /**
     * 执行SQL
     */
    fun List<String>.exec() {
        this.forEach { it.exec() }
    }

    /**
     * 插入（Bean）
     */
    inline fun <reified T> T.insert(): Long = insert(
        T::class.getTableName(), *DatabaseUtil.getFieldValueArray(this)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> List<T>.insert() = this.forEach { it.insert() }

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
    inline fun <reified T> T.replace(): Long = replace(
        T::class.getTableName(), *DatabaseUtil.getFieldValueArray(this)
    )

    /**
     * 插入多个（Bean）
     */
    inline fun <reified T> List<T>.replace() = this.forEach { it.replace() }

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
    fun String.select(selectionArgs: Array<String> = arrayOf()): Cursor =
        db.rawQuery(this, selectionArgs)

    /**
     * 查询(DCL)
     */
    @SuppressLint("Recycle")
    inline fun <reified T> select(
        alias: String = "", init: SelectBuilder.() -> Unit = {}
    ): Cursor =
        subSelect<T>(alias, init).run {
            if (join.isNotEmpty() || indexedBy.isNotEmpty())
                createSql().select(selectionArgs)
            else
                db.query(
                    distinct, tableName, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit
                )
        }

    /**
     * 取得子查询
     */
    inline fun <reified T> subSelect(
        alias: String = "", init: SelectBuilder.() -> Unit = {}
    ): SelectBuilder =
        SelectBuilder(alias).apply {
            tableName = T::class.getTableName()
            init()

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
    inline fun <reified T> T.update(
        updateNull: Boolean = false, init: UpdateBuilder.() -> Unit = {}
    ): Int =
        UpdateBuilder().run {
            init()

            update(
                T::class.getTableName(),
                *DatabaseUtil.getFieldValueArray(this@update, updateNull),
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
    fun union(select1: () -> SelectBuilder, select2: () -> SelectBuilder): Cursor {
        val selectBuilder1 = select1()
        val selectBuilder2 = select2()

        return "${selectBuilder1.createSql()} UNION ${selectBuilder2.createSql()}".select(
            arrayOf(*selectBuilder1.selectionArgs, *selectBuilder2.selectionArgs)
        )
    }

    /**
     * Union All
     */
    fun unionAll(select1: () -> SelectBuilder, select2: () -> SelectBuilder): Cursor {
        val selectBuilder1 = select1()
        val selectBuilder2 = select2()

        return "${selectBuilder1.createSql()} UNION ALL ${selectBuilder2.createSql()}".select(
            arrayOf(*selectBuilder1.selectionArgs, *selectBuilder2.selectionArgs)
        )
    }

    /**
     * 表是否存在
     */
    fun isTableExists(tableName: String, isTemp: Boolean = false): Boolean =
        "SELECT COUNT(*) FROM ${if (isTemp) "sqlite_temp_master" else "sqlite_master"} WHERE type = ? AND name = ?".select(
            arrayOf("table", tableName)
        ).toObject<Int>()?.let { it > 0 } ?: false

    /**
     * 取得字段名列表
     */
    fun getColumnsName(tableName: String): List<String> =
        "PRAGMA table_info($tableName)".select().toList(SField("name"))

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
        var indexedBy = ""
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

        fun indexedBy(indexName: String = "index_1") {
            this.indexedBy = "${tableName.originalTableName}_$indexName"
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
            c("INDEXED BY", indexedBy)}${c("WHERE", selection)}${
            c("GROUP BY", groupBy)}${c("HAVING", having)}${
            c("ORDER BY", orderBy)}${c("LIMIT", limit)}"

        private fun c(name: String, clause: String): String =
            if (clause.isNotEmpty()) " $name $clause" else ""

        operator fun String.invoke(): SField = SField(this, alias)

        private val String.originalTableName
            get() = if (this.indexOf(" AS") > -1) this.substring(0, this.indexOf(" AS")) else this
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