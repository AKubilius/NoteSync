package com.android.notesync

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ButtonActivity (): AppCompatActivity() {

    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupTextView()
//        setupDatabaseListener()
        initializeDbRef()
    }




    fun initializeDbRef() {
        db = Firebase.database.reference
        val textView: EditText = findViewById(R.id.editText)
        db.child("Notes").child(getNoteId()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val valueFromDatabase = snapshot.getValue(String::class.java) ?: ""
                val currentText = textView.text.toString()

                if (currentText != valueFromDatabase) {
                    val cursorPosition = textView.selectionStart
                    textView.setText(valueFromDatabase)
                    textView.setSelection(Math.min(cursorPosition, valueFromDatabase.length))
                }
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

        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val cursorPosition = textView.selectionStart
                saveTextToDatabase(s.toString())
                textView.setSelection(cursorPosition) // Restore cursor position
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun saveTextToDatabase(text: String) {
        db.child("Notes").child(getNoteId()).setValue(text)
    }
    private fun updateMainText(value: String) {
        val textView: TextView = findViewById(R.id.editText)
        textView.text = value
    }




    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}








