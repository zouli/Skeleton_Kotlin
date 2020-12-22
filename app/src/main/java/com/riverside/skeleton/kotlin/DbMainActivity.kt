package com.riverside.skeleton.kotlin

import android.content.Context
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.db.DatabaseHelper
import com.riverside.skeleton.kotlin.db.DbBeanHelper
import com.riverside.skeleton.kotlin.db.toList
import com.riverside.skeleton.kotlin.dbtest.A
import com.riverside.skeleton.kotlin.dbtest.B
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.converter.toString
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexFile
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import java.lang.reflect.Field
import java.util.*

class DbMainActivity : SBaseActivity() {
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)

            button("aaa") {
                onClick {
//                    val d = DexFile(this@DbMainActivity.packageCodePath)
//                    for (entry in d.entries()) {
//                        if(entry.contains("com.riverside.skeleton.kotlin.dbtest"))
//                        SLog.w(entry)
//                    }
                    getDexFiles(activity).flatMap { it.entries().asSequence() }
                        .filter { it.startsWith("com.riverside.skeleton.kotlin.dbtest") }
                        .forEach { SLog.w(it) }
                }
            }.lparams(matchParent, wrapContent)

            button("remove bean") {
                onClick {
                    DbBeanHelper.removeBean(A::class)
                }
            }.lparams(matchParent, wrapContent)

            button("insert data") {
                onClick {
                    val sql =
                        "INSERT INTO [A] ([user_id],[login_date],[score],[flag]) values (?,?,?,?)"
                    val args = arrayOf(
                        "aa", Date().toString("yyyy-MM-dd HH:mm:ss"), "0.1", "1,2,3,4"
                    )
                    DatabaseHelper.defaultDatabase.execSQL(sql, args)

                    val sql1 =
                        "INSERT INTO [B] ([a],[b],[c],[d],[e],[f],[g],[h],[i]) values (?,?,?,?,?,?,?,?,?)"
                    val args1 = arrayOf(
                        "1",
                        "2",
                        "3",
                        "4",
                        "伍",
                        "6.6",
                        "7.7",
                        "1",
                        "2020-05-15 05:21:01"
                    )
                    DatabaseHelper.defaultDatabase.execSQL(sql1, args1)
                }
            }.lparams(matchParent, wrapContent)

            button("select data") {
                onClick {
                    val c = DatabaseHelper.defaultDatabase.rawQuery("SELECT * FROM [A]", arrayOf())
                    SLog.w(c.toList<A>())
                    val c1 = DatabaseHelper.defaultDatabase.rawQuery("SELECT * FROM [B]", arrayOf())
                    SLog.w(c1.toList<B>())
                }
            }.lparams(matchParent, wrapContent)

            button("aaaa") {
                onClick {
//                    val a:SInt = SInt()
//                    A(a, null, null, null, null)
//                    SLog.w(a eq 1)
                    SLog.w(DbBeanHelper.getCreateSql())
                }
            }.lparams(matchParent, wrapContent)
        }
    }

    private fun getDexFiles(context: Context): Sequence<DexFile> {
        // Here we do some reflection to access the dex files from the class loader. These implementation details vary by platform version,
        // so we have to be a little careful, but not a huge deal since this is just for testing. It should work on 21+.
        // The source for reference is at:
        // https://android.googlesource.com/platform/libcore/+/oreo-release/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
        val classLoader = context.classLoader as BaseDexClassLoader

        val pathListField = field("dalvik.system.BaseDexClassLoader", "pathList")
        val pathList = pathListField.get(classLoader) // Type is DexPathList

        val dexElementsField = field("dalvik.system.DexPathList", "dexElements")

        @Suppress("UNCHECKED_CAST")
        val dexElements =
            dexElementsField.get(pathList) as Array<Any> // Type is Array<DexPathList.Element>

        val dexFileField = field("dalvik.system.DexPathList\$Element", "dexFile")
        return dexElements.map {
            dexFileField.get(it) as DexFile
        }.asSequence()
    }

    private fun field(className: String, fieldName: String): Field {
        val clazz = Class.forName(className)
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        return field
    }
}