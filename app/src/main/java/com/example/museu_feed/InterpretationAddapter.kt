package com.example.museu_feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.museu_feed.R

class InterpretationAdapter(
    private val interpretations: List<Interpretation>,
    private val toggleFlagCallback: (String, String, Boolean) -> Unit,
    private val firestoreId: String,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<InterpretationAdapter.InterpretationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterpretationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.art_feed_item, parent, false)
        return InterpretationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterpretationViewHolder, position: Int) {
        val interpretation = interpretations[position]
        holder.bind(interpretation)
    }

    override fun getItemCount() = interpretations.size

    inner class InterpretationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.username)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentText)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestamp)
        private val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        private val likesCountTextView: TextView = itemView.findViewById(R.id.likes)

        fun bind(interpretation: Interpretation) {
            usernameTextView.text = interpretation.username
            commentTextView.text = interpretation.text
            timestampTextView.text = interpretation.timestamp
            likesCountTextView.text = interpretation.likes.toString()

            updateLikeIcon(interpretation.isFlagged)

            likeIcon.setOnClickListener {
                val newFlaggedStatus = !interpretation.isFlagged
                toggleFlagCallback(interpretation.username, interpretation.text, newFlaggedStatus)
                updateLikeIcon(newFlaggedStatus)

                if (newFlaggedStatus) {
                    incrementLikes(interpretation)
                } else {
                    decrementLikes(interpretation)
                }
            }
        }

        private fun updateLikeIcon(isFlagged: Boolean) {
            if (isFlagged) {
                likeIcon.setImageResource(R.drawable.ic_heart_filled)
            } else {
                likeIcon.setImageResource(R.drawable.ic_heart_outline)
            }
        }

        private fun incrementLikes(interpretation: Interpretation) {
            firestore.collection("obras").document(firestoreId).get()
                .addOnSuccessListener { document ->
                    val comments = document.get("comments") as? List<Map<String, Any>> ?: return@addOnSuccessListener
                    val updatedComments = comments.map { comment ->
                        if (comment["username"] == interpretation.username && comment["text"] == interpretation.text) {
                            comment.toMutableMap().apply {
                                this["likes"] = convertToLong(this["likes"]) + 1
                            }
                        } else {
                            comment
                        }
                    }
                    firestore.collection("obras").document(firestoreId).update("comments", updatedComments)
                }
        }

        private fun decrementLikes(interpretation: Interpretation) {
            firestore.collection("obras").document(firestoreId).get()
                .addOnSuccessListener { document ->
                    val comments = document.get("comments") as? List<Map<String, Any>> ?: return@addOnSuccessListener
                    val updatedComments = comments.map { comment ->
                        if (comment["username"] == interpretation.username && comment["text"] == interpretation.text) {
                            comment.toMutableMap().apply {
                                this["likes"] = convertToLong(this["likes"]) - 1
                            }
                        } else {
                            comment
                        }
                    }
                    firestore.collection("obras").document(firestoreId).update("comments", updatedComments)
                }
        }

        private fun convertToLong(likes: Any?): Long {
            return when (likes) {
                is Long -> likes
                is String -> likes.toLongOrNull() ?: 0L
                else -> 0L
            }
        }
    }
}
