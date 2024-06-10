package com.example.museu_feed

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchResult : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchQueryTextView: TextView
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result)

        firestore = Firebase.firestore

        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        searchQueryTextView = findViewById(R.id.searchQueryTextView)
        val backButton = findViewById<ImageView>(R.id.backIcon)

        backButton.setOnClickListener {
            finish()
        }

        val searchQuery = intent.getStringExtra("search") ?: ""
        searchQueryTextView.text = "Search results for: \"$searchQuery\""

        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)

        searchArtworks(searchQuery)
    }

    private fun searchArtworks(query: String) {
        firestore.collection("obras")
            .whereGreaterThanOrEqualTo("title", query)
            .get()
            .addOnSuccessListener { documents ->
                displaySearchResults(documents)
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun displaySearchResults(documents: QuerySnapshot) {
        val artworks = documents.map { document ->
            ArtworkEntry(
                id = document.id,
                title = document.getString("title") ?: "",
                author = document.getString("author") ?: "",
                comments = document.get("comments") as? List<Map<String, Any>> ?: emptyList()
            )
        }
        searchResultsAdapter = SearchResultsAdapter(artworks, firestore)
        searchResultsRecyclerView.adapter = searchResultsAdapter
    }

}
