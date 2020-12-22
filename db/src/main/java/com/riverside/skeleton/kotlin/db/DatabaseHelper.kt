package com.riverside.skeleton.kotlin.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import com.riverside.skeleton.kotlin.util.resource.ContextHolder

class DatabaseHelper constructor(context: Context, dbName: String, dbVersion: Int) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {
    private var currentDatabase: SQLiteDatabase = try {
        writableDatabase
    } catch (e: Exception) {
        readableDatabase
    }

    /**
     * 创建数据库
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.beginTransaction()
        DbBeanHelper.getCreateSql().forEach {
            db.execSQL(it)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

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
        orderBy: String
    ): Cursor =
        currentDatabase.query(table, columns, selection, selectionArgs, null, null, orderBy)

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

    /**
     * 取得字段名列表
     */
    fun getColumnsName(tableName: String): List<String> =
        rawQuery("PRAGMA table_info($tableName)", arrayOf()).run {
            val columnsName = mutableListOf<String>()
            if (this.moveToFirst()) {
                val nameIndex = this.getColumnIndexIgnoreCase("name")
                do {
                    columnsName.add(this.getString(nameIndex))
                } while (this.moveToNext())
            }
            columnsName
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