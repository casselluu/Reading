package io.legado.app.ui.book.read.page.delegate

import android.graphics.Canvas
import android.view.MotionEvent
import io.legado.app.ui.book.read.page.PageView
import kotlin.math.abs

class ScrollPageDelegate(pageView: PageView) : PageDelegate(pageView) {

    override fun onAnimStart() {
        if (!atTop && !atBottom) {
            stopScroll()
            return
        }
        val distanceY: Float
        when (mDirection) {
            Direction.NEXT -> distanceY =
                if (isCancel) {
                    var dis = viewHeight - startY + touchY
                    if (dis > viewHeight) {
                        dis = viewHeight.toFloat()
                    }
                    viewHeight - dis
                } else {
                    -(touchY + (viewHeight - startY))
                }
            else -> distanceY =
                if (isCancel) {
                    -(touchY - startY)
                } else {
                    viewHeight - (touchY - startY)
                }
        }

        startScroll(0, touchY.toInt(), 0, distanceY.toInt())
    }

    override fun onDraw(canvas: Canvas) {

    }

    override fun onAnimStop() {
        if (!isCancel) {
            pageView.fillPage(mDirection)
        }
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (!isMoved && abs(distanceX) < abs(distanceY)) {
            if (distanceY < 0) {
                if (atTop) {
                    val event = e1.toAction(MotionEvent.ACTION_UP)
                    curPage.dispatchTouchEvent(event)
                    event.recycle()
                    //如果上一页不存在
                    if (!hasPrev()) {
                        noNext = true
                        return true
                    }
                    setDirection(Direction.PREV)
                    setBitmap()
                }
            } else {
                if (atBottom) {
                    val event = e1.toAction(MotionEvent.ACTION_UP)
                    curPage.dispatchTouchEvent(event)
                    event.recycle()
                    //如果不存在表示没有下一页了
                    if (!hasNext()) {
                        noNext = true
                        return true
                    }
                    setDirection(Direction.NEXT)
                    setBitmap()
                }
            }
            isMoved = true
        }
        if ((atTop && mDirection != Direction.PREV) || (atBottom && mDirection != Direction.NEXT) || mDirection == Direction.NONE) {
            //传递触摸事件到textView
            curPage.dispatchTouchEvent(e2)
        }
        if (isMoved) {
            isCancel = if (mDirection == Direction.NEXT) distanceY < 0 else distanceY > 0
            isRunning = true
            //设置触摸点
            setTouchPoint(e2.x, e2.y)
        }
        return isMoved
    }

}