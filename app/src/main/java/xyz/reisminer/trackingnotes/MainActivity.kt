package xyz.reisminer.trackingnotes

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
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

        val o = TableLayout.inflate(this, R.layout.tiles_home, null)
        val o2 = TableLayout.inflate(this, R.layout.tiles_home, null)

        val x = mutableListOf<TableLayout>()

        var helper = true

        for (i in 0..y) {
            if (helper) {
                tr.add(TableRow(this))
                tr[i / 2].layoutParams = params
                x.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tr[i / 2].addView(x[i])
                helper = !helper;
            } else {
                x.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                tr[i / 2].addView(x[i])
                helper = !helper;
            }
        }

        for (trr in tr)
            table.addView(
                trr,
                params
            )
    }

}