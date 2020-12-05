package com.riverside.skeleton.kotlin.basetest

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.eventbus.EventBus
import com.riverside.skeleton.kotlin.util.extras.setArguments
import org.jetbrains.anko.*
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.viewPager

class RxBusActivity : SBaseActivity() {
    lateinit var et_text: TextView
    lateinit var tabLayout: TabLayout
    lateinit var mViewPager: ViewPager
    lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    val tabTitles = mutableListOf<String>()
    val fragments = mutableListOf<Fragment>()
    var index = 1

    override fun initView() {
        title = "RxBus"

        verticalLayout {
            lparams(matchParent, matchParent)

            et_text = editText("1").lparams(matchParent, wrapContent)

            button("普通") {
                onClick {
//                    RxBus.post(et_text.text.toString())
                    EventBus.post(et_text.text.toString())
                }
            }.lparams(matchParent, wrapContent)

            button("粘滞") {
                onClick {
//                    RxBus.postSticky(et_text.text.toString())
                    EventBus.postSticky(et_text.text.toString())
                }
            }

            button("add fragment") {
                onClick {
                    tabTitles.add(index++.toString())
                    fragments.add(RxBusFragment().setArguments("value" to -4))
                    mSectionsPagerAdapter.notifyDataSetChanged()
                }
            }

            tabLayout = tabLayout {
                tabMode = TabLayout.MODE_SCROLLABLE
            }.lparams(matchParent, wrapContent)

            mViewPager = viewPager {
                id = R.id.container
            }.lparams(matchParent, matchParent)
        }

        tabTitles.add(index++.toString())
        fragments.add(RxBusFragment().setArguments("value" to -1))
        tabTitles.add(index++.toString())
        fragments.add(RxBusFragment().setArguments("value" to -2))
        tabTitles.add(index++.toString())
        fragments.add(RxBusFragment().setArguments("value" to -3))

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mSectionsPagerAdapter
        tabLayout.setupWithViewPager(mViewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
//        RxBus.removeSticky<String>()
        EventBus.removeSticky<String>()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = tabTitles.size

        override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
    }
}
