package com.android.notesync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ButtonActivity : AppCompatActivity() {

    private val database = Firebase.database("https://notesync-d4cef-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("TemaOne")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)

        setupTextView()
        setupBackButton()
        setupDatabaseListener()
    }

    private fun setupTextView() {
        val textView: EditText = findViewById(R.id.editText)
        textView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveTextToDatabase(textView.text.toString())
                true
            } else false
        }
    }
    private fun updateMainText(value: String) {
        val textView: TextView = findViewById(R.id.editText)
        textView.text = value
    }
    private fun setupDatabaseListener() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java) ?: ""
                updateMainText(value)

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here

            }
        })
    }
    private fun saveTextToDatabase(text: String) {
        myRef.setValue(text)
    }

    private fun setupBackButton()
    {
        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener{
            startMainActivity()
        }
    }
    private fun startMainActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}