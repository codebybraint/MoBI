package com.atiga.cakeorder.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.MainActivity
import com.atiga.cakeorder.core.BuildConfig
import com.atiga.cakeorder.databinding.ActivityLoginBinding
import com.atiga.cakeorder.util.SessionManager
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        sessionManager = SessionManager(this)

        if(BuildConfig.DEBUG) {
            Log.d(
                "login",
                "current user: ${sessionManager.getFromPreference(SessionManager.KEY_USERNAME)}"
            )
            Log.d("login", "token: ${sessionManager.getFromPreference(SessionManager.TOKEN)}")
            Log.d("userId", "userId: ${sessionManager.getFromPreference(SessionManager.USER_ID)}")
            Log.d("roleId", "roleID: ${sessionManager.getFromPreference(SessionManager.ROLE_ID)}")
        }
        // check is login
        if(sessionManager.isLogin){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        loginViewModel.login.observe(this, {
            it?.let {
                sessionManager.createLoginSession(it.username, it.token, it.userId, it.roleId.toString())
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        loginViewModel.loginFailed.observe(this, {
            it?.let {
                isLoading(false)
                showMessage("Username/password salah")
            }
        })

    }

    private fun login() {
        if (binding.etUsername.text.toString().isNotBlank() && binding.etUsername.text.toString()
                .isNotBlank()
        ) {
            isLoading(true)
            lifecycleScope.launch {
                loginViewModel.login(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        } else {
            showMessage("Username/password tidak boleh kosong")
        }
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.etUsername.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.btnLogin.isEnabled = false
            binding.progressBarUpdate.visibility = View.VISIBLE
        } else {
            binding.etUsername.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.btnLogin.isEnabled = true
            binding.progressBarUpdate.visibility = View.GONE
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}