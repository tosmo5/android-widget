package com.tosmo.awidget

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.tosmo.awidget.cw.CustomWidget


/**
 *
 * 将继承[CustomWidget]的[widget]包装成[AlertDialog]
 * 在创建[AlertDialog]前，执行一次[buildAction]
 *
 * 类中包含实际[dialog]，并简单提供了[show]和[dismiss]方法
 * [AlertDialog.setCanceledOnTouchOutside]默认为 false，可在[show]前通过[dialog]修改
 */
class DialogContainer<CW : CustomWidget>(
    val widget: CW,
    private val buildAction: ((dc: DialogContainer<CW>) -> Unit)? = null
) {
    private var mBuildActions: MutableList<(AlertDialog.Builder) -> Unit> = mutableListOf()
    
    private val mBuilder: AlertDialog.Builder =
        AlertDialog.Builder(widget.getWidgetContext()).setView(widget.getWidgetView())
    
    private var mNotDissmissButtonActions: MutableMap<Int, (view: View) -> Unit> = mutableMapOf()
    
    lateinit var dialog: AlertDialog
    
    fun setNotDismissButton(witch: Int, name: String, listener: (view: View) -> Unit) {
        when (witch) {
            AlertDialog.BUTTON_POSITIVE -> mBuilder.setPositiveButton(name, null)
            AlertDialog.BUTTON_NEGATIVE -> mBuilder.setNegativeButton(name, null)
            AlertDialog.BUTTON_NEUTRAL -> mBuilder.setNeutralButton(name, null)
        }
        mNotDissmissButtonActions[witch] = listener
    }
    
    fun setNotDismissButton(witch: Int, @StringRes nameRes: Int, listener: (view: View) -> Unit) {
        when (witch) {
            AlertDialog.BUTTON_POSITIVE -> mBuilder.setPositiveButton(nameRes, null)
            AlertDialog.BUTTON_NEGATIVE -> mBuilder.setNegativeButton(nameRes, null)
            AlertDialog.BUTTON_NEUTRAL -> mBuilder.setNeutralButton(nameRes, null)
        }
        mNotDissmissButtonActions[witch] = listener
    }
    
    /**
     * 添加[AlertDialog]的构造行为
     */
    fun addOnBuild(buildAction: (builder: AlertDialog.Builder) -> Unit) {
        mBuildActions.add(buildAction)
    }
    
    /**
     * 创建新的[AlertDialog]
     */
    fun create(): DialogContainer<CW> {
        buildAction?.let { it(this) }
        mBuildActions.forEach { it(mBuilder) }
        dialog = mBuilder.create().apply { setCanceledOnTouchOutside(false) }
        dialog.setOnShowListener {
            mNotDissmissButtonActions.forEach { (witch, listener) ->
                dialog.getButton(witch).setOnClickListener(listener)
            }
        }
        return this
    }
    
    /**
     * 显示[AlertDialog]，若未创建则自动创建一次
     */
    fun show(): DialogContainer<CW> {
        if (widget.isReady()) {
            if (!this::dialog.isInitialized) {
                create()
            }
            dialog.show()
            widget.onStart()
        }
        return this
    }
    
    fun dismiss() = dialog.dismiss()
}
