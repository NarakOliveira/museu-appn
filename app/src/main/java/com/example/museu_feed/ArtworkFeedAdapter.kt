package com.example.museu_feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ArtworkFeedAdapter(
    private val comments: List<Comment>,
    private val firestore: FirebaseFirestore,
    private val firestoreId: String
) : RecyclerView.Adapter<ArtworkFeedAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount() = comments.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.commentUsername)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentText)
        private val timestampTextView: TextView = itemView.findViewById(R.id.commentTimestamp)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteCommentIcon)

        fun bind(comment: Comment) {
            usernameTextView.text = comment.username
            commentTextView.text = comment.text
            timestampTextView.text = comment.timestamp

            deleteIcon.setOnClickListener {
                firestore.collection("obras").document(firestoreId).get()
                    .addOnSuccessListener { document ->
                        val comments = document.get("comments") as? List<Map<String, Any>>
                        if (comments != null) {
                            val updatedComments = comments.filterNot {
                                it["username"] == comment.username && it["text"] == comment.text
                            }
                            firestore.collection("obras").document(firestoreId)
                                .update("comments", updatedComments)
                                .addOnSuccessListener {
                                    notifyItemRemoved(adapterPosition)
                                }
                                .addOnFailureListener {
                                    // Handle the error
                                }
                        }
                    }
            }
        }
    }
}
