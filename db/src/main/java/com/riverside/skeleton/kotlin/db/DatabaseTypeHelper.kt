package com.riverside.skeleton.kotlin.db

interface SqliteType<T>

class SInt : SqliteType<Int> {

}

infix fun SqliteType<*>.eq(other: Any?): String {
    return "$this == $other"
}