package com.vortexen.sharpchat.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.LayoutSharpChartActionBarBinding

/**
 * SharpChatActionBar is a custom ActionBar for the SharpChat app.
 * It supports dynamic title, back button, search, call, and menu icons.
 */
class SharpChatActionBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutSharpChartActionBarBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var title: String = ""
        set(value) {
            field = value
            binding.tvTitle.text = value
        }

    var showBackButton: Boolean = false
        set(value) {
            field = value
            binding.ivBack.visibility = if (value) VISIBLE else GONE
        }

    var showSearch: Boolean = false
        set(value) {
            field = value
            binding.ivSearch.visibility = if (value) VISIBLE else GONE
        }

    var showCallButton: Boolean = false
        set(value) {
            field = value
            binding.ivCall.visibility = if (value) VISIBLE else GONE
        }

    var showSettings: Boolean = false
        set(value) {
            field = value
            binding.ivSettings.visibility = if (value) VISIBLE else GONE
        }

    var showMenu: Boolean = false
        set(value) {
            field = value
            binding.ivMoreOptions.visibility = if (value) VISIBLE else GONE
        }


    fun setOnBackClickListener(listener: () -> Unit) {
        binding.ivBack.setOnClickListener { listener.invoke() }
    }

    fun setOnSearchClickListener(listener: () -> Unit) {
        binding.ivSearch.setOnClickListener { listener.invoke() }
    }

    fun setOnCallClickListener(listener: () -> Unit) {
        binding.ivCall.setOnClickListener { listener.invoke() }
    }

    fun setOnSettingsClickListener(listener: () -> Unit) {
        binding.ivSettings.setOnClickListener { listener.invoke() }
    }

    fun setOnMenuClickListener(listener: () -> Unit) {
        binding.ivMoreOptions.setOnClickListener { listener.invoke() }
    }

    init {
        context.withStyledAttributes(
            attrs, R.styleable.SharpChatActionBar, defStyleAttr, 0
        ) {
            title = getString(R.styleable.SharpChatActionBar_title) ?: ""
            showBackButton = getBoolean(R.styleable.SharpChatActionBar_showBackButton, false)
            showSearch = getBoolean(R.styleable.SharpChatActionBar_showSearch, false)
            showCallButton = getBoolean(R.styleable.SharpChatActionBar_showCallButton, false)
            showSettings = getBoolean(R.styleable.SharpChatActionBar_showSettings, false)
            showMenu = getBoolean(R.styleable.SharpChatActionBar_showMenu, false)
        }
    }
}
