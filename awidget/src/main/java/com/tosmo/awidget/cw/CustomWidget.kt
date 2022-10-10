package com.tosmo.awidget.cw

import android.content.Context
import android.view.View

interface CustomWidget {
    
    /**
     * 取得组件的上下文
     */
    fun getWidgetContext(): Context
    
    /**
     * 取得组件的[View]
     */
    fun getWidgetView(): View?
    
    /**
     * 让组件开始工作
     */
    fun onStart()
    
    /**
     * 此组件是否准备好工作
     */
    fun isReady(): Boolean
}