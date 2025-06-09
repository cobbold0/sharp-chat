package com.vortexen.sharpchat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vortexen.sharpchat.R
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class DeepLinkHandlerActivity : AppCompatActivity() {
    @Inject
    lateinit var supabaseClient: SupabaseClient
    private lateinit var callback: (String, String) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deep_link_handler)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supabaseClient.handleDeeplinks(intent = intent, onSessionSuccess = { userSession ->
            Log.d("LOGIN", "Log in successfully with user info: ${userSession.user}")
            userSession.user?.apply {
                callback(email ?: "", "")
            }
        })

        callback = { email, _ ->
            if (email.isNotEmpty()) {
                navigateToOnBoarding()
            }
        }
    }


    private fun navigateToOnBoarding() {
        val intent = Intent(this, OnBoardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        if (!isFinishing) {
            finish()
        }
    }
}