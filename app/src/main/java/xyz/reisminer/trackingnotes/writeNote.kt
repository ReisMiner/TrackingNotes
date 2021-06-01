package xyz.reisminer.trackingnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton

class writeNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_note)
        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener{
            saveNote()
        }
    }

    fun saveNote (){
        val titleEditView = findViewById(R.id.TextViewSaveTitle) as EditText
        val contentEditView = findViewById(R.id.TextViewSaveText) as EditText

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}