package com.aetheria.bigtype.ui

import android.view.MotionEvent
import kotlin.math.abs

class GestureHandler(
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit,
    private val onSwipeUp: () -> Unit,
    private val onTwoFingerSwipeLeft: () -> Unit,
    private val onTwoFingerSwipeRight: () -> Unit,
    private val onZPattern: () -> Unit
) {
    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var pointerCount = 0
    
    private val swipeThreshold = 100f
    private val swipeVelocityThreshold = 100f
    
    private var zPatternPoints = mutableListOf<Pair<Float, Float>>()
    private var lastZTime = 0L
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        pointerCount = event.pointerCount
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                startTime = System.currentTimeMillis()
                zPatternPoints.clear()
                zPatternPoints.add(Pair(event.x, event.y))
            }
            MotionEvent.ACTION_MOVE -> {
                zPatternPoints.add(Pair(event.x, event.y))
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val duration = System.currentTimeMillis() - startTime
                
                if (checkZPattern()) {
                    onZPattern()
                    return true
                }
                
                val deltaX = endX - startX
                val deltaY = endY - startY
                
                if (pointerCount >= 2) {
                    if (abs(deltaX) > swipeThreshold) {
                        if (deltaX < 0) {
                            onTwoFingerSwipeLeft()
                        } else {
                            onTwoFingerSwipeRight()
                        }
                        return true
                    }
                }
                
                if (abs(deltaX) > swipeThreshold && abs(deltaX) > abs(deltaY)) {
                    if (deltaX < 0) {
                        onSwipeLeft()
                    } else {
                        onSwipeRight()
                    }
                    return true
                }
                
                if (abs(deltaY) > swipeThreshold && deltaY < 0) {
                    onSwipeUp()
                    return true
                }
            }
        }
        return false
    }
    
    private fun checkZPattern(): Boolean {
        if (zPatternPoints.size < 10) return false
        
        val now = System.currentTimeMillis()
        if (now - lastZTime > 2000) {
            zPatternPoints.clear()
            return false
        }
        
        var leftCount = 0
        var rightCount = 0
        var wentRightFirst = false
        var wentLeft = false
        
        for (i in 1 until zPatternPoints.size) {
            val prev = zPatternPoints[i - 1]
            val curr = zPatternPoints[i]
            val dx = curr.first - prev.first
            
            if (dx > 20) {
                rightCount++
                if (!wentLeft) wentRightFirst = true
            } else if (dx < -20) {
                leftCount++
                wentLeft = true
            }
        }
        
        lastZTime = now
        return wentRightFirst && wentLeft && leftCount >= 2 && rightCount >= 2
    }
}