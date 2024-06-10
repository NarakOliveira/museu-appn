package com.example.museu_feed

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArtFeed : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var interpretationAdapter: InterpretationAdapter
    private lateinit var backButton: ImageView
    private lateinit var addCommentButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.art_feed)

        firestore = Firebase.firestore
        sharedPreferences = getSharedPreferences("ART_FEED_PREFS", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.rv_feed_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton = findViewById(R.id.backIcon)
        backButton.setOnClickListener {
            finish()
        }

        addCommentButton = findViewById(R.id.addCommentButton)
        addCommentButton.setOnClickListener {
            val intent = Intent(this, CommentCreator::class.java).apply {
                putExtra("FIRESTORE_ID", intent.getStringExtra("FIRESTORE_ID"))
            }
            startActivity(intent)
        }

        val firestoreId = intent.getStringExtra("FIRESTORE_ID")
        if (firestoreId != null) {
            fetchArtworkData(firestoreId)
        }
    }

    private fun fetchArtworkData(firestoreId: String) {
        firestore.collection("obras").document(firestoreId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val comments = document.get("comments") as? List<Map<String, Any>>
                    val userCommentKey = "user_comment_$firestoreId"
                    val userComment = sharedPreferences.getString(userCommentKey, null)

                    val userInterpretation = userComment?.let {
                        println(it)
                        val parts = it.split(":")

                        if (parts.size == 4) {
                            Interpretation("${parts[0]} (Você)", parts[1], parts[2] + ":" + parts[3], 0, true)
                        } else {
                            null
                        }
                    }

                    val filteredComments = comments?.filterNot {
                        it["username"] == userInterpretation?.username?.removeSuffix(" (Você)") &&
                                it["text"] == userInterpretation?.text
                    }

                    if (filteredComments != null) {
                        val interpretationList = filteredComments.map {
                            val username = it["username"] as? String ?: "Unknown"
                            val text = it["text"] as? String ?: "No text"
                            val timestamp = it["timestamp"] as? String ?: "Unknown date"
                            val likes = when (val likesValue = it["likes"]) {
                                is Long -> likesValue.toInt()
                                is String -> likesValue.toIntOrNull() ?: 0
                                else -> 0
                            }
                            val isFlagged = isCommentFlagged(username, text)
                            Interpretation(username, text, timestamp, likes, isFlagged)
                        }
                        val finalList = userInterpretation?.let { listOf(it) + interpretationList } ?: interpretationList
                        interpretationAdapter = InterpretationAdapter(finalList, this::toggleFlag, firestoreId, firestore)
                        recyclerView.adapter = interpretationAdapter
                    } else {
                        // Handle the case where comments are null or not present
                        val finalList = userInterpretation?.let { listOf(it) } ?: emptyList()
                        interpretationAdapter = InterpretationAdapter(finalList, this::toggleFlag, firestoreId, firestore)
                        recyclerView.adapter = interpretationAdapter
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun isCommentFlagged(username: String, text: String): Boolean {
        val key = "$username-$text"
        return sharedPreferences.getBoolean(key, false)
    }

    private fun toggleFlag(username: String, text: String, isFlagged: Boolean) {
        val key = "$username-$text"
        sharedPreferences.edit().putBoolean(key, isFlagged).apply()
    }
}
