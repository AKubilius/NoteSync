package com.android.notesync

import android.content.Context
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    val database = Firebase.database("https://notesync-d4cef-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("change")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        addName()


        val textView: EditText = findViewById(R.id.textView)

        textView.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateTextView(textView)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        val containerLayout: LinearLayout = findViewById(R.id.containerLayout)
        val addButton: ImageButton = findViewById(R.id.buttonAdd)
        val editTextManager = EditTextManager(this)
        addButton.setOnClickListener {
            editTextManager.addNewEditText(containerLayout)

        }

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue()
                updateText(textView,value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun updateTextView( textView: TextView) {
        myRef.setValue(textView.text.toString()) }

    private fun updateText(textView: TextView, value: String) {
        textView.text = value }

    fun addName()
    {
        var loginName = intent.getStringExtra("loginName")
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