package com.blurrworld.knowledgebaseai.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blurrworld.knowledgebaseai.MainActivity
import com.blurrworld.knowledgebaseai.R
import com.blurrworld.knowledgebaseai.databinding.ActivityAuthBinding
import com.blurrworld.knowledgebaseai.network.ApiClient
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        val token = tokenManager.getToken()
        if (token != null) {
            ApiClient.tokenProvider = { tokenManager.getToken() }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commit()
        }
    }
}
