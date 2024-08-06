package com.example.budgee.layouts

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout

class ScrollableTabLayout @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val tabCount = tabCount
        val screenWidth = measuredWidth
        if (tabCount > 0) {
            val tabWidth = when (tabCount) {
                1 -> screenWidth
                2 -> screenWidth / 2
                else -> screenWidth / 3
            }
            for (i in 0 until tabCount) {
                val tab = getTabAt(i)
                tab?.view?.let {
                    val layoutParams = it.layoutParams
                    layoutParams.width = tabWidth
                    it.layoutParams = layoutParams
                }
            }
        }
    }
}