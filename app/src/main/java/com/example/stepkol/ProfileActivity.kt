package com.example.stepkol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    // Ключи для передачи данных между окнами
    companion object {
        const val EXTRA_NAME = "com.example.stepkol.EXTRA_NAME"
        const val EXTRA_WEIGHT = "com.example.stepkol.EXTRA_WEIGHT"
        const val EXTRA_HEIGHT = "com.example.stepkol.EXTRA_HEIGHT"
    }

    private lateinit var etName: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etName = findViewById(R.id.etName)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Получаем текущие данные из главного окна (если переданы)
        val weight = intent.getDoubleExtra(EXTRA_WEIGHT, 0.0)
        val height = intent.getIntExtra(EXTRA_HEIGHT, 0)
        val name = intent.getStringExtra(EXTRA_NAME)

        if (name.isNullOrEmpty().not()) etName.setText(name)
        if (weight > 0) etWeight.setText(weight.toString())
        if (height > 0) etHeight.setText(height.toString())

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val weight = etWeight.text.toString().toDoubleOrNull()
            val height = etHeight.text.toString().toIntOrNull()

            if (name.isEmpty()) {
                etName.error = "Введите имя"
                return@setOnClickListener
            }
            if (weight == null || weight <= 0) {
                etWeight.error = "Введите вес"
                return@setOnClickListener
            }
            if (height == null || height <= 0) {
                etHeight.error = "Введите рост"
                return@setOnClickListener
            }

            // Отправляем данные обратно в главное окно (T2)
            val result = Intent()
            result.putExtra(EXTRA_NAME, name)
            result.putExtra(EXTRA_WEIGHT, weight)
            result.putExtra(EXTRA_HEIGHT, height)
            setResult(Activity.RESULT_OK, result)
            Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_profile -> {
                openProfile()
                true
            }
            R.id.menu_statistics -> {
                openStatistics()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.EXTRA_NAME, etName.text.toString().trim())
        intent.putExtra(ProfileActivity.EXTRA_WEIGHT, etWeight.text.toString().toDoubleOrNull() ?: 0.0)
        intent.putExtra(ProfileActivity.EXTRA_HEIGHT, etHeight.text.toString().toIntOrNull() ?: 0)
        startActivity(intent)
    }

    private fun openStatistics() {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }
}