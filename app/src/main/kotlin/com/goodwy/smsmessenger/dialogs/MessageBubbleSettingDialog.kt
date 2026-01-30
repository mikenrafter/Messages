package com.goodwy.smsmessenger.dialogs

import android.content.res.ColorStateList
import androidx.appcompat.app.AlertDialog
import com.goodwy.commons.extensions.*
import com.goodwy.smsmessenger.R
import com.goodwy.smsmessenger.activities.SimpleActivity
import com.goodwy.smsmessenger.databinding.DialogMessageBubbleSettingBinding
import com.goodwy.smsmessenger.extensions.config
import com.goodwy.smsmessenger.helpers.BUBBLE_STYLE_IOS
import com.goodwy.smsmessenger.helpers.BUBBLE_STYLE_IOS_NEW
import com.goodwy.smsmessenger.helpers.BUBBLE_STYLE_ORIGINAL
import com.goodwy.smsmessenger.helpers.BUBBLE_STYLE_ROUNDED
import kotlin.math.abs

class MessageBubbleSettingDialog(
    private val activity: SimpleActivity,
    private val isPro: Boolean,
    private val callback: (style: Int) -> Unit,
) {
    private val binding = DialogMessageBubbleSettingBinding.inflate(activity.layoutInflater)
    private val config = activity.config
    private var currentBubbleStyle = config.bubbleStyle
    private var dialog: AlertDialog? = null

    init {
        setupBubbleInvertColor()
        setupBubbleUseContactColor()
        setupColors()
        setupToggleBubbleStyle()

        val backgroundColor =
            if (activity.isDynamicTheme() && !activity.isSystemInDarkMode()) activity.getSurfaceColor()
            else activity.getProperBackgroundColor()
        if ((activity.isDynamicTheme()) || activity.isBlackTheme()) {
            val drawable = binding.root.resources.getColoredDrawableWithColor(
                R.drawable.rounded_rectangle_color,
                backgroundColor
            )

            binding.styleOriginal.background = drawable
            binding.styleRounded.background = drawable
            binding.styleIosNew.background = drawable
            binding.styleIos.background = drawable
        }
        binding.styleOriginal.setOnClickListener {
            currentBubbleStyle = BUBBLE_STYLE_ORIGINAL
            setupToggleBubbleStyle(BUBBLE_STYLE_ORIGINAL)
        }
        binding.styleRounded.setOnClickListener {
            currentBubbleStyle = BUBBLE_STYLE_ROUNDED
            setupToggleBubbleStyle(BUBBLE_STYLE_ROUNDED)
        }
        binding.styleIosNew.setOnClickListener {
            currentBubbleStyle = BUBBLE_STYLE_IOS_NEW
            setupToggleBubbleStyle(BUBBLE_STYLE_IOS_NEW)
        }
        binding.styleIos.setOnClickListener {
            currentBubbleStyle = BUBBLE_STYLE_IOS
            setupToggleBubbleStyle(BUBBLE_STYLE_IOS)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.goodwy.commons.R.string.ok) { _, _ ->
                config.bubbleStyle = currentBubbleStyle
                callback(currentBubbleStyle)
            }
            .apply {
                activity.setupDialogStuff(binding.root, this, com.goodwy.strings.R.string.speech_bubble) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun setupToggleBubbleStyle(style: Int = currentBubbleStyle) {
        binding.styleOriginalCheck.isActivated = style == BUBBLE_STYLE_ORIGINAL
        binding.styleRoundedCheck.isActivated = style == BUBBLE_STYLE_ROUNDED
        binding.styleIosNewCheck.isActivated = style == BUBBLE_STYLE_IOS_NEW
        binding.styleIosCheck.isActivated = style == BUBBLE_STYLE_IOS
        val states = arrayOf(
            intArrayOf(android.R.attr.state_activated),
            intArrayOf(-android.R.attr.state_activated))
        arrayOf(binding.styleOriginalCheck, binding.styleRoundedCheck, binding.styleIosNewCheck, binding.styleIosCheck).forEach {
            it.imageTintList = ColorStateList(states, intArrayOf(binding.root.context.getProperPrimaryColor(), binding.root.context.getProperTextColor()))
        }
    }

    private fun setupBubbleInvertColor() {
        binding.bubbleInvertColor.isChecked = config.bubbleInvertColor
        binding.bubbleInvertColorHolder.setOnClickListener {
            binding.bubbleInvertColor.toggle()
            config.bubbleInvertColor = binding.bubbleInvertColor.isChecked
            setupColors()
        }
    }

    private fun setupBubbleUseContactColor() {
        binding.bubbleUseContactColor.isChecked = config.bubbleInContactColor
        binding.bubbleUseContactColorHolder.setOnClickListener {
            binding.bubbleUseContactColor.toggle()
            config.bubbleInContactColor = binding.bubbleUseContactColor.isChecked
            setupColors()
        }
    }

    private fun setupColors() {
        binding.apply {
            val random = (0..10).random().toString()
            val letterBackgroundColors = root.context.getLetterBackgroundColors()
            val primaryColor =
                if (config.bubbleInContactColor) letterBackgroundColors[abs(random.hashCode()) % letterBackgroundColors.size].toInt()
                else root.context.getProperPrimaryColor()

            val useSurfaceColor = root.context.isDynamicTheme() && !root.context.isSystemInDarkMode()
            val surfaceColor = if (useSurfaceColor) root.context.getProperBackgroundColor() else root.context.getSurfaceColor()

            val backgroundReceived = if (config.bubbleInvertColor) primaryColor else surfaceColor
            val contrastColorReceived = backgroundReceived.getContrastColor()
            arrayOf(
                styleOriginalBubbleOne,
                styleOriginalBubbleTwo,
                styleRoundedBubbleOne,
                styleRoundedBubbleTwo,
                styleIosNewBubbleOne,
                styleIosNewBubbleTwo,
                styleIosBubbleOne,
                styleIosBubbleTwo,
            ).forEach {
                it.background.applyColorFilter(backgroundReceived)
                it.setTextColor(contrastColorReceived)
            }

            val backgroundSender = if (config.bubbleInvertColor) surfaceColor else primaryColor
            val contrastColorSender = backgroundSender.getContrastColor()
            arrayOf(
                styleOriginalBubbleThree,
                styleRoundedBubbleThree,
                styleIosNewBubbleThree,
                styleIosBubbleThree,
            ).forEach {
                it.background.applyColorFilter(backgroundSender)
                it.setTextColor(contrastColorSender)
            }
        }
    }
}
