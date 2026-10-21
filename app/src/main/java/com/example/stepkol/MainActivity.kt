package com.example.stepkol

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val history = ArrayList<StepEntry>()
    private lateinit var adapter: ArrayAdapter<StepEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etSteps = findViewById<EditText>(R.id.etSteps)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        val lvHistory = findViewById<ListView>(R.id.lvHistory)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
        lvHistory.adapter = adapter

        btnAdd.setOnClickListener {
            val steps = etSteps.text.toString().toIntOrNull()
            val weight = etWeight.text.toString().toDoubleOrNull()
            val height = etHeight.text.toString().toIntOrNull()

            if (steps == null || steps <= 0) {
                etSteps.error = "Введите число шагов"
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

            val entry = StepCalculator.calculate(steps, weight, height)
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
}
