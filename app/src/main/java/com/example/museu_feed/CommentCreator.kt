package com.example.museu_feed

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentCreator : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_creator)

        firestore = Firebase.firestore

        val backButton = findViewById<ImageView>(R.id.returnButton)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val commentEditText = findViewById<EditText>(R.id.commentEditText)
        val charCountTextView = findViewById<TextView>(R.id.charCountTextView)
        val submitCommentButton = findViewById<Button>(R.id.submitCommentButton)

        val firestoreId = intent.getStringExtra("FIRESTORE_ID")

        backButton.setOnClickListener {
            finish()
        }

        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                charCountTextView.text = "${s?.length}/100"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        submitCommentButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val commentText = commentEditText.text.toString()
            val currentTime = System.currentTimeMillis()
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = formatter.format(Date(currentTime))

            if (username.isNotEmpty() && commentText.isNotEmpty() && firestoreId != null) {
                val comment = hashMapOf(
                    "username" to username,
                    "text" to commentText,
                    "timestamp" to formattedDate,
                    "likes" to 0
                )

                firestore.collection("obras").document(firestoreId).update("comments", FieldValue.arrayUnion(comment))
                    .addOnSuccessListener {
                        // Save the comment locally
                        val sharedPreferences = getSharedPreferences("ART_FEED_PREFS", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("user_comment_$firestoreId", "$username:$commentText:$formattedDate")
                        editor.apply()
                        finish()
                    }
                    .addOnFailureListener {
                        // Handle the error
                    }
            }
        }
    }
}
