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
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Firebase database references
    private val database = Firebase.database("https://notesync-d4cef-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("change")

    private lateinit var db: DatabaseReference

    // Local storage for user preferences
    private lateinit var preferences: NotesPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize preferences


        preferences = NotesPreferences(this)

        setupUsernameDisplay()
        setupTextView()
       // setupAddButton()
        setupBackButton()
        setupDatabaseListener()
        setupMyButton()
        initializeDbRef()
        database()
    }

    fun database(){
        val layoutNotes:LinearLayout = findViewById(R.id.notes)
        val ref = FirebaseDatabase.getInstance().reference.child("Notes")
        ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.value
                    if (value is Map<*, *>) {
                        for ((key, data) in value) {
                            Log.w("DataSnapShot", "Key: $key, Value: $data")
                            createNoteButton(layoutNotes, data)

                        }
                    } else {
                        Log.w("DataSnapShot", "Value is not a Map: $value")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle databaseError
                }
            })
    }

    fun createNoteButton(layoutNotes:LinearLayout, text:Any?){

        val button_dynamic = Button(this)
        button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        button_dynamic.text = text.toString()
        layoutNotes.addView(button_dynamic)

    }

    fun initializeDbRef() {
        db = Firebase.database.reference
        db.child("Notes").child("Notes1").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    private fun setupUsernameDisplay() {

    }

    private fun setupNotes() {
        val loginName= preferences.getUsername()

    }

    private fun setupMyButton() {
        val myButton:Button = findViewById(R.id.myButton)
        myButton.setOnClickListener{
            startButtonActivity()
        }
    }

    private fun updateMyButton(value:String) {
       val myButton:Button = findViewById(R.id.myButton)
        myButton.text = value;
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
//    private fun setupAddButton() {
//        val containerLayout: LinearLayout = findViewById(R.id.containerLayout)
//        val addButton: ImageButton = findViewById(R.id.buttonAdd)
//        val editTextManager = EditTextManager(this)
//        addButton.setOnClickListener {
//            editTextManager.addNewEditText(containerLayout)
//        }
//    }

    private fun setupBackButton()
    {
        val backButton:Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener{
            preferences.setLoggedOff(false)
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startButtonActivity() {
        val intent = Intent(this, ButtonActivity::class.java)
        startActivity(intent)
    }

    // Set up listener to update text when data changes in Firebase
    private fun setupDatabaseListener() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java) ?: ""
                updateMainText(value)
                updateMyButton(value)
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