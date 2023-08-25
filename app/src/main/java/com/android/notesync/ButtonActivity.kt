package com.android.notesync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ButtonActivity () : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)




        setupTextView()
        setupBackButton()
//        setupDatabaseListener()
        initializeDbRef()

    }

    fun initializeDbRef() {
        db = Firebase.database.reference
        db.child("Notes").child(getNoteId()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java) ?: ""
                updateMainText(value)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }
    fun getNoteId(): String {
        val intent = intent
        val noteId:String = intent.getStringExtra("Id") ?: "NotFound"
        return noteId
    }
    private fun setupTextView() {
        val textView: EditText = findViewById(R.id.editText)

        textView.setOnKeyListener { _, keyCode, event ->
            saveTextToDatabase(textView.text.toString())
            Log.i("Testing5", textView.text.toString().length.toString())
            Log.i("Testing6", textView.text.length.toString())
            false // Don't consume the event
        }
    }

    private fun saveTextToDatabase(text: String) {
        db.child("Notes").child(getNoteId()).setValue(text)
    }
    private fun updateMainText(value: String) {
        val textView: TextView = findViewById(R.id.editText)
        textView.text = value
    }



    private fun setupBackButton()
    {
        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener{
            startMainActivity()
        }
    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}








