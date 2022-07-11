package com.example.dartapp.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.core.animation.doOnEnd
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

private const val CHIP_ICON_SIZE_PROPERTY_NAME = "chipIconSize"

// A value of '0f' would be interpreted as 'use the default size' by the ChipDrawable, so use a slightly larger value.
private const val INVISIBLE_CHIP_ICON_SIZE = 0.00001f

/**
 * Custom Chip class which will animate transition between the [isChecked] states.
 */
class CheckAnimationChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.chipStyle
) : Chip(context, attrs, defStyleAttr) {

    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var _chipDrawable: ChipDrawable
    private var defaultCheckedIconSize: Float
    private var currentlyScalingDown = false

    var animationDuration = 200L

    init {
        // Set default values for this category of chip.
        isCheckable = true
        isCheckedIconVisible = true

        _chipDrawable = chipDrawable as ChipDrawable
        defaultCheckedIconSize = _chipDrawable.chipIconSize

        super.setOnCheckedChangeListener { buttonView, isChecked ->
            if (currentlyScalingDown) {
                // Block the changes caused by the scaling-down animation.
                return@setOnCheckedChangeListener
            }
            onCheckedChangeListener?.onCheckedChanged(buttonView, isChecked)

            if (isChecked) {
                scaleCheckedIconUp()
            } else if (!isChecked) {
                scaleCheckedIconDown()
            }
        }
    }

    /**
     * Scale the size of the Checked-Icon from invisible to its default size.
     */
    private fun scaleCheckedIconUp() {
        ObjectAnimator.ofFloat(_chipDrawable, CHIP_ICON_SIZE_PROPERTY_NAME,
            INVISIBLE_CHIP_ICON_SIZE, defaultCheckedIconSize)
            .apply {
                duration =  animationDuration
                start()
                doOnEnd {
                    _chipDrawable.chipIconSize = defaultCheckedIconSize
                }
            }
    }

    /**
     * Scale the size of the Checked-Icon from its default size down to invisible. To achieve this, the
     * [isChecked] property needs to be manipulated. It is set to be true till the animation has ended.
     */
    private fun scaleCheckedIconDown() {
        currentlyScalingDown = true
        isChecked = true
        ObjectAnimator.ofFloat(_chipDrawable, CHIP_ICON_SIZE_PROPERTY_NAME,
            defaultCheckedIconSize, INVISIBLE_CHIP_ICON_SIZE)
            .apply {
                duration =  animationDuration
                start()
                doOnEnd {
                    isChecked = false
                    currentlyScalingDown = false
                    _chipDrawable.chipIconSize = defaultCheckedIconSize
                }
            }
    }

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }
}