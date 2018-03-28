package com.example.rumpilstilstkin.kotlintest.adapters

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup


class AwesomeLayoutManager(context: Context) : RecyclerView.LayoutManager() {
    private val viewCache = SparseArray<View>()
    private var mAnchorPos: Int = 0

    private val anchorView: View?
        get() {
            val childCount = childCount
            val mainRect = Rect(0, 0, width, height)
            val maxSquare = 0
            var anchorView: View? = null
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                val top = getDecoratedTop(view)
                val bottom = getDecoratedBottom(view)
                val left = getDecoratedLeft(view)
                val right = getDecoratedRight(view)
                val viewRect = Rect(left, top, right, bottom)
                val intersect = viewRect.intersect(mainRect)
                if (intersect) {
                    val square = viewRect.width() * viewRect.height()
                    if (square > maxSquare) {
                        anchorView = view
                    }
                }
            }
            return anchorView
        }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler)
        fill(recycler)
        mAnchorPos = 0
    }

    private fun fill(recycler: RecyclerView.Recycler?) {

        val anchorView = anchorView
        viewCache.clear()
        run {
            var i = 0
            val cnt = childCount
            while (i < cnt) {
                val view = getChildAt(i)
                val pos = getPosition(view)
                viewCache.put(pos, view)
                i++
            }
        }

        for (i in 0 until viewCache.size()) {
            detachView(viewCache.valueAt(i))
        }

        fillUp(anchorView, recycler)
        fillDown(anchorView, recycler)

        for (i in 0 until viewCache.size()) {
            recycler!!.recycleView(viewCache.valueAt(i))
        }

        updateViewScale()
    }

    private fun fillUp(anchorView: View?, recycler: RecyclerView.Recycler?) {
        val anchorPos: Int
        var anchorTop = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorTop = getDecoratedTop(anchorView)
        }
        else {
            anchorPos = mAnchorPos
        }

        var fillUp = true
        var pos = anchorPos - 1
        var viewBottom = anchorTop
        val viewHeight = (height * ITEM_HEIGHT_PERCENT).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        while (fillUp && pos >= 0) {
            var view: View? = viewCache.get(pos)
            if (view == null) {
                view = recycler!!.getViewForPosition(pos)
                addView(view!!, 0)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
                layoutDecorated(view, 0, viewBottom - viewHeight, decoratedMeasuredWidth, viewBottom)
            }
            else {
                attachView(view, 0)
                viewCache.remove(pos)
            }
            viewBottom = getDecoratedTop(view)
            fillUp = viewBottom > 0
            pos--
        }
    }

    private fun fillDown(anchorView: View?, recycler: RecyclerView.Recycler?) {
        val anchorPos: Int
        var anchorTop = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorTop = getDecoratedTop(anchorView)
        }
        else {
            anchorPos = mAnchorPos
        }

        var pos = anchorPos
        var fillDown = true
        val height = height
        var viewTop = anchorTop
        val itemCount = itemCount
        val viewHeight = (getHeight() * ITEM_HEIGHT_PERCENT).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY)

        while (fillDown && pos < itemCount) {
            var view: View? = viewCache.get(pos)
            if (view == null) {
                view = recycler!!.getViewForPosition(pos)
                addView(view)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view!!)
                layoutDecorated(view, 0, viewTop, decoratedMeasuredWidth, viewTop + viewHeight)
            }
            else {
                attachView(view)
                viewCache.remove(pos)
            }
            viewTop = getDecoratedBottom(view)
            fillDown = viewTop <= height
            pos++
        }
    }

    private fun fillLeft(anchorView: View?, recycler: RecyclerView.Recycler?) {
        val anchorPos: Int
        var anchorLeft = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorLeft = getDecoratedLeft(anchorView)
        }
        else {
            anchorPos = mAnchorPos
        }

        var fillLeft = true
        var pos = anchorPos - 1
        var viewRight = anchorLeft
        val width = width
        val height = height
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
        while (fillLeft && pos >= 0) {
            var view: View? = viewCache.get(pos)
            if (view == null) {
                view = recycler!!.getViewForPosition(pos)
                addView(view!!, 0)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredHeight = getDecoratedMeasuredHeight(view)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
                layoutDecorated(view, viewRight - decoratedMeasuredWidth, 0, viewRight, decoratedMeasuredHeight)
            }
            else {
                attachView(view, 0)
                viewCache.remove(pos)
            }
            viewRight = getDecoratedLeft(view)
            fillLeft = viewRight > 0
            pos--
        }
    }

    private fun fillRight(anchorView: View?, recycler: RecyclerView.Recycler?) {
        val anchorPos: Int
        var anchorLeft = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorLeft = getDecoratedLeft(anchorView)
        }
        else {
            anchorPos = mAnchorPos
        }

        var pos = anchorPos
        var fillRight = true
        var viewLeft = anchorLeft
        val itemCount = itemCount
        val width = width
        val height = height
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)

        while (fillRight && pos < itemCount) {
            var view: View? = viewCache.get(pos)
            if (view == null) {
                view = recycler!!.getViewForPosition(pos)
                addView(view)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredHeight = getDecoratedMeasuredHeight(view!!)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
                layoutDecorated(view, viewLeft, 0, viewLeft + decoratedMeasuredWidth, decoratedMeasuredHeight)
            }
            else {
                attachView(view)
                viewCache.remove(pos)
            }
            viewLeft = getDecoratedRight(view)
            fillRight = viewLeft <= width
            pos++
        }
    }

    private fun updateViewScale() {
        val childCount = childCount
        val height = height
        val thresholdPx = (height * SCALE_THRESHOLD_PERCENT).toInt()
        for (i in 0 until childCount) {
            var scale = 1f
            val view = getChildAt(i)
            val viewTop = getDecoratedTop(view)
            if (viewTop >= thresholdPx) {
                val delta = viewTop - thresholdPx
                scale = (height - delta) / height.toFloat()
                scale = Math.max(scale, 0f)
            }
            view.pivotX = (view.height / 2).toFloat()
            view.pivotY = (view.height / -2).toFloat()
            view.scaleX = scale
            view.scaleY = scale
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        if (position >= itemCount) {
            Log.e(TAG, "Cannot scroll to $position, item count is $itemCount")
            return
        }

        val scroller = object : LinearSmoothScroller(recyclerView!!.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@AwesomeLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    private fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0))
        val direction = if (targetPosition < firstChildPos) -1 else 1

        return PointF(0f, -1f)
    }

    override fun canScrollVertically() = true

    override fun canScrollHorizontally() = false

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val delta = scrollHorizontallyInternal(dx)
        offsetChildrenHorizontal(-delta)
        fill(recycler)
        return delta
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val delta = scrollVerticallyInternal(dy)
        offsetChildrenVertical(-delta)
        fill(recycler)
        return delta
    }

    private fun scrollVerticallyInternal(dy: Int): Int {
        val childCount = childCount
        val itemCount = itemCount
        if (childCount == 0) {
            return 0
        }

        val topView = getChildAt(0)
        val bottomView = getChildAt(childCount - 1)

        val viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView)
        if (viewSpan <= height) {
            return 0
        }

        var delta = 0
        if (dy < 0) {
            val firstView = getChildAt(0)
            val firstViewAdapterPos = getPosition(firstView)
            if (firstViewAdapterPos > 0) {
                delta = dy
            }
            else {
                val viewTop = getDecoratedTop(firstView)
                delta = Math.max(viewTop, dy)
            }
        }
        else if (dy > 0) {
            val lastView = getChildAt(childCount - 1)
            val lastViewAdapterPos = getPosition(lastView)
            if (lastViewAdapterPos < itemCount - 1) {
                delta = dy
            }
            else {
                val viewBottom = getDecoratedBottom(lastView)
                val parentBottom = height
                delta = Math.min(viewBottom - parentBottom, dy)
            }
        }
        return delta
    }

    private fun scrollHorizontallyInternal(dx: Int): Int {
        val childCount = childCount
        val itemCount = itemCount
        if (childCount == 0) {
            return 0
        }

        val leftView = getChildAt(0)
        val rightView = getChildAt(childCount - 1)

        val viewSpan = getDecoratedRight(rightView) - getDecoratedLeft(leftView)
        if (viewSpan <= width) {
            return 0
        }

        var delta = 0
        if (dx < 0) {
            val firstView = getChildAt(0)
            val firstViewAdapterPos = getPosition(firstView)
            if (firstViewAdapterPos > 0) {
                delta = dx
            }
            else {
                val viewLeft = getDecoratedLeft(firstView)
                delta = Math.max(viewLeft, dx)
            }
        }
        else if (dx > 0) {
            val lastView = getChildAt(childCount - 1)
            val lastViewAdapterPos = getPosition(lastView)
            if (lastViewAdapterPos < itemCount - 1) {
                delta = dx
            }
            else {
                val viewRight = getDecoratedRight(lastView)
                delta = Math.min(viewRight - width, dx)
            }
        }
        return delta
    }

    private fun measureChildWithDecorationsAndMargin(child: View?, widthSpec: Int, heightSpec: Int) {
        var widthSpec = widthSpec
        var heightSpec = heightSpec
        val decorRect = Rect()
        calculateItemDecorationsForChild(child, decorRect)
        val lp = child!!.layoutParams as RecyclerView.LayoutParams
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
                lp.rightMargin + decorRect.right)
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
                lp.bottomMargin + decorRect.bottom)
        child.measure(widthSpec, heightSpec)
    }

    private fun updateSpecWithExtra(spec: Int, startInset: Int, endInset: Int): Int {
        if (startInset == 0 && endInset == 0) {
            return spec
        }
        val mode = View.MeasureSpec.getMode(spec)
        return if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode)
        }
        else spec
    }

    companion object {

        private val TAG = "AwesomeLayoutManager"

        private val SCALE_THRESHOLD_PERCENT = 0.80f
        private val ITEM_HEIGHT_PERCENT = 0.75f
    }
}