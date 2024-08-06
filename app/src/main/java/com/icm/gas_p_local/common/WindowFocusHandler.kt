package com.icm.gas_p_local.common

import android.app.Activity
import android.view.View

class WindowFocusHandler {
    companion object {
        fun handleWindowFocusChanged(activity: Activity, hasFocus: Boolean) {
            if (hasFocus) {
                activity.window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}
