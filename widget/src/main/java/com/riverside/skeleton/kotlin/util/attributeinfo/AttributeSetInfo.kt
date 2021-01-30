package com.riverside.skeleton.kotlin.util.attributeinfo

import android.content.Context
import android.util.AttributeSet
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 * AttributeSet处理类  1.0
 *
 * b_e  2020/11/15
 */

class AttributeSetInfo<T>(
    private val context: Context,
    private val attrs: AttributeSet?, private val styleableId: IntArray,
    private val attrId: Int, private val default: T
) : ReadOnlyProperty<Any?, T> {
    private val attrMap = mutableMapOf<Int, T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val attr = property.findAnnotation<Attr>()
        return attrMap.getOrPut(attrId) {
            with(context.obtainStyledAttributes(attrs, styleableId, 0, 0)) {
                if (attr != null) {
                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    val res = when (attr.type) {
                        AttrType.INTEGER -> getInt(attrId, default as Int)
                        AttrType.STRING -> getString(attrId)
                        AttrType.DIMENSION -> getDimensionPixelSize(attrId, default as Int)
                        AttrType.REFERENCE -> getResourceId(attrId, default as Int)
                        AttrType.COLOR -> getColor(attrId, default as Int)
                        AttrType.BOOLEAN -> getBoolean(attrId, default as Boolean)
                        AttrType.FLOAT -> getFloat(attrId, default as Float)
                        AttrType.FRACTION -> getFraction(
                            attrId, attr.fraction_base, attr.fraction_pbase, default as Float
                        )
                        AttrType.ENUM -> getInt(attrId, default as Int)
                        AttrType.FLAG -> getInt(attrId, default as Int)
                        AttrType.DRAWABLE -> getDrawable(attrId)
                    }
                    recycle()

                    (res ?: default) as T
                } else default
            }
        }
    }
}

/**
 * Attr类型注解
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Attr(val type: AttrType, val fraction_base: Int = 1, val fraction_pbase: Int = 1)

/**
 * Attr类型
 */
enum class AttrType(type: String) {
    REFERENCE("reference"),
    INTEGER("integer"),
    STRING("string"),
    DIMENSION("dimension"),
    COLOR("color"),
    BOOLEAN("boolean"),
    FLOAT("float"),
    FRACTION("fraction"),
    ENUM("enum"),
    FLAG("flag"),
    DRAWABLE("drawable")
}