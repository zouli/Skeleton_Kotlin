package com.riverside.skeleton.kotlin.widget.selector

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.CompleteListView
import kotlinx.android.synthetic.main.view_address_selector.view.*

/**
 * 地址选择控件   1.0
 *
 * b_e  2021/2/25
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class AddressSelectorView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    constructor(context: Context) : this(context, null)

    private lateinit var adapterSelected: ListViewAdapter<AddressSelectItem>
    private lateinit var adapterList: ListViewAdapter<AddressSelectItem>
    private lateinit var getListListener: OnGetListListener
    private lateinit var selectIsOver: (data: List<AddressSelectItem>) -> Unit

    /**
     * 已选择Item Layout
     */
    @Attr(AttrType.REFERENCE)
    val selectedLayoutId: Int by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_selectedLayoutId,
        R.layout.list_item_address_selector_selected
    )

    fun setSelectedLayoutId(id: Int) = initSelected(id)

    /**
     * 备选Item Layout
     */
    @Attr(AttrType.REFERENCE)
    val listItemLayoutId: Int by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_listItemLayoutId,
        R.layout.list_item_address_selector_item
    )

    fun setListItemLayoutId(id: Int) = initList(id)

    /**
     * 选择个数
     */
    @Attr(AttrType.INTEGER)
    val _selectSize: Int by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_selectSize, 3
    )

    var selectSize = _selectSize

    /**
     * 选择提示
     */
    @Attr(AttrType.STRING)
    val _selectHint: String by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_selectHint, "请选择"
    )

    var selectHint = _selectHint
        set(value) {
            field = value
            with(adapterSelected.getItem(adapterSelected.count - 1)) {
                if (this.code == "") {
                    adapterSelected.remove(this)
                    addSelectHint(null)
                }
            }
        }

    /**
     * 分割线样式
     */
    @Attr(AttrType.DRAWABLE)
    private val listDivider: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_listDivider, null
    )

    /**
     * 分割线高度
     */
    @Attr(AttrType.DIMENSION)
    private val listDividerHeight: Int by AttributeSetInfo(
        context, attrs, R.styleable.AddressSelectorView,
        R.styleable.AddressSelectorView_asv_listDividerHeight, 0
    )

    fun setDivider(divider: Drawable?, dividerHeight: Int) {
        lv_list.divider = divider
        lv_list.dividerHeight = dividerHeight
    }

    init {
        initView()
        initEvent()
    }

    private fun initView() {
        LayoutInflater.from(context)
            .inflate(R.layout.view_address_selector, this@AddressSelectorView)

        initSelected(selectedLayoutId)
        initList(listItemLayoutId)
    }

    private fun initSelected(id: Int) {
        adapterSelected =
            ListViewAdapter(id) { viewHolder, _, item ->
                viewHolder.setText(android.R.id.text1, item.name)
            }

        clv_selected.adapter = adapterSelected
        clv_selected.choiceMode = CompleteListView.CHOICE_MODE_SINGLE
        addSelectHint(null)
    }

    private fun initList(id: Int) {
        adapterList =
            ListViewAdapter(id) { viewHolder, _, item ->
                viewHolder.setText(android.R.id.text1, item.name)
            }
        lv_list.adapter = adapterList
        lv_list.choiceMode = CompleteListView.CHOICE_MODE_SINGLE
        setDivider(listDivider, listDividerHeight)
    }

    private fun initEvent() {
        clv_selected.setOnItemClickListener { _, position ->
            val parent = adapterSelected.getItem(position)

            parent.parent.apply {
                adapterList.clear()
                if (::getListListener.isInitialized) getListData(position, this)
            }
        }

        lv_list.setOnItemClickListener { _, _, position, _ ->
            val parent = adapterList.getItem(position)

            val p = clv_selected.getCheckedItemPosition()
            if (p > -1) {
                (adapterSelected.count - 1 downTo p).forEach {
                    adapterSelected.remove(it)
                }
            }
            adapterSelected.addItem(parent)

            if (adapterSelected.count >= selectSize) {
                if (::selectIsOver.isInitialized) selectIsOver(adapterSelected.getItems())
            } else {
                clv_selected.clearChoices()
                addSelectHint(parent)

                adapterList.clear()
                lv_list.clearChoices()
                getListData(p + 1, parent)
            }
        }
    }

    private fun addSelectHint(parent: AddressSelectItem?) {
        if (adapterSelected.count < selectSize)
            adapterSelected.addItem(AddressSelectItem("", selectHint, parent))
        clv_selected.setItemChecked(adapterSelected.count - 1, true)
    }

    private fun getListData(position: Int, parent: AddressSelectItem?) {
        this.post {
            if (::getListListener.isInitialized) getListListener(position, parent)
        }
    }

    /**
     * 选择完成监听
     */
    fun setOnSelectIsOver(listener: (data: List<AddressSelectItem>) -> Unit) {
        this.selectIsOver = listener
    }

    /**
     * 取得当前已选择列表
     */
    fun getSelectedData(): List<AddressSelectItem> = adapterSelected.getItems()

    /**
     * 设置当前地址列表
     */
    fun setListData(data: List<AddressSelectItem>) {
        adapterList.clear()
        adapterList.addItems(data)
        lv_list.setSelection(0)

        val parent = adapterSelected.getItem(clv_selected.getCheckedItemPosition())
        (0 until adapterList.count).firstOrNull {
            adapterList.getItem(it).code == parent.code
        }?.apply {
            lv_list.setItemChecked(this, true)
            lv_list.setSelection(this)
        }
    }

    /**
     * 设置已选择列表
     */
    fun setSelectedData(data: List<AddressSelectItem>) {
        this.post {
            if (data.isNotEmpty()) {
                adapterSelected.clear()
                data.fold<AddressSelectItem, AddressSelectItem?>(null) { acc, addressSelectItem ->
                    AddressSelectItem(addressSelectItem.code, addressSelectItem.name, acc).apply {
                        adapterSelected.addItem(this)
                    }
                }?.apply {
                    clv_selected.setItemChecked(data.size - 1, true)
                    getListData(data.size - 1, this.parent)
                }
            } else getListData(0, null)
        }
    }

    /**
     * 取得地址列表监听
     */
    fun setOnGetListListener(listener: OnGetListListener) {
        getListListener = listener
    }
}

typealias OnGetListListener = (position: Int, parent: AddressSelectItem?) -> Unit

/**
 * 地址Item
 */
data class AddressSelectItem(
    val code: String, val name: String, val parent: AddressSelectItem? = null
)