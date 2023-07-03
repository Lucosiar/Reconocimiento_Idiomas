package com.example.traductor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.traductor.API.retrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etDescripcion: EditText
    private lateinit var bttDetectarLenguaje: Button
    private lateinit var progressBar: ProgressBar

    var allLanguages = emptyList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
        getLanguage()
    }

    private fun initListener() {
        bttDetectarLenguaje.setOnClickListener{
            val text = etDescripcion.text.toString()

            if(text.isNotEmpty()){
                showLoading()
                getTextLanguage(text)
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
    }


    private fun getTextLanguage(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = retrofitService.getTextLanguage(text)

            if(result.isSuccessful){
                checkResult(result.body())
            }else{
                showError()
            }
            cleanText()
            hideLoading()
        }
    }

    private fun cleanText() {
        etDescripcion.setText("")
    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if(detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()){
            val correctLanguages = detectionResponse.data.detections.filter { it.isReliable }

            if(correctLanguages.isNotEmpty()){

                val languageName = allLanguages.find{ it.code == correctLanguages.first().language }

                if(languageName != null){
                    runOnUiThread{
                        Toast.makeText(this,"El idioma es ${languageName.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getLanguage(){
        CoroutineScope(Dispatchers.IO).launch {
            val languages = retrofitService.getLanguages()
            if(languages.isSuccessful){
                allLanguages = languages.body() ?: emptyList()
                showSucces()
            }else{
                showError()
            }
        }
    }

    private fun showSucces(){
        runOnUiThread{
            Toast.makeText(this, "Petición correcta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError(){
        runOnUiThread{
            Toast.makeText(this, "Error al hacer la llamada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView(){
        bttDetectarLenguaje = findViewById(R.id.bttDetectarLenguaje)
        etDescripcion = findViewById(R.id.etDescripcion)
        progressBar = findViewById(R.id.progressBar)
    }
}