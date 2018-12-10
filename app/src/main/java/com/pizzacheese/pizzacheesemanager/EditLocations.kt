package com.pizzacheese.pizzacheesemanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.*
import com.pizzacheese.pizzacheesemanager.Types.ShipLocation
import kotlinx.android.synthetic.main.activity_edit_locations.*
import kotlinx.android.synthetic.main.activity_edit_products.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.padding
import java.util.*

class EditLocations : AppCompatActivity() {
    var shipLocations = ArrayList<ShipLocation>()
    private lateinit var locationsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_locations)
        locationsRef = FirebaseDatabase.getInstance().getReference("ShipLocations")
        setupLocations()

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val delLoc = shipLocations[position]
            locationsRef.child(delLoc.id).removeValue()
            shipLocations.remove(delLoc)
            return@setOnItemLongClickListener true
        }

        addLocation.setOnClickListener {
            val l = LinearLayout(this)
            l.orientation = LinearLayout.VERTICAL
            val edName = EditText(this)
            edName.hint = "הכנס שם מיקום..."
            val edPrice = EditText(this)
            edPrice.hint = "הכנס מחיר משלוח למיקום..."
            edPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            l.addView(edName)
            l.addView(edPrice)


            alert {
                title = "מיקום חדש"
                customView = l
                l.padding = 10
                positiveButton("הוסף") {
                    val newLoc = ShipLocation(UUID.randomUUID().toString(), edName.text.toString(), edPrice.text.toString().toDouble())
                    locationsRef.child(newLoc.id).setValue(newLoc)
                }
            }.show()
        }
    }

    // sets up the ship locations
    private fun setupLocations() {
        locationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                shipLocations.clear()
                for (snapshot in dataSnapshot.children)
                    shipLocations.add(snapshot.getValue(ShipLocation::class.java)!!)
                setList()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setList() {
        listView.adapter = object : ArrayAdapter<ShipLocation>(this, android.R.layout.simple_list_item_1, shipLocations) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val textView = TextView(context)
                textView.textSize = 21f
                textView.setPadding(20,5,50,5)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(31, 11, 30, 9)
                params.gravity = Gravity.END
                textView.layoutParams = params
                textView.text = this.getItem(position).toString()
                return textView
            }
        }

    }

}
