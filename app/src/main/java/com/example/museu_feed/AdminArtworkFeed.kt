package com.example.museu_feed

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminArtworkFeed : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var artworkFeedRecyclerView: RecyclerView
    private lateinit var artworkFeedAdapter: ArtworkFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_artwork_feed)

        firestore = Firebase.firestore

        artworkFeedRecyclerView = findViewById(R.id.artworkFeedRecyclerView)
        artworkFeedRecyclerView.layoutManager = LinearLayoutManager(this)

        val backButton = findViewById<ImageView>(R.id.backIcon)

        backButton.setOnClickListener {
            finish()
        }

        val firestoreId = intent.getStringExtra("FIRESTORE_ID")
        if (firestoreId != null) {
            fetchArtworkFeed(firestoreId)
        }
    }

    private fun fetchArtworkFeed(firestoreId: String) {
        firestore.collection("obras").document(firestoreId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val comments = document.get("comments") as? List<Map<String, Any>>
                    if (comments != null) {
                        val commentList = comments.map {
                            val username = it["username"] as? String ?: "Unknown"
                            val text = it["text"] as? String ?: "No text"
                            val timestamp = it["timestamp"] as? String ?: "Unknown date"
                            val likes = when (val likesValue = it["likes"]) {
                                is Long -> likesValue.toInt()
                                is String -> likesValue.toIntOrNull() ?: 0
                                else -> 0
                            }
                            Comment(username, text, timestamp, likes)
                        }
                        artworkFeedAdapter = ArtworkFeedAdapter(commentList, firestore, firestoreId)
                        artworkFeedRecyclerView.adapter = artworkFeedAdapter
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}
