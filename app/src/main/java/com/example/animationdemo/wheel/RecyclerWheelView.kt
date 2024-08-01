package com.example.animationdemo.wheel

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import kotlin.math.abs
import kotlin.math.max

class RecyclerWheelView : RecyclerView {

    private val snapHelper by lazy { WheelSnapHelper(0.3f) }

    private var currentSelectedPosition = 0

    private var currentDeterminePosition = 0

    private var scrollDy = 0

    var wheelSlideListener: WheelSlideListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun setAdapter(adapter: Adapter<ViewHolder>?) {
        val wheelTestAdapter = adapter as RecyclerWheelDataAdapter
        super.setAdapter(wheelTestAdapter)
        snapHelper.attachToRecyclerView(this)
        post {
            select(0, false)
        }
    }


    override fun onScrolled(dx: Int, dy: Int) {
        val layoutManager = layoutManager ?: return
        val globalAdapter = (adapter as? RecyclerWheelDataAdapter) ?: return
        val snapView = snapHelper.findSnapView(layoutManager) ?: return
        val snapViewHolder = getChildViewHolder(snapView)
        val snapViewPositionGlobal = layoutManager.getPosition(snapView)
        Log.i("RecyclerWheelView", "snapViewPositionGlobal:$snapViewPositionGlobal")
        val snapViewPosition = globalAdapter.getRelativePosition(snapViewPositionGlobal)
        if (snapViewPosition == NO_POSITION) {
            return
        }
        updateSelectPosition(snapViewPosition)
        scrollDy += dy
        updateFraction(dx, dy, scrollDy)
    }

    private fun updateSelectPosition(selectedPositionNew: Int) {
        Log.i(
            "RecyclerWheelView", "updateSelectPosition:" +
                    "$currentSelectedPosition->$selectedPositionNew"
        )
        if (currentSelectedPosition != selectedPositionNew) {
            currentSelectedPosition = selectedPositionNew
//            onScrollingSelectListener?.invoke(selectedPositionNew)

        }
    }


