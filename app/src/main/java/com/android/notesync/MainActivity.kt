package com.android.notesync

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
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

        val button: Button = findViewById<Button>(R.id.button)
        val textView: EditText = findViewById(R.id.textView)

        button.setOnClickListener{
            updateTextView(textView)
        }


        myRef.addValueEventListener(object: ValueEventListener {
            val TAG = "Firebase"
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue()
                updateText(textView,value.toString())
                Log.d(TAG, "Value is: " + value)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }



    private fun updateTextView( textView: TextView)
    {
        myRef.setValue(textView.text.toString())
    }

    private fun updateText(textView: TextView, value: String)
    {
        textView.text = value

    }

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
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = context.resources.getDimensionPixelSize(R.dimen.rounded_corner).toFloat()
        shape.setColor(Color.parseColor("#87CEFA"))

        editText.background = shape
        editText.setPadding(16, 16, 16, 16)
    }

    private fun addEditTextToContainer(editText: EditText, containerLayout: LinearLayout) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, context.resources.getDimensionPixelSize(R.dimen.margin_bottom))
        editText.layoutParams = layoutParams
        containerLayout.addView(editText)
    }
}