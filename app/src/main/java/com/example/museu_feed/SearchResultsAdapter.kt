package com.example.museu_feed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SearchResultsAdapter(
    private val artworks: List<ArtworkEntry>,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<SearchResultsAdapter.ArtworkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artwork, parent, false)
        return ArtworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        val artwork = artworks[position]
        holder.bind(artwork)
    }

    override fun getItemCount() = artworks.size

    inner class ArtworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.artworkTitle)
        private val authorTextView: TextView = itemView.findViewById(R.id.artworkAuthor)
        private val commentsTextView: TextView = itemView.findViewById(R.id.artworkComments)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(artwork: ArtworkEntry) {
            titleTextView.text = artwork.title
            authorTextView.text = artwork.author
            commentsTextView.text = "Comments: ${artwork.comments.size}"

            deleteIcon.setOnClickListener {
                firestore.collection("obras").document(artwork.id).delete()
                    .addOnSuccessListener {
                        notifyItemRemoved(adapterPosition)
                    }
                    .addOnFailureListener {
                        // Handle the error
                    }
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AdminArtworkFeed::class.java).apply {
                    putExtra("FIRESTORE_ID", artwork.id)
                }
                context.startActivity(intent)
            }
        }
    }
}