    private fun updateFraction(dx: Int, dy: Int, scrollDy: Int) {
        val layoutManager = layoutManager as? LinearLayoutManager ?: return

        val wheelTestAdapter = adapter as RecyclerWheelDataAdapter

        val itemHeight =
            layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition())?.height
                ?: return
        // view视图中间
        val midViewHeight = scrollDy.toFloat() + height / 2f
        // 经过的item数量
        val passedItemCount = midViewHeight / itemHeight
        // 经过的item数量
        val curPos = passedItemCount.toInt()
        val fraction = getItemFraction(passedItemCount, curPos)
        val forward = dy > 0
        Log.i("onScrollStateChanged", "pos:$curPos,passedItem:$passedItemCount,fraction:$fraction")
        val next = if (dy > 0) curPos + 1 else curPos - 1
        val prev = if (dy > 0) curPos - 1 else curPos + 1
        Log.i("transition", "passed:${passedItemCount},cur:$curPos,fraction:$fraction")
        val prevFraction = getItemFraction(passedItemCount, prev)
        val nextFraction = getItemFraction(passedItemCount, next)
        Log.i("transition", "passed:${passedItemCount},prev:$prev,fraction:$prevFraction")
        Log.i(
            "transition",
            "passed:${passedItemCount},next:$next,fraction:${nextFraction}"
        )
        wheelSlideListener?.onSliding(forward, curPos, prevFraction, fraction, nextFraction)
    }


    private fun getItemFraction(passed: Float, pos: Int): Float {
        // passed  pos  fraction
        //  6.5     6     1
        //  7       6    0.5
        //  7       7    0.5
        //  7.5     6    0
        //  7.5     7    1
        val zeroPoint = pos + 0.5f
        if (passed - zeroPoint > 1f) return 0f
        val interestStart = zeroPoint - 1f
        val interestEnd = zeroPoint + 1f
        val fractionRange = interestStart..interestEnd
        val fraction = if (fractionRange.contains(passed)) {
            if (passed < zeroPoint) {
                return 1f - (zeroPoint - passed)
            } else {
                return 1f - (passed - zeroPoint)
            }
        } else {
            //不在范围内的原始值
            0f
        }
        Log.i("getItemFraction", "passed:$passed,pos:$pos,fraction:$fraction")
        return fraction
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            notifySelectDetermine(currentSelectedPosition)
        }
    }

    private fun notifySelectDetermine(value: Int) {
        Log.i(
            "RecyclerWheelView", "notifySelectDetermine:" +
                    "$currentDeterminePosition->$value"
        )
        if (currentDeterminePosition == value) {
            return
        }
        currentDeterminePosition = value
        wheelSlideListener?.onSelected(value)
    }


    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        Log.i("RecyclerWheelView", "fling,velocityX:$velocityX,velocityY:$velocityY")
        return snapHelper.onFling(velocityX, velocityY)
    }

    fun select(position: Int, smooth: Boolean) {
        Log.i("RecyclerWheelView", "position:$position,smooth:$smooth")
        val wheelTestAdapter = adapter as? RecyclerWheelDataAdapter ?: return
        val globalPosition = wheelTestAdapter.getGlobalPosition(position)

        val layoutManager = this.layoutManager as? LinearLayoutManager ?: return
        val first = layoutManager.findFirstVisibleItemPosition()
        val itemHeight = layoutManager.findViewByPosition(first)!!.height
        val offsetPosition = position - currentSelectedPosition
        val snapView = snapHelper.findSnapView(layoutManager)
        val snapPos = layoutManager.getPosition(snapView!!)
        Log.i(
            "RecyclerWheelView",
            "childCount:${layoutManager.itemCount},globalPosition:$globalPosition,snapPos:$snapPos"
        )
        val itemView = findViewHolderForLayoutPosition(globalPosition)?.itemView
        if (itemView != null) {
            Log.i("RecyclerWheelView", "snap to position:$")
            val snapDistance =
                snapHelper.calculateDistanceToFinalSnap(layoutManager, itemView)
                    ?: return
            if (smooth) {
                smoothScrollBy(0, snapDistance[1])
            } else {
                scrollBy(0, snapDistance[1])
            }
        } else {
            if (scrollDy == 0) {
                if (smooth) {
                    smoothScrollBy(0, itemHeight * offsetPosition - itemHeight / 2)
                } else {
                    scrollBy(0, itemHeight * offsetPosition - itemHeight / 2)
                }
            }
            if (smooth) {
                smoothScrollBy(0, itemHeight * offsetPosition)
            } else {
                scrollBy(0, itemHeight * offsetPosition)
            }
        }
    }


    class WheelSnapHelper(private val factor: Float) : LinearSnapHelper() {

        companion object {
            const val MILLISECONDS_PER_INCH = 150f
        }

        private lateinit var mRecyclerView: RecyclerWheelView

        private val mInterpolator by lazy { BounceEaseInOutInterpolator() }

        override fun attachToRecyclerView(recyclerView: RecyclerView?) {
            mRecyclerView = (recyclerView as RecyclerWheelView)
            super.attachToRecyclerView(recyclerView)
        }

        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            Log.i("WheelSnapHelper", "onFling......")
            val vx = (velocityX * factor).toInt()
            val vy = (velocityY * factor).toInt()
            val layoutManager = mRecyclerView.layoutManager ?: return false
            val minFlingVelocity = mRecyclerView.minFlingVelocity
            return ((abs(vy.toDouble()) > minFlingVelocity || abs(vx.toDouble()) > minFlingVelocity)
                    && snapFromFling(layoutManager, vx, vy))
        }


        private fun snapFromFling(
            layoutManager: LayoutManager, velocityX: Int,
            velocityY: Int
        ): Boolean {
            if (layoutManager !is ScrollVectorProvider) {
                return false
            }

            val smoothScroller = createScroller(layoutManager) ?: return false
            val scrollTargetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY)
            Log.i("RecyclerWheelView", "scrollTargetPosition:${scrollTargetPosition}")
            if (scrollTargetPosition == NO_POSITION) {
                return false
            }
            val adapter = mRecyclerView.adapter as? RecyclerWheelDataAdapter ?: return false
            val actualTargetPosition = adapter.getDataGlobalPosition(scrollTargetPosition)
            smoothScroller.targetPosition = actualTargetPosition
            Log.i("RecyclerWheelView", "actualTargetPosition:${actualTargetPosition}")
            layoutManager.startSmoothScroll(smoothScroller)
            return true
        }


        override fun createScroller(layoutManager: LayoutManager?): SmoothScroller? {
            if (layoutManager !is ScrollVectorProvider) {
                return null
            }
            return object : LinearSmoothScroller(mRecyclerView.context) {
                override fun onTargetFound(targetView: View, state: State, action: Action) {
                    Log.i("WheelSnapHelper", "onTargetFound,state:$state,action:$action")
                    val snapDistances = calculateDistanceToFinalSnap(
                        mRecyclerView.layoutManager!!,
                        targetView
                    )
                    val dx = snapDistances!![0]
                    val dy = snapDistances[1]
                    val time = calculateTimeForDeceleration(
                        max(abs(dx.toDouble()), abs(dy.toDouble()))
                            .toInt()
                    )
                    if (time > 0) {
                        action.update(dx, dy, time, mInterpolator)
                    }
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }
        }
    }
}
