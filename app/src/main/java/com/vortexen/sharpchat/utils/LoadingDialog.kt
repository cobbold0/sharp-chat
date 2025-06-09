package com.vortexen.sharpchat.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.DialogLoadingBinding
import timber.log.Timber
import androidx.core.graphics.drawable.toDrawable

class LoadingDialog(private val activity: Activity) {
    private val binding: DialogLoadingBinding =
        DialogLoadingBinding.inflate(LayoutInflater.from(activity))

    private fun applyBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.window.decorView.setRenderEffect(
                RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP)
            )
        }
    }

    private fun clearBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.window.decorView.setRenderEffect(null)
        }
    }

    private fun loadGif() {
        Glide.with(activity).asGif().load(R.drawable.light_splash_icon)
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // Don't cache
            .skipMemoryCache(true)  // Skip memory cache
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e(e, "Failed to load GIF")
                    // Try loading again if failed
                    if (dialog.isShowing) {
                        loadGif()
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable,
                    model: Any,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Ensure GIF plays and loops forever
                    resource.setLoopCount(GifDrawable.LOOP_FOREVER)
                    resource.start()
                    return false
                }
            }).into(binding.imageView)
    }

    private val dialog: Dialog = Dialog(activity, R.style.ProgressLoadingDialog).apply {
        setContentView(binding.root)

        window?.apply {
            // For Android 14+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                attributes = attributes?.apply {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                    blurBehindRadius = 50
                }
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }
            // For Android 12-13
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBackgroundBlurRadius(50)
                attributes?.apply {
                    flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                }
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            } else {
                // Fallback
                setBackgroundDrawable(Color.parseColor("#CC000000").toDrawable())
            }

            // Set layout params
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
            )
        }

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        // Clear the blur effect when dialog is dismissed
        setOnDismissListener {
            clearBlurEffect()
        }
    }

    fun show() {
        if (!dialog.isShowing && !activity.isFinishing) {
            try {
                applyBlurEffect()  // Apply blur before showing dialog
                loadGif()  // Load GIF before showing dialog
                dialog.show()
            } catch (e: Exception) {
                clearBlurEffect()  // Clear blur if showing fails
                Timber.e(e, "Error showing progress dialog")
            }
        }
    }

    fun dismiss() {
        if (dialog.isShowing && !activity.isFinishing) {
            try {
                // Clear any GIF resources
                Glide.with(activity).clear(binding.imageView)
                clearBlurEffect()  // Clear blur effect
                dialog.dismiss()
            } catch (e: Exception) {
                Timber.e(e, "Error dismissing progress dialog")
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) show() else dismiss()
    }
}

//class LoadingDialog(private val activity: Activity) {
//    private val binding: DialogLoadingBinding = DialogLoadingBinding.inflate(LayoutInflater.from(activity))
//
//    private fun applyBlurEffect() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            activity.window.decorView.setRenderEffect(
//                RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP)
//            )
//        }
//    }
//
//    private fun clearBlurEffect() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            activity.window.decorView.setRenderEffect(null)
//        }
//    }
//
//
//    private val dialog: Dialog = Dialog(activity, R.style.ProgressLoadingDialog).apply {
//        setContentView(binding.root)
//
//        window?.apply {
//            // Set transparent background
//            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
//
//            // For Android 14+
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                attributes = attributes?.apply {
//                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//                    flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
//                    blurBehindRadius = 50
//                }
//            }
//            // For Android 12-13
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                setBackgroundBlurRadius(50)
//                attributes?.apply {
//                    flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
//                }
//            }
//
//            // Set layout params
//            setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        setCancelable(false)
//        setCanceledOnTouchOutside(false)
//
//        // Clear the blur effect when dialog is dismissed
//        setOnDismissListener {
//            clearBlurEffect()
//        }
//    }
//
//    private fun show() {
//        if (!dialog.isShowing && !activity.isFinishing) {
//            try {
//                applyBlurEffect()  // Apply blur before showing dialog
//                dialog.show()
//            } catch (e: Exception) {
//                clearBlurEffect()  // Clear blur if showing fails
//                Timber.e(e, "Error showing progress dialog")
//            }
//        }
//    }
//
//    private fun dismiss() {
//        if (dialog.isShowing && !activity.isFinishing) {
//            try {
//                // Clear any GIF resources
//                Glide.with(activity).clear(binding.imageView)
//                clearBlurEffect()  // Clear blur effect
//                dialog.dismiss()
//            } catch (e: Exception) {
//                Timber.e(e, "Error dismissing progress dialog")
//            }
//        }
//    }
//
//    fun setLoading(isLoading: Boolean) {
//        if (isLoading) show() else dismiss()
//    }
//}