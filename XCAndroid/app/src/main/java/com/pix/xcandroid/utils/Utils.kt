package com.pix.xcandroid.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class Utils {
    /**
     * 根据手机分辨率从dp转成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    companion object {
        open fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * 显示软键盘
         */
        fun showSoftInput(context: Context) {
            val inputMeMana = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMeMana.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        /**
         * 隐藏输入键盘
         *
         * @param view
         * @param context
         */
        fun hideSoftInput(view: EditText, context: Context) {
            val inputMeMana = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMeMana.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


}