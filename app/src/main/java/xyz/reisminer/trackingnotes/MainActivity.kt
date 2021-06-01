package xyz.reisminer.trackingnotes

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.io.BufferedReader
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createHomeTiles(10)

        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener {
            val intent = Intent(this, writeNote::class.java)
            startActivity(intent)
        }
    }

    private fun createHomeTiles(y: Int) {
        val table = findViewById<TableLayout>(R.id.mainTable)

        val params = TableLayout.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        params.setMargins(10, 10, 10, 10)

        val tr = mutableListOf<TableRow>()

        val tileList = mutableListOf<TableLayout>()

        var helper = true


        for (i in 0..y) {
            if (helper) {
                tr.add(TableRow(this))
                tr[i / 2].layoutParams = params
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0) as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            } else {
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0) as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            }
        }

        File(filesDir.toURI()).walk().forEach { daFile ->
            var i =0;
            if (helper) {
                tr.add(TableRow(this))
                tr[i / 2].layoutParams = params
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0) as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            } else {
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0) as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            }
            i++;
        }

        for (trr in tr)
            table.addView(trr,params)
    }

    private fun openAccordingTile(title: String) {
        val intent = Intent(this, writeNote::class.java)

        File(filesDir.toURI()).walk().forEach { daFile ->
            if(title==daFile.name){
                intent.putExtra("title", title)
                val bufferedReader: BufferedReader = File(daFile.toURI()).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                intent.putExtra("content", inputString)
            }
        }
        intent.putExtra("new", false)
        startActivity(intent)
    }

}