package com.example.realm


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var edtFistName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var txtDataFistName: TextView
    private lateinit var txtDataLastName: TextView
    private lateinit var btnSave: Button
    private lateinit var btnRealData: Button
    private lateinit var realm: Realm
    private var user=User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtFistName=findViewById(R.id.edtFistName)
        edtLastName=findViewById(R.id.edtLastName)
        txtDataFistName=findViewById(R.id.txtDataFistName)
        txtDataLastName=findViewById(R.id.txtDataLastName)
        btnSave=findViewById(R.id.btnSave)
        btnRealData=findViewById(R.id.btnRealData)

        btnSave.setOnClickListener {
            saveData()
        }
        btnRealData.setOnClickListener {
            realData()
        }
        val configuration=RealmConfiguration.create(schema = setOf(User::class,Hai::class))
        realm=Realm.open(configuration)


    }
    private fun realData() {
        val all=realm.query<User>().find()
        val userByName=realm.query<User>("fistName = $0", "${user.fistName}")
        val filteredByHai = realm.query<User>("hai.age = $0 AND hai.name = $1", 23, "Lo Van Sun").find()
        GlobalScope.launch(Dispatchers.IO) {
            filteredByHai.asFlow().collect { result->
                GlobalScope.launch(Dispatchers.Main){
                    result.list.forEach {
                        txtDataFistName.text=it.hai?.name.toString()
                    }
                }
            }
        }
    }

    private fun saveData() {
        val text1=edtFistName.text.toString()
        val text2=edtLastName.text.toString()
        user.apply {
            fistName=text1
            lastName=text2
            hai=Hai().apply {
                name="Lo Van Sun"
                age=23
            }
        }
        realm.writeBlocking {
            val managedUser=copyToRealm(user)
        }
    }
}