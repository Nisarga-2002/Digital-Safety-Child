package com.example.digitalsafety_child.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.constants.Constants
import com.example.digitalsafety_child.databinding.ActivityLoginBinding
import com.example.digitalsafety_child.utils.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import java.util.Objects

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mAuth = FirebaseAuth.getInstance()
        binding.buttonLogin.setOnClickListener {
            val email =
                Objects.requireNonNull(binding.etEmail.text).toString()
            val password =
                Objects.requireNonNull(binding.etPassword.text).toString()
            if (email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex()) && password.length > 8) {
                binding.txtErrorEmail.visibility = View.GONE
                binding.txtErrorPassword.visibility = View.GONE
                showProgressingView()

                mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        SharedPreferences.putString(Constants.SharedPreferences.emailId, value = email)
                        startActivity(Intent(this@LoginActivity, PermissionOverviewActivity::class.java))
                        binding.etEmail.text!!.clear()
                        binding.etPassword.text!!.clear()
                        SharedPreferences.putBoolean(Constants.SharedPreferences.IsAuthorized,value = true)
                        hideProgressingView()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Login failed!!" + " Please try again later", Toast.LENGTH_LONG).show()
                        hideProgressingView()
                    }
            } else {
                if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex())) {
                    binding.txtErrorEmail.visibility = View.VISIBLE
                    binding.txtErrorEmail.text = "Enter Valid email"
                }
                if (password.length < 8) {
                    binding.txtErrorPassword.visibility = View.VISIBLE
                    binding.txtErrorPassword.text = "Password should be greater than 8 digits"
                }
            }
        }

    }
}