package xyz.reisminer.trackingnotes

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //dynamic fields dings
        val table = findViewById<TableLayout>(R.id.mainTable)

        val params = TableLayout.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        params.setMargins(10,10,10,10)

        val tr = TableRow(this)
        tr.layoutParams = params

        val o = View.inflate(this,R.layout.tiles_home,null)
        val o2 = View.inflate(this,R.layout.tiles_home,null)
        val o3 = View.inflate(this,R.layout.tiles_home,null)

        tr.addView(o)
        tr.addView(o2)

        table.addView(
            tr,
            params
        )
        //dynamic field dings fertig

//        val intent = Intent(this, writeNote::class.java)
//        startActivity(intent)
    }


}