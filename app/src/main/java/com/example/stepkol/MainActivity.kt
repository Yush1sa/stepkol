package com.example.stepkol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val history = ArrayList<StepEntry>()
    private lateinit var adapter: ArrayAdapter<StepEntry>

    // Данные пользователя из профиля
    private var userName = ""
    private var userWeight = 0.0
    private var userHeight = 0

    private lateinit var tvResult: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvUser: TextView

    // Launcher для приёма результата из ProfileActivity (T2 – данные из другого окна в начальное)
    private val profileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            userName = data.getStringExtra(ProfileActivity.EXTRA_NAME) ?: ""
            userWeight = data.getDoubleExtra(ProfileActivity.EXTRA_WEIGHT, 0.0)
            userHeight = data.getIntExtra(ProfileActivity.EXTRA_HEIGHT, 0)
            updateUserDisplay()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etSteps = findViewById<EditText>(R.id.etSteps)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        tvResult = findViewById(R.id.tvResult)
        tvTotal = findViewById(R.id.tvTotal)
        tvUser = findViewById(R.id.tvUser)
        val lvHistory = findViewById<ListView>(R.id.lvHistory)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
        lvHistory.adapter = adapter

        updateUserDisplay()

        btnAdd.setOnClickListener {
            val steps = etSteps.text.toString().toIntOrNull()

            if (steps == null || steps <= 0) {
                etSteps.error = "Введите число шагов"
                return@setOnClickListener
            }

            // Вес и рост берутся из профиля
            if (userWeight <= 0) {
                Toast.makeText(this, "Сначала укажите вес в профиле", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userHeight <= 0) {
                Toast.makeText(this, "Сначала укажите рост в профиле", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entry = StepCalculator.calculate(steps, userWeight, userHeight)
            tvResult.text = "Дистанция: %.2f км\nКалории: %.1f ккал"
                .format(entry.distanceKm, entry.calories)

            history.add(0, entry)
            adapter.notifyDataSetChanged()
            updateTotal(tvTotal)

            etSteps.text.clear()
        }

        lvHistory.setOnItemLongClickListener { _, _, position, _ ->
            history.removeAt(position)
            adapter.notifyDataSetChanged()
            updateTotal(tvTotal)
            true
        }
    }

    private fun updateTotal(tvTotal: TextView) {
        tvTotal.text = "Всего: %d шагов, %.1f ккал"
            .format(StepCalculator.totalSteps(history), StepCalculator.totalCalories(history))
    }

    private fun updateUserDisplay() {
        tvUser.text = if (userName.isNotEmpty()) {
            getString(R.string.stat_user, userName)
        } else {
            "Пользователь: не указан"
        }
    }

    // --- Навигационное меню (N3 – Menu) ---

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main -> {
                // Уже на главном экране
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
        intent.putExtra(ProfileActivity.EXTRA_NAME, userName)
        intent.putExtra(ProfileActivity.EXTRA_WEIGHT, userWeight)
        intent.putExtra(ProfileActivity.EXTRA_HEIGHT, userHeight)
        profileLauncher.launch(intent)
    }

    private fun openStatistics() {
        val intent = Intent(this, StatisticsActivity::class.java)
        intent.putExtra(StatisticsActivity.EXTRA_USER_NAME, userName)
        intent.putExtra(StatisticsActivity.EXTRA_TOTAL_STEPS, StepCalculator.totalSteps(history))
        intent.putExtra(StatisticsActivity.EXTRA_TOTAL_CALORIES, StepCalculator.totalCalories(history))
        startActivity(intent)
    }
}
