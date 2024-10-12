package com.library.sunview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class SunView : View {
    private var mPaint: Paint? = null
    private var mStartTime: String? = null
    private var mEndTime: String? = null
    private var mCurrentTime: String? = null

    private var mTimeTextSize = 0f
    private var mTimeTextFont: Int = R.font.roboto_regular
    private var mArcDashWidth = 0f
    private var mArcDashGapWidth = 0f
    private var mArcDashHeight = 0f
    private var mArcRadius = 0f
    private var mDefaultWeatherIconSize = 0f
    private var mTextPadding = 0f


    private var mArcColor = 0
    private var mArcSolidColor = 0
    private var mBottomLineColor = 0
    private var mTimeTextColor = 0
    private var mSunColor = 0

    private var mBottomLineHeight = 0f
    private var mArcOffsetAngle = 0f

    private var mIs24HourFormat: Boolean = false

    private var mWeatherDrawable: Drawable? = null

    private var mDateFormat: SimpleDateFormat? = null

    private var offsetX = 0f
    private var offsetY = 0f


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context, attrs)
    }

    fun setArcSolidColor(color: Int) { //拱型內部顏色
        //mArcSolidColor = Color.parseColor(color);
        mArcSolidColor = color
    }

    fun setArcColor(color: Int) { //拱形虛線顏色
        mArcColor = color
    }

    fun setBottomLineColor(color: Int) { //拱形底線顏色
        mBottomLineColor = color
    }

    fun setTimeTextColor(color: Int) { //字體顏色
        mTimeTextColor = color
    }

    fun setIsp24HourFormat(is24HourFormat: Boolean) { //字體顏色
        mIs24HourFormat = is24HourFormat
    }

    fun setSunColor(color: Int) { //太陽顏色
        mSunColor = color
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.SunViewStyle)
        mStartTime = attrArray.getString(R.styleable.SunViewStyle_startTime)
        mEndTime = attrArray.getString(R.styleable.SunViewStyle_endTime)
        mCurrentTime = attrArray.getString(R.styleable.SunViewStyle_currentTime)
        mTimeTextSize = attrArray.getDimension(
            R.styleable.SunViewStyle_timeTextSize,
            resources.getDimension(R.dimen.default_text_size)
        )
        mTimeTextColor = attrArray.getColor(
            R.styleable.SunViewStyle_timeTextColor,
            resources.getColor(R.color.default_text_color)
        )
        mTimeTextFont = attrArray.getResourceId(
            R.styleable.SunViewStyle_timeTextFont,
            R.font.roboto_regular
        )


        mWeatherDrawable = attrArray.getDrawable(R.styleable.SunViewStyle_weatherDrawable)
        mBottomLineHeight = attrArray.getDimension(
            R.styleable.SunViewStyle_bottomLineHeight,
            resources.getDimension(R.dimen.default_bottom_line_height)
        )
        mBottomLineColor = attrArray.getColor(
            R.styleable.SunViewStyle_bottomLineColor,
            resources.getColor(R.color.default_bottom_line_color)
        )
        mArcColor = attrArray.getColor(
            R.styleable.SunViewStyle_arcColor,
            resources.getColor(R.color.default_arc_color)
        )
        mArcSolidColor = attrArray.getColor(
            R.styleable.SunViewStyle_arcSolidColor,
            resources.getColor(R.color.default_arc_solid_color)
        )
        mArcDashWidth = attrArray.getDimension(
            R.styleable.SunViewStyle_arcDashWidth,
            resources.getDimension(R.dimen.default_arc_dash_width)
        )
        mArcDashGapWidth = attrArray.getDimension(
            R.styleable.SunViewStyle_arcDashGapWidth,
            resources.getDimension(R.dimen.default_arc_dash_gap_width)
        )
        mArcDashHeight = attrArray.getDimension(
            R.styleable.SunViewStyle_arcDashHeight,
            resources.getDimension(R.dimen.default_arc_dash_height)
        )
        mArcRadius = attrArray.getDimension(R.styleable.SunViewStyle_arcRadius, 0f)
        mArcOffsetAngle = attrArray.getInteger(R.styleable.SunViewStyle_arcOffsetAngle, 0).toFloat()

        mIs24HourFormat = attrArray.getBoolean(R.styleable.SunViewStyle_is24HourFormat, true)

        mSunColor = attrArray.getColor(
            R.styleable.SunViewStyle_sunColor,
            resources.getColor(R.color.default_sun_color)
        )
        mTextPadding = attrArray.getDimension(R.styleable.SunViewStyle_textPadding, 0f)
        attrArray.recycle()

        mDefaultWeatherIconSize = resources.getDimension(R.dimen.default_weather_icon_size)

        mPaint = Paint()
        mPaint!!.isAntiAlias = true

        setDefaultTime()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true


        mTimeTextSize = resources.getDimension(R.dimen.default_text_size)
        mTimeTextColor = resources.getColor(R.color.default_text_color)
        mTimeTextFont = R.font.roboto_regular

        mBottomLineHeight = resources.getDimension(R.dimen.default_bottom_line_height)
        mBottomLineColor = resources.getColor(R.color.default_bottom_line_color)
        mArcColor = resources.getColor(R.color.default_arc_color)
        mArcSolidColor = resources.getColor(R.color.default_arc_solid_color)
        mArcDashWidth = resources.getDimension(R.dimen.default_arc_dash_width)
        mArcDashGapWidth = resources.getDimension(R.dimen.default_arc_dash_gap_width)
        mArcDashHeight = resources.getDimension(R.dimen.default_arc_dash_height)
        mIs24HourFormat = true
        mDefaultWeatherIconSize = resources.getDimension(R.dimen.default_weather_icon_size)
        mSunColor = resources.getColor(R.color.default_sun_color)
        setDefaultTime()
    }

    fun setStartTime(s: String?) {
        mStartTime = s
    }

    fun setEndTime(s: String?) {
        mEndTime = s
    }

    fun setCurrentTime(s: String?) {
        var s = s
        val str_Start = mStartTime!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val str_Current = s!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val current = str_Current[0].toInt() * 60 + str_Current[1].toInt()
        val Start = str_Start[0].toInt() * 60 + str_Start[1].toInt()
        if (current < Start) {
            mArcSolidColor = resources.getColor(R.color.Transparent)
            s = mStartTime
        }
        mCurrentTime = s
    }

    private fun setDefaultTime() {
        mDateFormat = SimpleDateFormat(DATE_FORMAT)

        if (TextUtils.isEmpty(mStartTime)) {
            mStartTime = resources.getString(R.string.default_start_time)
        }

        if (TextUtils.isEmpty(mEndTime)) {
            mEndTime = resources.getString(R.string.default_end_time)
        }

        if (TextUtils.isEmpty(mCurrentTime)) {
            mCurrentTime = mDateFormat!!.format(Date())
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)


        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            if (mArcRadius == 0f) {
                setMeasuredDimension(width, height)
            } else {
                width = (mArcRadius * 2 + widthGap).toInt()
                height = (mArcRadius + heightGap).toInt()
                setMeasuredDimension(width, height)
            }
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = if (mArcRadius == 0f) {
                (height - heightGap) * 2 + widthGap
            } else {
                (mArcRadius * 2 + widthGap).toInt()
            }
            setMeasuredDimension(width, height)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = if (mArcRadius == 0f) {
                (width - widthGap) / 2 + heightGap
            } else {
                (mArcRadius + heightGap).toInt()
            }

            setMeasuredDimension(width, height)
        } else {
            setMeasuredDimension(width, height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        drawLine(canvas)
        drawArc(canvas)
    }

    /**
     * 畫圓弧
     *
     * @param canvas
     */
    private fun drawArc(canvas: Canvas) {
        radius


        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.color = mArcColor
        mPaint!!.strokeWidth = mArcDashHeight


        val left = paddingLeft + (width - paddingLeft - paddingRight - 2 * mArcRadius) / 2
        val top = height - mArcRadius - bottomHeightGap
        val right = left + 2 * mArcRadius
        val bottom = top + 2 * mArcRadius
        val rectF = RectF(left, top, right, bottom)

        val offsetPoint = calcArcEndPointXY(
            rectF.centerX(),
            rectF.centerY(),
            rectF.width() / 2,
            180 + mArcOffsetAngle
        )
        offsetX = offsetPoint.x - left
        offsetY = rectF.centerY() - offsetPoint.y

        rectF.top += offsetY
        rectF.bottom += offsetY

        val effect = DashPathEffect(
            floatArrayOf(
                mArcDashWidth,
                mArcDashGapWidth,
                mArcDashWidth,
                mArcDashGapWidth
            ), 0f
        )
        mPaint!!.setPathEffect(effect)
        canvas.drawArc(rectF, 180 + mArcOffsetAngle, 180 - mArcOffsetAngle * 2, false, mPaint!!)

        drawSolidArc(canvas, rectF)

        val startPoint = calcArcEndPointXY(
            rectF.centerX(),
            rectF.centerY(),
            rectF.width() / 2,
            180 + mArcOffsetAngle
        )

        val endPoint = calcArcEndPointXY(
            rectF.centerX(),
            rectF.centerY(),
            rectF.width() / 2,
            180 + mArcOffsetAngle + (180 - mArcOffsetAngle * 2)
        )

        drawPoint(canvas, startPoint.x, startPoint.y)
        drawPoint(canvas, endPoint.x, endPoint.y)
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float) {
        mPaint!!.color = Color.parseColor("#B3E9D42B")
        mPaint!!.style = Paint.Style.FILL

        canvas.drawCircle(x, y, mDefaultWeatherIconSize / 3, mPaint!!)
    }

    private val radius: Unit
        get() {
            if (mArcRadius == 0f) {
                val width = width - widthGap
                val height = height - heightGap

                mArcRadius = if (width / 2 > height) {
                    height.toFloat()
                } else {
                    (width / 2).toFloat()
                }
            }
        }

    /**
     * 繪製天氣圖標
     *
     * @param canvas
     * @param point
     */
    private fun drawWeatherDrawable(canvas: Canvas, point: PointF) {
        if (mWeatherDrawable != null) {
            val dw =
                if (mWeatherDrawable!!.intrinsicWidth == 0) mDefaultWeatherIconSize.toInt() else mWeatherDrawable!!.intrinsicWidth
            val dh =
                if (mWeatherDrawable!!.intrinsicHeight == 0) mDefaultWeatherIconSize.toInt() else mWeatherDrawable!!.intrinsicHeight

            val rect = Rect()
            rect.left = (point.x - dw / 2).toInt()
            rect.top = (point.y - dh / 2).toInt()
            rect.right = rect.left + dw
            rect.bottom = rect.top + dh

            mWeatherDrawable!!.bounds = rect
            mWeatherDrawable!!.draw(canvas)
        } else {
            drawSun(canvas, point)
        }
    }

    /**
     * 畫太陽
     *
     * @param canvas
     * @param point
     */
    private fun drawSun(canvas: Canvas, point: PointF) {
        // Vẽ mặt trời
        mPaint!!.color = mSunColor
        mPaint!!.style = Paint.Style.FILL
        canvas.drawCircle(point.x, point.y, mDefaultWeatherIconSize / 2, mPaint!!)

        val numberOfCircles = 8
        val circleRadius = mDefaultWeatherIconSize / 9
        val distanceFromSun = mDefaultWeatherIconSize / 1.4f

        for (i in 0 until numberOfCircles) {
            val angle = (i * 2 * Math.PI / numberOfCircles).toDouble()

            val circleX = point.x + distanceFromSun * cos(angle).toFloat()
            val circleY = point.y + distanceFromSun * sin(angle).toFloat()

            canvas.drawCircle(circleX, circleY, circleRadius, mPaint!!)
        }
    }

    /**
     * 畫實心圓弧
     *
     * @param canvas
     * @param rectF
     */
    private var animatedAngle = 0f // Biến lưu trữ góc đã animation
    private var animator: ValueAnimator? = null // Biến animator

    private fun drawSolidArc(canvas: Canvas, rectF: RectF) {
        var angle = 0
        try {
            val start = mDateFormat!!.parse(mStartTime).time
            val end = mDateFormat!!.parse(mEndTime).time
            val current = mDateFormat!!.parse(mCurrentTime).time
            val factor = 1.0f * (current - start) / (end - start)
            angle = if (factor > 1) {
                (180 - mArcOffsetAngle * 2).toInt()
            } else {
                (factor * (180 - mArcOffsetAngle * 2)).toInt()
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, angle.toFloat()).apply {
                duration = 1500
                addUpdateListener { animation ->
                    animatedAngle = animation.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }

        mPaint!!.style = Paint.Style.FILL
        mPaint!!.color = mArcSolidColor
        canvas.drawArc(rectF, 180 + mArcOffsetAngle, animatedAngle, false, mPaint!!)

        val point = calcArcEndPointXY(
            rectF.centerX(),
            rectF.centerY(),
            rectF.width() / 2,
            180 + mArcOffsetAngle + animatedAngle
        )

        drawTriangle(canvas, rectF, point)
        drawWeatherDrawable(canvas, point)

        drawText(canvas, rectF)
    }


    private fun drawText(canvas: Canvas, rect: RectF) {
        mPaint!!.setPathEffect(null)
        mPaint!!.style = Paint.Style.FILL

        mPaint!!.color = mTimeTextColor
        mPaint!!.textSize = mTimeTextSize
        mPaint!!.typeface = ResourcesCompat.getFont(context, mTimeTextFont)

        val startTextWidth = getTextWidth(mPaint, formatTime(mStartTime))
        val endTextWidth = getTextWidth(mPaint, formatTime(mEndTime))

        val textHeight: Int = textHeight + 15

        mPaint!!.setPathEffect(null)
        mPaint!!.style = Paint.Style.FILL

        canvas.drawText(
            formatTime(mStartTime),
            rect.left - startTextWidth / 2 + offsetX,
            rect.centerY() - offsetY + textHeight + mTextPadding,
            mPaint!!
        )
        canvas.drawText(
            formatTime(mEndTime),
            rect.right - endTextWidth / 2 - 2 - offsetX * 2,
            rect.centerY() - offsetY + textHeight + mTextPadding,
            mPaint!!
        )
    }

    private fun formatTime(time: String?): String {
        if (time.isNullOrEmpty()) return "invalid"

        val sdfInput = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfOutput = SimpleDateFormat(if (mIs24HourFormat) "HH:mm" else "hh:mm a", Locale.getDefault())

        return try {
            val date = sdfInput.parse(time)
            sdfOutput.format(date!!)
        } catch (e: ParseException) {
            time
        }
    }

    private val textHeight: Int
        get() {
            mPaint!!.textSize = mTimeTextSize
            val fm = mPaint!!.fontMetrics // 得到系統默認字體屬性
            return ceil((fm.descent - fm.ascent).toDouble()).toInt()
        }

    /**
     * 畫三角形
     *
     * @param canvas
     * @param rect
     * @param point
     */
    private fun drawTriangle(canvas: Canvas, rect: RectF, point: PointF) {
        //因為計算損失精度，所以這裡用1像素來微調

        val path = Path()
        path.moveTo(rect.left - 1 + offsetX, rect.centerY() - offsetY) // 此點為多邊形的起點
        path.lineTo(point.x - 1, point.y - 1)
        path.lineTo(point.x - 1, rect.centerY() - offsetY)
        path.close() //使這些點構成封閉的多邊形
        canvas.drawPath(path, mPaint!!)
    }

    /**
     * 畫底部線條
     *
     * @param canvas
     */
    private fun drawLine(canvas: Canvas) {
        mPaint!!.color = mBottomLineColor
        mPaint!!.strokeWidth = mBottomLineHeight
        mPaint!!.style = Paint.Style.FILL

        canvas.drawLine(
            paddingLeft.toFloat(),
            (height - bottomHeightGap).toFloat(),
            (width - paddingRight).toFloat(),
            (height - bottomHeightGap).toFloat(),
            mPaint!!
        )
    }


    private val widthGap: Int
        /**
         * 寬度除了圓弧以外的空隙
         * @return
         */
        get() = paddingLeft + paddingRight + getTextWidth(mStartTime) / 2 + getTextWidth(mEndTime) / 2

    private val heightGap: Int
        /**
         * 高度除了圓弧以外的空隙
         * @return
         */
        get() = (paddingTop + paddingBottom + mTextPadding + mBottomLineHeight + weatherHeight / 2).toInt() + this.textHeight

    private val bottomHeightGap: Int
        /**
         * 圓弧底部空隙
         * @return
         */
        get() = (paddingBottom + textHeight + mTextPadding).toInt() + 10

    private val weatherHeight: Int
        /**
         * 天氣圖標高度
         * @return
         */
        get() {
            if (mWeatherDrawable == null) {
                return mDefaultWeatherIconSize.toInt() * 2
            }
            if (mWeatherDrawable!!.intrinsicHeight == 0) {
                return mDefaultWeatherIconSize.toInt() * 2
            }
            return mWeatherDrawable!!.intrinsicHeight
        }

    private val weatherWidth: Int
        /**
         * 天氣圖標寬度
         * @return
         */
        get() {
            if (mWeatherDrawable == null) {
                return mDefaultWeatherIconSize.toInt() * 2
            }
            if (mWeatherDrawable!!.intrinsicWidth == 0) {
                return mDefaultWeatherIconSize.toInt() * 2
            }
            return mWeatherDrawable!!.intrinsicWidth
        }


    //依圓心坐標，半徑，扇形角度，計算出扇形終射線與圓弧交叉點的xy坐標
    private fun calcArcEndPointXY(cirX: Float, cirY: Float, radius: Float, cirAngle: Float): PointF {
        val point = PointF()

        //將角度轉換為弧度
        var arcAngle = (Math.PI * cirAngle / 180.0).toFloat()

        //當角度= 90°時，弧度=Π/ 2 =Π* 90°/ 180°=Π*角度/ 180°，
        //當角度= 180°時，弧度=Π=Π* 180°/ 180°=Π*角度/ 180°，
        //所以弧度（弧度）=Π* angle / 180（1弧度等於半徑的圓弧對應的圓心角，1度是1/360圓心角）
        if (cirAngle < 90) //直角的三角形斜邊是半徑
        {
            point.x = cirX + cos(arcAngle.toDouble()).toFloat() * radius
            point.y = cirY + sin(arcAngle.toDouble()).toFloat() * radius
        } else if (cirAngle == 90f) {
            point.x = cirX
            point.y = cirY + radius
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = (Math.PI * (180 - cirAngle) / 180.0).toFloat()
            point.x = cirX - cos(arcAngle.toDouble()).toFloat() * radius
            point.y = cirY + sin(arcAngle.toDouble()).toFloat() * radius
        } else if (cirAngle == 180f) {
            point.x = cirX - radius
            point.y = cirY
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = (Math.PI * (cirAngle - 180) / 180.0).toFloat()
            point.x = cirX - cos(arcAngle.toDouble()).toFloat() * radius
            point.y = cirY - sin(arcAngle.toDouble()).toFloat() * radius
        } else if (cirAngle == 270f) {
            point.x = cirX
            point.y = cirY - radius
        } else {
            arcAngle = (Math.PI * (360 - cirAngle) / 180.0).toFloat()
            point.x = cirX + cos(arcAngle.toDouble()).toFloat() * radius
            point.y = cirY - sin(arcAngle.toDouble()).toFloat() * radius
        }

        return point
    }


    private fun getTextWidth(paint: Paint?, str: String?): Int {
        var iRet = 0
        if (!str.isNullOrEmpty()) {
            val len = str.length
            val widths = FloatArray(len)
            paint!!.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    private fun getTextWidth(str: String?): Int {
        mPaint!!.textSize = mTimeTextSize
        return getTextWidth(mPaint, str)
    }

    companion object {
        private const val DATE_FORMAT = "HH:mm"
    }
}