package com.riverside.skeleton.kotlin.db

import android.database.sqlite.SQLiteDatabase
import com.riverside.skeleton.kotlin.db.DatabaseUtil.getCreateSql
import com.riverside.skeleton.kotlin.db.DatabaseUtil.getDropSql
import com.riverside.skeleton.kotlin.db.DatabaseUtil.getFieldNames

/**
 * 数据库迁移帮助类 1.0
 *
 * b_e  2020/12/28
 */
object DbMigrationHelper {
    /**
     * 迁移数据库
     */
    fun migration(db: SQLiteDatabase) {
        DatabaseDCL(db).transaction {
            DbBeanHelper.beanList.forEach { clazz ->
                val tableName = clazz.getTableName()
                val tableNameTemp = "${tableName}_TEMP"

                //生成临时表
                if (isTableExists(tableName)) {
                    "DROP TABLE IF EXISTS $tableNameTemp".exec()
                    "CREATE TEMPORARY TABLE $tableNameTemp AS SELECT * FROM $tableName".exec()
                }

                //删除旧表
                clazz.getDropSql().exec()
                //创建新表
                clazz.getCreateSql().exec()

                if (isTableExists(tableNameTemp, true)) {
                    val columnList = getColumnsName(tableNameTemp)
                    val fieldList = clazz.getFieldNames().filter { columnList.contains(it) }
                    if (fieldList.isNotEmpty()) {
                        //还原数据
                        val fields = fieldList.joinToString(", ")
                        "INSERT INTO $tableName ($fields) SELECT $fields FROM $tableNameTemp".exec()
                        "DROP TABLE $tableNameTemp".exec()
                    }
                }
            }
        }
    }
}