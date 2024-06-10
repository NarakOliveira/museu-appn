package com.example.museu_feed

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginAdm : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_adm)

        val loginText = findViewById<TextView>(R.id.loginText)
        val passwdText = findViewById<TextView>(R.id.passwdText)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val returnBtn = findViewById<ImageView>(R.id.returnButton)

        mAuth = FirebaseAuth.getInstance()

        returnBtn.setOnClickListener {
            finish()
        }

        loginBtn.setOnClickListener {
            val email = loginText.text.toString()
            val password = passwdText.text.toString()

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        val intent = android.content.Intent(this, FeedAdmin::class.java)
                        startActivity(intent)
                    } else {
                        Log.w("failed", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@LoginAdm, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
