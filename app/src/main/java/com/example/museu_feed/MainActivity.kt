package com.example.museu_feed

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.museu_feed.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scanQRButton = findViewById<ImageButton>(R.id.buttonScanQR)
        val adminTextView = findViewById<TextView>(R.id.textViewAdmin)
        val scanTextView = findViewById<TextView>(R.id.textViewScanQR)

        scanQRButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentIntegrator.initiateScan()
        }

        adminTextView.setOnClickListener {
            val intent = Intent(this, LoginAdm::class.java)
            startActivity(intent)
        }

        scanTextView.setOnClickListener {
            val intent = Intent(this, ArtFeed::class.java).apply {
                putExtra("FIRESTORE_ID", "3PVV3zBk1iETxvM1rxy1")
            }
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            val contents = intentResult.contents
            if (contents != null) {
                val intent = Intent(this, ArtFeed::class.java).apply {
                    putExtra("FIRESTORE_ID", contents)
                }
                startActivity(intent)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
