package com.norma.abc.utils

import android.app.Activity
import android.widget.Toast

class BackPressExitHandler(private val activity: Activity) {
    private var backButtonPressedTime: Long = 0
    private var toast: Toast? = null

    fun onBackPressed() {
        if (System.currentTimeMillis() > backButtonPressedTime + 2000) {
            backButtonPressedTime = System.currentTimeMillis()
            toastGuide()
            return
        } else {
            activity.finish()
            toast!!.cancel()
        }
    }

    private fun toastGuide() {
        toast = Toast.makeText(activity, "'뒤로'를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
        toast!!.show()
    }
}
