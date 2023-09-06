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
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.setMargins
import com.google.android.material.appbar.MaterialToolbar
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

        preferences = NotesPreferences(this)

        setupNavigation()
        initializeDbRef()
        database()
    }

    fun setupNavigation()
    {
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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
                            createNoteButton(layoutNotes,key,data)

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
    fun createNoteButton(layoutNotes:LinearLayout, key:Any?, text:Any?){

        val noteButton = Button(this)

        var params =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(0,10,20,30)

        noteButton.layoutParams = params;
        noteButton.text = text.toString()

        noteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue700))

        layoutNotes.addView(noteButton)
        noteButton.setOnClickListener{
            startButtonActivity(key.toString())
        }

    }
    fun initializeDbRef() {
        db = Firebase.database.reference
        db.child("Notes").child("Notes1").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startButtonActivity(noteId:String) {
        val intent = Intent(this, ButtonActivity::class.java)
        intent.putExtra("Id", noteId)
        startActivity(intent)
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