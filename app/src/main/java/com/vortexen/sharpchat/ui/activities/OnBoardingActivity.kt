package com.vortexen.sharpchat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.ActivityOnboardingBinding
import com.vortexen.sharpchat.utils.extensions.observeEventLiveData
import com.vortexen.sharpchat.ui.viewModels.OnBoardingActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    companion object {
        private const val MIN_SPLASH_DURATION = 1000L
    }

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnBoardingActivityViewModel by viewModels()
    private lateinit var navController: NavController

    private var isSessionLoaded = false
    private var isMinDurationPassed = false
    private var sessionResult: SessionResult? = null
    private var hasNavigated = false

    private sealed class SessionResult {
        data object Success : SessionResult()
        data class Error(val exception: Throwable) : SessionResult()
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !shouldDismissSplash() }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupUI()
        setupNavigation()
        startSessionLoading()
        startMinDurationTimer()
    }

    private fun setupUI() {
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.visibility = View.INVISIBLE

        ViewCompat.setOnApplyWindowInsetsListener(binding.onboarding) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.onboarding_nav_host_fragment) as? NavHostFragment
                ?: run {
                    Timber.e("NavHostFragment not found")
                    finish()
                    return
                }

        navController = navHostFragment.navController
    }

    private fun startSessionLoading() {
        viewModel.loadSession()

        observeEventLiveData(liveData = viewModel.currentSession, onFailure = { message ->
            Timber.e("Session loading failed with message: $message")
            sessionResult = SessionResult.Error(Exception(message))
            isSessionLoaded = true
            checkAndProceed()
        }, onSuccess = {
            Timber.d("Session loaded successfully")
            sessionResult = SessionResult.Success
            isSessionLoaded = true
            checkAndProceed()
        })
    }

    private fun startMinDurationTimer() {
        lifecycleScope.launch {
            delay(MIN_SPLASH_DURATION)
            isMinDurationPassed = true
            checkAndProceed()
        }
    }

    private fun shouldDismissSplash(): Boolean {
        return (isSessionLoaded && isMinDurationPassed && sessionResult is SessionResult.Error) || hasNavigated
    }

    private fun checkAndProceed() {
        if (!isSessionLoaded || !isMinDurationPassed || hasNavigated) return

        when (val result = sessionResult) {
            is SessionResult.Success -> navigateToMainActivity()
            is SessionResult.Error -> showOnboardingContent()
            null -> {
                Timber.e("Session result is null when proceeding")
                showOnboardingContent()
            }
        }
    }

    private fun navigateToMainActivity() {
        if (hasNavigated) return

        hasNavigated = true

        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Timber.e(e, "Failed to navigate to MainActivity")
            hasNavigated = false
            showOnboardingContent()
        }
    }

    private fun showOnboardingContent() {
        binding.root.visibility = View.VISIBLE
        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(300).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::binding.isInitialized) {
            binding.root.clearAnimation()
        }
    }
}