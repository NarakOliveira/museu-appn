package com.example.museu_feed

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class FeedAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed_admin)

        val backButton = findViewById<ImageView>(R.id.backIcon)
        val searchField = findViewById<EditText>(R.id.searchWork)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val addButton = findViewById<Button>(R.id.addWork)

        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            val search = searchField.text.toString()
            if (search.isEmpty()) {
                Toast.makeText(this, "Campo de busca vazio", Toast.LENGTH_SHORT).show()
            } else {
                val intent = android.content.Intent(this, SearchResult::class.java)
                intent.putExtra("search", search)
                startActivity(intent)
            }
        }

        addButton.setOnClickListener {
            val intent = android.content.Intent(this, ArtCreator::class.java)
            startActivity(intent)
        }
    }
}

