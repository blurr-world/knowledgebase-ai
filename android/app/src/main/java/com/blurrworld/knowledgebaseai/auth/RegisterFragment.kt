package com.blurrworld.knowledgebaseai.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blurrworld.knowledgebaseai.R
import com.blurrworld.knowledgebaseai.databinding.FragmentRegisterBinding
import com.blurrworld.knowledgebaseai.models.RegisterRequest
import com.blurrworld.knowledgebaseai.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performRegister(email, password)
        }

        binding.btnSwitchToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commit()
        }
    }

    private fun performRegister(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.register(RegisterRequest(email, password))
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()!!
                        if (registerResponse.success) {
                            Toast.makeText(requireContext(), "Account created login now", Toast.LENGTH_LONG).show()
                            // Switch back to login screen
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.auth_fragment_container, LoginFragment())
                                .commit()
                        } else {
                            Toast.makeText(requireContext(), registerResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
