package com.tosmo.awidget.cw

import android.content.Context
import android.os.Handler
import android.os.HandlerThread


class ThreadTextProgresser<T, I : Iterator<T>>(context: Context, max: Int, iter: I) :
    TextProgresser<T, I>(context, max, iter) {
    
    private val mHandler = Handler(HandlerThread("progressx").apply { start() }.looper)
    
    private val mThread: Thread = Thread {
        while (iter.hasNext()) {
            val item = iter.next()
            onTraverse(item, iter)
            mHandler.post {
                if (increaseAndJudge()) {
                    onTraverseView(item)
                    onFinishTraverse()
                }
            }
        }
    }
    
    override val mTraverser: () -> Unit
        get() = {
            mThread.start()
        }
    
    init {
        addOnDetach {
            mThread.interrupt()
        }
    }
}