package xyz.reisminer.trackingnotes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton

class writeNote : AppCompatActivity() {

    private lateinit var titleEditView: EditText;
    private lateinit var contentEditView: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_note)

        titleEditView = findViewById(R.id.TextViewSaveTitle)
        contentEditView = findViewById(R.id.TextViewSaveText)

        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener{
            saveNote()
        }
        val deleteButton = findViewById<AppCompatButton>(R.id.deleteButton)
        deleteButton.setOnClickListener{
            deleteNote()
        }

        //set title and content according to pressed tile
        if(!intent.getBooleanExtra("new",true)){
            titleEditView.setText(intent.getStringExtra("title"))
            contentEditView.setText(intent.getStringExtra("content"))
        }
    }

    private fun saveNote (){

        openFileOutput(titleEditView.text.toString(), Context.MODE_PRIVATE).use {
            it.write(contentEditView.text.toString().toByteArray())
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun deleteNote(){
        //todo
    }
}