package com.example.museu_feed

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class ArtCreator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.art_creator)

        val backButton = findViewById<ImageView>(R.id.returnButton)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        backButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            val alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("Deseja adicionar a obra?")
            alertDialog.setMessage("Deseja mesmo adicionar esta obra?")
            alertDialog.setPositiveButton("Confirmar") { dialog, which ->
                // Add the art to the database
                createArt()
            }
            alertDialog.setNegativeButton("Voltar") { dialog, which ->
                // Do nothing
            }

            alertDialog.show()
        }
    }

    private fun createArt() {
        val title = findViewById<EditText>(R.id.titleEditText).text.toString()
        val author = findViewById<EditText>(R.id.authorEditText).text.toString()
        val artUrl = findViewById<EditText>(R.id.artUrl).text.toString()
        val emptyJson = hashMapOf<String, String>()

        val work = hashMapOf(
            "title" to title,
            "author" to author,
            "artUrl" to artUrl,
            "comments" to emptyJson
        )

        val database = Firebase.firestore

        database.collection("obras")
            .add(work)
            .addOnSuccessListener { documentReference ->
                Log.d("ArtCreator", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Obra adicionada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("ArtCreator", "Error adding document", e)
                Toast.makeText(this, "Erro ao adicionar obra", Toast.LENGTH_SHORT).show()
            }
    }
}

