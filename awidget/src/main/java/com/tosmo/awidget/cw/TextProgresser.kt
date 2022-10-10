package com.tosmo.awidget.cw

import android.content.Context
import android.view.View
import androidx.core.view.doOnDetach
import com.tosmo.awidget.R
import com.tosmo.awidget.databinding.TextProgressBarBinding

abstract class TextProgresser<T, I : Iterator<T>>(
    val context: Context,
    private val max: Int,
    val iter: I
) :
    CustomWidget {
    
    val view: View = View.inflate(context, R.layout.text_progress_bar, null)
    
    val binding = TextProgressBarBinding.bind(view)
    
    val step = 1
    
    var description: String = ""
        set(value) {
            binding.progressBar.description = value
            field = value
        }
    
    private val mBeforeTraverseActions = mutableListOf<() -> Unit>()
    
    private val mOnTraverseActions = mutableListOf<(item: T, iter: I) -> Unit>()
    
    private val mTraverseViewActions = mutableListOf<(item: T) -> Unit>()
    
    private val mFinishTraverseActions = mutableListOf<() -> Unit>()
    
    private val mAfterTraverseAction = mutableListOf<() -> Unit>()
    
    private val mOnCatchExceptionActions = mutableListOf<(e: Exception) -> Unit>()
    
    private val mOnFinallyActions = mutableListOf<() -> Unit>()
    
    private val mDoOnDetachActions = mutableListOf<() -> Unit>()
    
    protected abstract val mTraverser: () -> Unit
    
    init {
        binding.progressBar.max = max
        view.doOnDetach {
            onDetach()
        }
    }
    
    /**
     * 加入遍历开始前的[action]
     */
    fun addBeforeTraverse(action: () -> Unit) {
        mBeforeTraverseActions.add(action)
    }
    
    fun onBeforeTraverse() {
        mBeforeTraverseActions.forEach { it() }
    }
    
    /**
     * 加入遍历时的[action]
     */
    open fun addOnTraverse(action: (item: T, iter: I) -> Unit) {
        mOnTraverseActions.add(action)
    }
    
    fun onTraverse(item: T, iter: I) {
        mOnTraverseActions.forEach { it(item, iter) }
    }
    
    /**
     * 加入成功遍历后的[action]
     */
    fun addAfterTraverse(action: () -> Unit) {
        mAfterTraverseAction.add(action)
    }
    
    fun afterTraverse() {
        mAfterTraverseAction.forEach { it() }
    }
    
    /**
     * 出现错误时的[action]
     */
    fun addOnCatchException(action: (e: Exception) -> Unit) {
        mOnCatchExceptionActions.add(action)
    }
    
    fun onCatchException(e: Exception) {
        mOnCatchExceptionActions.forEach { it(e) }
    }
    
    /**
     * 最后的[action]
     */
    fun addOnFinally(action: () -> Unit) {
        mOnFinallyActions.add(action)
    }
    
    fun onFinally() {
        mOnFinallyActions.forEach { it() }
    }
    
    
    /**
     * 添加在遍历数量到最大时要执行的[action]，一般只用于更新页面
     */
    fun addOnFinishTraverse(action: () -> Unit) {
        mFinishTraverseActions.add(action)
    }
    
    fun onFinishTraverse() {
        mFinishTraverseActions.forEach { it() }
    }
    
    /**
     * 添加每次遍历时更新页面的[action]
     */
    fun addOnTraverseView(action: (item: T) -> Unit) {
        mTraverseViewActions.add(action)
    }
    
    fun onTraverseView(item: T) {
        mTraverseViewActions.forEach { it(item) }
    }
    
    fun addOnDetach(action: () -> Unit) {
        mDoOnDetachActions.add(action)
    }
    
    fun onDetach() {
        mDoOnDetachActions.forEach { it() }
    }
    
    override fun getWidgetContext(): Context = context
    
    override fun getWidgetView(): View = view
    
    operator fun plusAssign(value: Int) {
        binding.progressBar.progress += value
    }
    
    
    operator fun minusAssign(value: Int) {
        binding.progressBar.progress -= value
    }
    
    
    operator fun compareTo(value: Int): Int = binding.progressBar.progress.compareTo(value)
    
    operator fun invoke(value: Int) {
        binding.progressBar.progress = value
    }
    
    fun increaseAndJudge(value: Int = step): Boolean {
        this += value
        return binding.progressBar.progress >= max
    }
    
    fun isFinished(): Boolean = binding.progressBar.progress >= max
    
    override fun onStart() {
        if (!isFinished()) {
            try {
                onBeforeTraverse()
                mTraverser()
                afterTraverse()
            } catch (e: Exception) {
                onCatchException(e)
            } finally {
                onFinally()
            }
        }
    }
    
    override fun isReady(): Boolean = !isFinished()
}
