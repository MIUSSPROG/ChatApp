package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var auth: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth.currentUser
        auth?.let {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()
        binding!!.apply {
            editNumber.requestFocus()
            continueButton.setOnClickListener {
                val intent = Intent(this@MainActivity, OTPActivity::class.java)
                intent.putExtra("phoneNumber", editNumber.text.toString())
                startActivity(intent)
            }
        }
    }
}