package com.android.notesync

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Firebase database references
    private val database = Firebase.database("https://notesync-d4cef-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("change")

    // Local storage for user preferences
    private lateinit var preferences: NotesPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize preferences
        preferences = NotesPreferences(this)

        setupUsernameDisplay()
        setupTextView()
        setupAddButton()
        setupBackButton()
        setupDatabaseListener()
    }


    private fun setupUsernameDisplay() {
        val loginName= preferences.getUsername()
        // With else
        if (loginName != null) {
            Log.w("NAME", loginName)
        } else {
            Log.w("NAME", "Nullis")
        }
    }

    // Set up the main text view where data is displayed and saved
    private fun setupTextView() {
        val textView: EditText = findViewById(R.id.textView)
        textView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveTextToDatabase(textView.text.toString())
                true
            } else false
        }
    }

    // Set up button for adding new EditText views
    private fun setupAddButton() {
        val containerLayout: LinearLayout = findViewById(R.id.containerLayout)
        val addButton: ImageButton = findViewById(R.id.buttonAdd)
        val editTextManager = EditTextManager(this)
        addButton.setOnClickListener {
            editTextManager.addNewEditText(containerLayout)
        }
    }


    private fun setupBackButton()
    {
        val backButton:Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener{
            preferences.setLoggedOff(false)
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    // Set up listener to update text when data changes in Firebase
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

    private fun updateMainText(value: String) {
        val textView: TextView = findViewById(R.id.textView)
        textView.text = value
    }
}
class EditTextManager(private val context: Context) {

    fun addNewEditText(containerLayout: LinearLayout) {
        val newEditText = createNewEditText()
        applyEditTextStyle(newEditText)
        addEditTextToContainer(newEditText, containerLayout)
    }
    private fun createNewEditText(): EditText {
        val newEditText = EditText(context)
        newEditText.textSize = 16f
        newEditText.height = 200
        newEditText.gravity = Gravity.TOP
        return newEditText
    }

    private fun applyEditTextStyle(editText: EditText) {
        editText.setBackgroundColor(Color.WHITE) // Set background color to white
        editText.setPadding(16, 16, 16, 16)
        editText.elevation = context.resources.getDimension(R.dimen.elevation)
    }

    private fun addEditTextToContainer(editText: EditText, containerLayout: LinearLayout) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(context.resources.getDimensionPixelSize(R.dimen.margin_left),
            0,
            context.resources.getDimensionPixelSize(R.dimen.margin_right),
            context.resources.getDimensionPixelSize(R.dimen.margin_bottom))

        editText.layoutParams = layoutParams
        containerLayout.addView(editText)
    }
}