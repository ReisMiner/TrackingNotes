package xyz.reisminer.trackingnotes

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


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
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0)as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            } else {
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tileList[i].setOnClickListener { openAccordingTile((tileList[i].getChildAt(0)as TextView).text as String) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            }
        }

        for (trr in tr)
            table.addView(
                trr,
                params
            )
    }

    private fun openAccordingTile(title: String) {
        val intent = Intent(this, writeNote::class.java)
        intent.putExtra("new",false)
        intent.putExtra("title",title)
        startActivity(intent)
    }

}