package com.example.stepkol

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_NAME = "com.example.stepkol.EXTRA_USER_NAME"
        const val EXTRA_TOTAL_STEPS = "com.example.stepkol.EXTRA_TOTAL_STEPS"
        const val EXTRA_TOTAL_CALORIES = "com.example.stepkol.EXTRA_TOTAL_CALORIES"
    }

    private lateinit var tvUser: TextView
    private lateinit var tvTotalSteps: TextView
    private lateinit var tvTotalCalories: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        tvUser = findViewById(R.id.tvUser)
        tvTotalSteps = findViewById(R.id.tvTotalSteps)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)

        val name = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
        val steps = intent.getIntExtra(EXTRA_TOTAL_STEPS, 0)
        val calories = intent.getDoubleExtra(EXTRA_TOTAL_CALORIES, 0.0)

        tvUser.text = getString(R.string.stat_user, name)
        tvTotalSteps.text = getString(R.string.stat_total_steps, steps)
        tvTotalCalories.text = getString(R.string.stat_total_calories, calories)
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
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_statistics -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}