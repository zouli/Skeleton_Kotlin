package com.riverside.skeleton.kotlin.dbtest

import com.riverside.skeleton.kotlin.db.Autoincrement
import com.riverside.skeleton.kotlin.db.Id
import com.riverside.skeleton.kotlin.db.STable
import java.util.*

@STable("A")
//@SCreateSql(
//    "CREATE TABLE [A] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
//            ", [user_id] VARCHAR(50)" +
//            ", [login_date] VARCHAR(20)" +
//            ", [score] NUMBER" +
//            ", [flag] VARCHAR(200)" +
//            ")"
//)
data class A(
    @Id @Autoincrement var id: Int,
    var userId: String?,
    var loginDate: Date?,
    var score: Double?,
    var flag: List<String>?
)

@STable
data class B(
    var a: Byte,
    var b: Short,
    var c: Int,
    var d: Long,
    var e: String,
    var f: Float,
    var g: Double,
    var h: Boolean,
    var i: Date
)