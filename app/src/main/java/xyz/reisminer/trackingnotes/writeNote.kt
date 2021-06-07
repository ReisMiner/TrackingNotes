package xyz.reisminer.trackingnotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.io.File

class writeNote : AppCompatActivity() {

    private lateinit var titleEditView: EditText
    private lateinit var contentEditView: EditText
    private var filePath: String = "huh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_note)

        titleEditView = findViewById(R.id.TextViewSaveTitle)
        contentEditView = findViewById(R.id.TextViewSaveText)

        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener {
            saveNote()
        }
        val deleteButton = findViewById<AppCompatButton>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            deleteNote()
        }

        //set title and content according to pressed tile
        if (!intent.getBooleanExtra("new", true)) {
            titleEditView.setText(intent.getStringExtra("title"))
            contentEditView.setText(intent.getStringExtra("content"))
            filePath = intent.getStringExtra("path").toString()
        }
    }

    private fun saveNote() {

        if (titleEditView.text.toString().isEmpty()) {
            Toast.makeText(this, "Cannot save note without title!", Toast.LENGTH_SHORT).show()
        } else {
            openFileOutput(titleEditView.text.toString(), Context.MODE_PRIVATE).use {
                it.write(contentEditView.text.toString().toByteArray())
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteNote() {

        if (filePath != "huh") {

            val file = File(filePath)
            val deleted = file.delete()

            if (deleted) {
                Toast.makeText(this, "Deleted Note", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Couldn't Delete Note", Toast.LENGTH_SHORT).show()
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}