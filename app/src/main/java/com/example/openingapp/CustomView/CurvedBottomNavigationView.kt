package com.example.openingapp.CustomView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView

class CurvedBottomNavigationView : BottomNavigationView {
    lateinit var mPath: Path
    lateinit var mPaint: Paint;

    /** the CURVE_CIRCLE_RADIUS represent the radius of the fab button  */
    val CURVE_CIRCLE_RADIUS = 256 / 2

    // the coordinates of the first curve
    val mFirstCurveStartPoint: Point = Point()
    val mFirstCurveEndPoint: Point = Point()
    val mFirstCurveControlPoint1: Point = Point()
    val mFirstCurveControlPoint2: Point = Point()

    //the coordinates of the second curve
    var mSecondCurveStartPoint: Point = Point()
    val mSecondCurveEndPoint: Point = Point()
    val mSecondCurveControlPoint1: Point = Point()
    val mSecondCurveControlPoint2: Point = Point()
    var mNavigationBarWidth = 0
    var mNavigationBarHeight = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mPath = Path()
        mPaint = Paint()
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE)
        mPaint.setColor(Color.WHITE)
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // get width and height of navigation bar
        // Navigation bar bounds (width & height)
        mNavigationBarWidth = getWidth()
        mNavigationBarHeight = getHeight()
        // the coordinates (x,y) of the start point before curve
        mFirstCurveStartPoint.set(
            mNavigationBarWidth / 2 - CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS / 3,
            0
        )
        // the coordinates (x,y) of the end point after curve
        mFirstCurveEndPoint.set(
            mNavigationBarWidth / 2,
            CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4
        )
        // same thing for the second curve
        mSecondCurveStartPoint = mFirstCurveEndPoint
        mSecondCurveEndPoint.set(
            mNavigationBarWidth / 2 + CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS / 3,
            0
        )

        // the coordinates (x,y)  of the 1st control point on a cubic curve
        mFirstCurveControlPoint1.set(
            mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4,
            mFirstCurveStartPoint.y
        )
        // the coordinates (x,y)  of the 2nd control point on a cubic curve
        mFirstCurveControlPoint2.set(
            mFirstCurveEndPoint.x - CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS,
            mFirstCurveEndPoint.y
        )
        mSecondCurveControlPoint1.set(
            mSecondCurveStartPoint.x + CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS,
            mSecondCurveStartPoint.y
        )
        mSecondCurveControlPoint2.set(
            mSecondCurveEndPoint.x - CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4,
            mSecondCurveEndPoint.y
        )
        mPath.reset()
        mPath.moveTo(0F, 0F)
        mPath.lineTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat())
        mPath.cubicTo(
            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
        )
        mPath.cubicTo(
            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
        )
        mPath.lineTo(mNavigationBarWidth.toFloat(), 0F)
        mPath.lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
        mPath.lineTo(0F, mNavigationBarHeight.toFloat())
        mPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPath, mPaint)
    }
}