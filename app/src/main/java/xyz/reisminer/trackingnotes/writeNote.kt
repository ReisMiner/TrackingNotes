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
        val deleteButton = findViewById<AppCompatButton>(R.id.deleteButton)
        createButton.setOnClickListener{
            deleteNote()
        }
    }

    private fun saveNote (){
        val titleEditView = findViewById<EditText>(R.id.TextViewSaveTitle)
        val contentEditView = findViewById<EditText>(R.id.TextViewSaveText)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun deleteNote(){

    }
}