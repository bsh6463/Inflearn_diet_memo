package com.example.diet_memo

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                val healthMemo = findViewById<EditText>(R.id.healthMemo)?.text.toString()
                val database = Firebase.database
                //여기에 data 담기
                val myRef = database.getReference("myMemo")

                val model = DataModel(dateText, healthMemo)

                //객체도 가능
               myRef.push().setValue(model)

            }
        }
    }
}