package com.riverside.skeleton.kotlin.widget.selector

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.widget.ListView

/**
 * 共通选择画面设置类    1.0
 *
 * b_e      2020/12/03
 */
@SuppressLint("ParcelCreator")
class CommonSelectorOptions() : Parcelable {
    /** 标题 */
    var titleName = ""

    /** 选择模式 */
    var choiceMode = ListView.CHOICE_MODE_SINGLE

    /** 默认选择值 */
    var checkedValue = mutableListOf<String>()

    /** 空状态显示内容 */
    var emptyView = 0

    /** 查询模式 */
    var hasSearch = false

    /** 查询条件 */
    var listCriteria = mutableMapOf<String, Any>()

    /** 列表生成器 */
    var listGenerator: Class<out CommonSelectorBiz>? = null

    constructor(parcel: Parcel) : this() {
        readFroParcel(parcel)
    }

    /**
     * 读取缓存数据
     */
    private fun readFroParcel(parcel: Parcel) = with(parcel) {
        titleName = this.readString() ?: ""
        choiceMode = this.readInt()
        hasSearch = this.readInt() == 1
        this.readStringList(checkedValue)
        emptyView = this.readInt()
        this.readMap(listCriteria, javaClass.classLoader)
        this.readString()?.let { listGenerator = Class.forName(it) as Class<CommonSelectorBiz> }
    }

    /**
     * 缓存数据
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(titleName)
        dest.writeInt(choiceMode)
        dest.writeInt(if (hasSearch) 1 else 0)
        dest.writeStringList(checkedValue)
        dest.writeInt(emptyView)
        dest.writeMap(listCriteria)
        listGenerator?.let { dest.writeString(it.name) }
    }

    override fun describeContents() = 0

    /**
     * 缓存创建者对象
     */
    companion object CREATOR : Parcelable.Creator<CommonSelectorOptions> {
        override fun createFromParcel(parcel: Parcel): CommonSelectorOptions {
            return CommonSelectorOptions(parcel)
        }

        override fun newArray(size: Int): Array<CommonSelectorOptions?> {
            return arrayOfNulls(size)
        }
    }
}