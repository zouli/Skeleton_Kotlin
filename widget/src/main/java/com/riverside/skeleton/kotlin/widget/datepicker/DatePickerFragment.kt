package com.riverside.skeleton.kotlin.widget.datepicker

import android.view.KeyEvent
import androidx.fragment.app.FragmentManager
import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker.RecurrenceOption
import com.riverside.skeleton.kotlin.base.fragment.SBaseDialogFragment
import com.riverside.skeleton.kotlin.util.converter.calendar
import com.riverside.skeleton.kotlin.util.converter.hourOfDay
import com.riverside.skeleton.kotlin.util.converter.minute
import com.riverside.skeleton.kotlin.util.extras.FragmentArgument
import com.riverside.skeleton.kotlin.util.extras.setArguments
import com.riverside.skeleton.kotlin.widget.R
import java.util.*

/**
 * 日期选择器     1.0
 * b_e      2020/11/29
 */
class DatePickerFragment : SBaseDialogFragment() {
    companion object {
        const val SUBLIME_OPTIONS = "SUBLIME_OPTIONS"
    }

    private val options: SublimeOptions by FragmentArgument(SUBLIME_OPTIONS)

    private lateinit var onCancelledListener: () -> Unit
    private lateinit var onDateTimeRecurrenceSetListener: (
        selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
        recurrenceOption: RecurrenceOption?, recurrenceRule: String?
    ) -> Unit

    override val layoutId: Int get() = R.layout.sublime_picker

    override fun initView() {
        with(view as SublimePicker) {
            this.initializePicker(options, mListener)
        }

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (::onCancelledListener.isInitialized) onCancelledListener()

                dismiss()
                true
            } else {
                //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
                false
            }
        }
    }

    fun setOnCancelledListener(block: () -> Unit) {
        onCancelledListener = block
    }

    fun setOnDateTimeRecurrenceSetListener(
        block: (
            selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
            recurrenceOption: RecurrenceOption?, recurrenceRule: String?
        ) -> Unit
    ) {
        onDateTimeRecurrenceSetListener = block
    }

    var mListener: SublimeListenerAdapter = object : SublimeListenerAdapter() {
        override fun onCancelled() {
            if (::onCancelledListener.isInitialized) onCancelledListener()

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss()
        }

        override fun onDateTimeRecurrenceSet(
            sublimeMaterialPicker: SublimePicker,
            selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
            recurrenceOption: RecurrenceOption?, recurrenceRule: String?
        ) {
            if (::onDateTimeRecurrenceSetListener.isInitialized)
                onDateTimeRecurrenceSetListener(
                    selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule
                )

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss()
        }
    }

    class Creator(private val supportFragmentManager: FragmentManager) {
        private val sublimeOptions = SublimeOptions()
        private var displayOption = SublimeOptions.Picker.DATE_PICKER.ordinal
        private var startDate: Calendar = Date().calendar
        private var endDate: Calendar = startDate
        private lateinit var _onCancelledListener: () -> Unit
        private lateinit var _onDateTimeRecurrenceSetListener: (
            selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
            recurrenceOption: RecurrenceOption?, recurrenceRule: String?
        ) -> Unit

        fun showDate() =
            this.apply { displayOption = SublimeOptions.ACTIVATE_DATE_PICKER or displayOption }

        fun showTime() =
            this.apply { displayOption = SublimeOptions.ACTIVATE_TIME_PICKER or displayOption }

        fun showRecurrence() =
            this.apply {
                displayOption = SublimeOptions.ACTIVATE_RECURRENCE_PICKER or displayOption
            }

        fun setDate(date: Calendar) = setDate(date, date)

        fun setDate(startDate: Calendar, endDate: Calendar) =
            this.apply {
                this.startDate = startDate
                this.endDate = endDate
            }

        fun setOnCancelled(block: () -> Unit) = this.apply {
            _onCancelledListener = block
        }

        fun setOnDateTimeRecurrenceSet(
            block: (
                selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
                recurrenceOption: RecurrenceOption?, recurrenceRule: String?
            ) -> Unit
        ) = this.apply {
            _onDateTimeRecurrenceSetListener = block
        }

        fun show() = DatePickerFragment().apply {
            sublimeOptions.setDisplayOptions(displayOption)
            sublimeOptions.setDateParams(startDate, endDate)
            if (SublimeOptions.ACTIVATE_TIME_PICKER.and(displayOption) == SublimeOptions.ACTIVATE_TIME_PICKER)
                sublimeOptions.setTimeParams(startDate.hourOfDay, startDate.minute, true)
            setArguments(SUBLIME_OPTIONS to sublimeOptions)
            if (::_onCancelledListener.isInitialized) setOnCancelledListener { _onCancelledListener() }
            if (::_onDateTimeRecurrenceSetListener.isInitialized)
                setOnDateTimeRecurrenceSetListener { selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule ->
                    _onDateTimeRecurrenceSetListener(
                        selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule
                    )
                }
            setStyle(STYLE_NO_TITLE, 0)
            show(supportFragmentManager, "SUBLIME_PICKER")
        }
    }
}