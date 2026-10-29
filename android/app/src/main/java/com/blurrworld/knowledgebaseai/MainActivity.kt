package com.blurrworld.knowledgebaseai

import androidx.core.graphics.Insets;
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.blurrworld.knowledgebaseai.auth.TokenManager
import com.blurrworld.knowledgebaseai.databinding.ActivityMainBinding
import com.blurrworld.knowledgebaseai.network.ApiClient


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        ApiClient.tokenProvider = { tokenManager.getToken() }

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { view: View?, windowInsets: WindowInsetsCompat? ->
            val imeInsets: Insets = windowInsets!!.getInsets(
                WindowInsetsCompat.Type.ime()
            )
            val systemBars: Insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view!!.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom.coerceAtLeast(imeInsets.bottom)
            )
            windowInsets
        }

        if (savedInstanceState == null) {
           changeFragment(ChatFragment())
        }
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}