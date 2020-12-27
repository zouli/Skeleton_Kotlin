package com.riverside.skeleton.kotlin

import android.content.Context
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.db.DatabaseTypeHelper
import com.riverside.skeleton.kotlin.db.DbBeanHelper
import com.riverside.skeleton.kotlin.db.sqlite
import com.riverside.skeleton.kotlin.dbtest.A
import com.riverside.skeleton.kotlin.dbtest.AB
import com.riverside.skeleton.kotlin.dbtest.B
import com.riverside.skeleton.kotlin.dbtest.C
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.converter.toDate
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
                    val dataA =
                        listOf(
                            A(null, "dd", Date(), 0.2, null, null, null),
                            A(
                                null,
                                "cc",
                                Date(),
                                1.2,
                                listOf("1", "2"),
                                listOf(3, 4),
                                listOf(6.6, 7.7)
                            )
                        )
                    val dataB = B(
                        1, 2, 3, 4, "伍", 6.6F, 777.77777, true,
                        "2020-05-15 05:21:01".toDate(DatabaseTypeHelper.DATE_PATTERN)
                    )
                    sqlite {
//                        transaction {
                        insert(dataA)
                        replace(dataB)
//                        }
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("select data") {
                onClick {
                    sqlite {
                        select<A>("C") {
                            indexedBy()
//                            where {
//                                "scoreMath"() lt {
//                                    subSelect<B>("D") {
//                                        column("a"() As "b")
//                                        where { "c"().between(2, 4) }
//                                    }
//                                }
//                            }

//                            groupBy("scoreMath"()) {
//                                "scoreMath"() lt 1
//                            }
                            orderBy("scoreMath"().desc(), "id"())
                        }.forEach { SLog.w(it) }

                        select<B> {
                            distinct = true
                            where {
                                "h"().isNotNull()
                            }
                        }.forEach { SLog.w(it) }
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("delete") {
                onClick {
                    sqlite {
                        delete<B>()

                        delete<A> {
                            "id"() ge 2
                        }
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("update") {
                onClick {
                    val dataB = B(
                        2, 2, 3, 4, "伍伍", 6.6F, 777.77777, null,
                        "2020-05-15 05:21:01".toDate(DatabaseTypeHelper.DATE_PATTERN)
                    )

                    sqlite {
                        update(dataB, true) {
//                            values("a" to 2)
                            where {
//                                "a" eq 1
                                "b"().notIn {
                                    subSelect<A>("A") {
                                        column("scoreMath"())
                                        where { "userId"() eq "dd" }
                                        groupBy("userId"()) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("unions") {
                onClick {
                    sqlite {
                        union<AB>({
                            subSelect<A>("A") {
                                column("id"() As "aA", "userId"() As "bB")
                                where {
                                    "id"() gt 2
                                }
                            }
                        }, {
                            subSelect<B> {
                                column("a"() As "aA", "e"() As "bB")
                            }
                        }).forEach { SLog.w(it) }

                        SLog.w("------Union All-------")

                        unionAll<AB>({
                            subSelect<A>("A") {
                                column("id"() As "aA", "userId"() As "bB")
                            }
                        }, {
                            subSelect<B> {
                                column("a"() As "aA", "e"() As "bB")
                            }
                        }).forEach { SLog.w(it) }
                    }
                }
            }

            button("join") {
                onClick {
                    sqlite {
                        select<A>("A") {
                            column(
                                "a"().prefix("BB") As "id",
                                "userId"(),
                                "b"().prefix("CC") As "flag"
                            )
                            join(
                                crossJoin<B>("BB") {
                                    "a"().prefix("BB") eq "id"().prefix("A")
                                },
                                leftJoin<C>("CC") {
                                    "a"().prefix("CC").isNull()
                                }
                            )
                        }.forEach { SLog.w(it) }
                    }
                }
            }

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