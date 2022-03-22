package com.example.diet_memo

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    val dataModelList = mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("myMemo")

        val listView = findViewById<ListView>(R.id.mainLV)
        val adapter_list = ListViewAdapter(dataModelList)
        listView.adapter = adapter_list

        // Read from the database
        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModelList.clear()
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //모든 데이터를 하나하나 dataModelList에 넣음.
                for(dataModel in dataSnapshot.children){
                    Log.d("Data", dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)
                }

                //list View update, FireBaes가 비동기 처리이므로 database에서 불러온 후 업데이트 필요.
                adapter_list.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })


        val writeBtn = findViewById<ImageView>(R.id.wireBtn)
        writeBtn.setOnClickListener {

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("운동 메모 dialog")

            val mAlertDialog = mBuilder.show()
            val dateSelectBtn = mAlertDialog.findViewById<Button>(R.id.dateSelectBtn)
            var dateText = ""

            //날짜 선택하는 dialog
            dateSelectBtn?.setOnClickListener {

                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)


                val dlg = DatePickerDialog(this, object: DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        //month는 +1
                        Log.d("MAIN", "${year} /  ${month+1} /  ${dayOfMonth}")
                        dateSelectBtn.setText("${year} /  ${month+1} /  ${dayOfMonth}")

                        dateText = "${year} /  ${month+1} /  ${dayOfMonth}"
                    }

                }, year, month, date)
                dlg.show()
            }


            //저장
            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn?.setOnClickListener {

                val healthMemo = mAlertDialog.findViewById<EditText>(R.id.healthMemo)?.text.toString()
                val database = Firebase.database
                //여기에 data 담기, 현재 user의 uid 추가 가능.
                val myRef = database.getReference("myMemo").child(Firebase.auth.currentUser!! .uid)

                val model = DataModel(dateText, healthMemo)

                //객체도 가능
               myRef.push().setValue(model)

                //dialog 끄기
               mAlertDialog.dismiss()

            }
        }
    }
}