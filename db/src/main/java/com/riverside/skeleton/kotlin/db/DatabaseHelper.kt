package com.riverside.skeleton.kotlin.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import com.riverside.skeleton.kotlin.util.resource.ContextHolder

/**
 * 数据库帮助类   1.0
 *
 * b_e  2020/12/28
 */
class DatabaseHelper constructor(context: Context, dbName: String, dbVersion: Int) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {
    val currentDatabase: SQLiteDatabase
        get() = try {
            writableDatabase
        } catch (e: Exception) {
            readableDatabase
        }

    /**
     * 创建数据库
     */
    override fun onCreate(db: SQLiteDatabase) {
        DatabaseDCL(db).transaction {
            DbBeanHelper.getCreateSql().exec()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        DbMigrationHelper.migration(db)
    }

    /**
     * 执行SQL语句
     */
    @Synchronized
    fun rawQuery(sql: String, selectionArgs: Array<String>): Cursor =
        currentDatabase.rawQuery(sql, selectionArgs)

    /**
     * 执行SQL语句
     */
    @Synchronized
    fun execSQL(sql: String, bindArgs: Array<String>) {
        currentDatabase.execSQL(sql, bindArgs)
    }

    /**
     * 插入一条数据
     *
     * @return
     */
    @Synchronized
    fun insert(table: String, values: ContentValues): Long =
        currentDatabase.insert(table, null, values)

    /**
     * 查询
     */
    @Synchronized
    fun query(
        table: String, columns: Array<String>,
        selection: String, selectionArgs: Array<String>,
        groupBy: String?, having: String?,
        orderBy: String
    ): Cursor =
        currentDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)

    /**
     * 删除
     */
    @Synchronized
    fun delete(table: String, whereClause: String, whereArgs: Array<String>): Int =
        currentDatabase.delete(table, whereClause, whereArgs)

    /**
     * 更新一条数据
     */
    @Synchronized
    fun update(
        table: String, values: ContentValues, whereClause: String, whereArgs: Array<String>
    ): Int = currentDatabase.update(table, values, whereClause, whereArgs)

    @Synchronized
    override fun close() {
        super.close()
        if (currentDatabase.isOpen) currentDatabase.close()
    }

    companion object {
        var databaseName: String = ""
        private val dbVersion: Int by MetadataInfo("DATABASE_VERSION", 1)

        @Volatile
        private var db: DatabaseHelper? = null
        val defaultDatabase
            get() = if (databaseName.isEmpty()) throw Exception("需要先设置databaseName") else
                db
                    ?: synchronized(this) {
                        db ?: DatabaseHelper(
                            ContextHolder.applicationContext, databaseName, dbVersion
                        ).also { db = it }
                    }
    }
}