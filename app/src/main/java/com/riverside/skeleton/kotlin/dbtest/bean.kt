package com.riverside.skeleton.kotlin.dbtest

import com.riverside.skeleton.kotlin.db.*
import java.util.*

@STable("AA")
//@SCreateSql(
//    "CREATE TABLE [A] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
//            ", [user_id] VARCHAR(50)" +
//            ", [login_date] VARCHAR(20)" +
//            ", [score] NUMBER" +
//            ", [flag] VARCHAR(200)" +
//            ")"
//)
data class A(
    @Id var id: Int?,
    var userId: String?,
    var loginDate: Date?,
    var scoreMath: Double?,
    var flag1: List<String>?,
    var flag2: List<Int>?,
    var flag3: List<Double>?
)

@STable
data class B(
    var a: Byte,
    var b: Short,
    @Unique var c: Int,
    var d: Long,
    var e: String,
    var f: Float,
    var g: Double,
    var h: Boolean?,
    var i: Date
)

@STable
data class C(
    @Id var a: Int,
    @Id @Check("b > 2") var b: Int,
    @Default("1.0") var c: Double?
)

data class AB(var aA: Int, var bB: String)