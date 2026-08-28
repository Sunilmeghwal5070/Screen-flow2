package com.example.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.MainActivity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * System-level floating draggable recording bubble and smart overlay menu.
 * Appears when recording is active and overlay permission is granted.
 */
class FloatingBubbleManager(private val context: Context) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private var bubbleView: View? = null
    private var smartMenuView: View? = null

    private var isBubbleShowing = false
    private var isMenuExpanded = false
    private var isPaused = false

    private var screenWidth = 1080
    private var screenHeight = 1920

    private val density = context.resources.displayMetrics.density

    init {
        updateScreenDimensions()
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()

    private fun updateScreenDimensions() {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    }

    fun show() {
        if (!Settings.canDrawOverlays(context)) return
        if (isBubbleShowing) return

        mainHandler.post {
            try {
                updateScreenDimensions()
                createBubbleView()
                isBubbleShowing = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateState(paused: Boolean) {
        this.isPaused = paused
        mainHandler.post {
            refreshMenuState()
        }
    }

    fun hide() {
        mainHandler.post {
            try {
                if (smartMenuView != null) {
                    windowManager.removeViewImmediate(smartMenuView)
                    smartMenuView = null
                }
                if (bubbleView != null) {
                    windowManager.removeViewImmediate(bubbleView)
                    bubbleView = null
                }
            } catch (_: Exception) {}
            isBubbleShowing = false
            isMenuExpanded = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createBubbleView() {
        val bubbleSizePx = dpToPx(56)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            bubbleSizePx,
            bubbleSizePx,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = screenWidth - bubbleSizePx - dpToPx(16)
            y = dpToPx(180)
        }

        val frameLayout = FrameLayout(context).apply {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                colors = intArrayOf(
                    Color.parseColor("#EF4444"),
                    Color.parseColor("#B91C1C")
                )
                setStroke(dpToPx(2), Color.WHITE)
            }
            background = bg
            elevation = dpToPx(8).toFloat()
        }

        val icon = ImageView(context).apply {
            val d = ContextCompat.getDrawable(context, android.R.drawable.presence_video_online)
                ?: ContextCompat.getDrawable(context, android.R.drawable.ic_media_play)
            setImageDrawable(d)
            setColorFilter(Color.WHITE)
            val pad = dpToPx(14)
            setPadding(pad, pad, pad, pad)
        }

        frameLayout.addView(
            icon,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Drag and Click handling
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = true
        val clickThresholdPx = dpToPx(8)

        frameLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()

                    if (abs(dx) > clickThresholdPx || abs(dy) > clickThresholdPx) {
                        isClick = false
                    }

                    params.x = (initialX + dx).coerceIn(0, screenWidth - bubbleSizePx)
                    params.y = (initialY + dy).coerceIn(dpToPx(32), screenHeight - bubbleSizePx - dpToPx(48))

                    try {
                        windowManager.updateViewLayout(frameLayout, params)
                        if (isMenuExpanded && smartMenuView != null) {
                            updateMenuPosition(params.x, params.y)
                        }
                    } catch (_: Exception) {}
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        toggleSmartMenu(params.x, params.y)
                    } else {
                        // Snap to nearest screen edge
                        val middle = screenWidth / 2
                        val targetX = if (params.x + bubbleSizePx / 2 < middle) dpToPx(8) else (screenWidth - bubbleSizePx - dpToPx(8))
                        animateSnap(params, targetX)
                    }
                    true
                }
                else -> false
            }
        }

        bubbleView = frameLayout
        windowManager.addView(frameLayout, params)
    }

    private fun animateSnap(params: WindowManager.LayoutParams, targetX: Int) {
        val startX = params.x
        val animator = ValueAnimator.ofInt(startX, targetX).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                params.x = it.animatedValue as Int
                try {
                    bubbleView?.let { v -> windowManager.updateViewLayout(v, params) }
                } catch (_: Exception) {}
            }
        }
        animator.start()
    }

    private fun toggleSmartMenu(bubbleX: Int, bubbleY: Int) {
        if (isMenuExpanded) {
            closeSmartMenu()
        } else {
            openSmartMenu(bubbleX, bubbleY)
        }
    }

    private fun openSmartMenu(bubbleX: Int, bubbleY: Int) {
        if (smartMenuView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val menuParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = if (bubbleX < screenWidth / 2) bubbleX + dpToPx(64) else bubbleX - dpToPx(240)
            y = bubbleY.coerceIn(dpToPx(40), screenHeight - dpToPx(320))
        }

        val rootCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dpToPx(20).toFloat()
                setColor(Color.parseColor("#1E293B")) // Sleek Dark Slate
                setStroke(dpToPx(1), Color.parseColor("#334155"))
            }
            background = bg
            val p = dpToPx(12)
            setPadding(p, p, p, p)
            elevation = dpToPx(12).toFloat()
        }

        // Header Title
        val header = TextView(context).apply {
            text = "ScreenFlow Studio"
            textSize = 13f
            setTextColor(Color.parseColor("#94A3B8"))
            val padH = dpToPx(8)
            val padV = dpToPx(4)
            setPadding(padH, padV, padH, padV)
        }
        rootCard.addView(header)

        // Action Buttons Row
        val actionsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dpToPx(6), 0, dpToPx(4))
        }

        // 1. Stop Recording Button
        val stopBtn = createActionButton(
            label = "Stop",
            bgColor = Color.parseColor("#EF4444"),
            iconRes = android.R.drawable.ic_media_pause
        ) {
            sendServiceAction(ScreenRecordingService.ACTION_STOP)
            closeSmartMenu()
        }
        actionsRow.addView(stopBtn)

        // 2. Pause / Resume Button
        val pauseBtn = createActionButton(
            label = if (isPaused) "Resume" else "Pause",
            bgColor = if (isPaused) Color.parseColor("#10B981") else Color.parseColor("#F59E0B"),
            iconRes = if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause
        ) {
            if (isPaused) {
                sendServiceAction(ScreenRecordingService.ACTION_RESUME)
            } else {
                sendServiceAction(ScreenRecordingService.ACTION_PAUSE)
            }
            closeSmartMenu()
        }
        actionsRow.addView(pauseBtn)

        // 3. Screenshot Button
        val screenshotBtn = createActionButton(
            label = "Snapshot",
            bgColor = Color.parseColor("#3B82F6"),
            iconRes = android.R.drawable.ic_menu_camera
        ) {
            sendServiceAction(ScreenRecordingService.ACTION_SCREENSHOT)
            closeSmartMenu()
        }
        actionsRow.addView(screenshotBtn)

        // 4. Open App Button
        val appBtn = createActionButton(
            label = "Open App",
            bgColor = Color.parseColor("#8B5CF6"),
            iconRes = android.R.drawable.ic_menu_view
        ) {
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(appIntent)
            closeSmartMenu()
        }
        actionsRow.addView(appBtn)

        rootCard.addView(actionsRow)

        smartMenuView = rootCard
        isMenuExpanded = true
        windowManager.addView(rootCard, menuParams)
    }

    private fun createActionButton(
        label: String,
        bgColor: Int,
        iconRes: Int,
        onClick: () -> Unit
    ): View {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            val p = dpToPx(6)
            setPadding(p, p, p, p)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

        val circle = FrameLayout(context).apply {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(bgColor)
            }
            background = bg
            val s = dpToPx(42)
            layoutParams = LinearLayout.LayoutParams(s, s)
        }

        val img = ImageView(context).apply {
            setImageResource(iconRes)
            setColorFilter(Color.WHITE)
            val p = dpToPx(10)
            setPadding(p, p, p, p)
        }
        circle.addView(img, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val text = TextView(context).apply {
            this.text = label
            textSize = 10f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val pTop = dpToPx(4)
            setPadding(0, pTop, 0, 0)
        }

        container.addView(circle)
        container.addView(text)
        return container
    }

    private fun updateMenuPosition(bubbleX: Int, bubbleY: Int) {
        val menu = smartMenuView ?: return
        val params = menu.layoutParams as? WindowManager.LayoutParams ?: return
        params.x = if (bubbleX < screenWidth / 2) bubbleX + dpToPx(64) else bubbleX - dpToPx(240)
        params.y = bubbleY.coerceIn(dpToPx(40), screenHeight - dpToPx(320))
        try {
            windowManager.updateViewLayout(menu, params)
        } catch (_: Exception) {}
    }

    private fun refreshMenuState() {
        if (isMenuExpanded) {
            val b = bubbleView ?: return
            val params = b.layoutParams as? WindowManager.LayoutParams ?: return
            closeSmartMenu()
            openSmartMenu(params.x, params.y)
        }
    }

    private fun closeSmartMenu() {
        try {
            if (smartMenuView != null) {
                windowManager.removeViewImmediate(smartMenuView)
                smartMenuView = null
            }
        } catch (_: Exception) {}
        isMenuExpanded = false
    }

    private fun sendServiceAction(actionName: String) {
        val intent = Intent(context, ScreenRecordingService::class.java).apply {
            action = actionName
        }
        context.startService(intent)
    }
}
