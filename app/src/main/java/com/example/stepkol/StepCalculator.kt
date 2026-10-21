package com.example.stepkol

data class StepEntry(val steps: Int, val distanceKm: Double, val calories: Double) {
    override fun toString(): String =
        "%d шагов — %.2f км — %.1f ккал".format(steps, distanceKm, calories)
}

object StepCalculator {

    const val STEP_LENGTH_COEFF = 0.415

    const val CALORIES_COEFF = 0.57

    fun stepLengthCm(heightCm: Int): Double = heightCm * STEP_LENGTH_COEFF

    fun distanceKm(steps: Int, heightCm: Int): Double =
        steps * stepLengthCm(heightCm) / 100000

    fun calories(weightKg: Double, distanceKm: Double): Double =
        CALORIES_COEFF * weightKg * distanceKm

    fun calculate(steps: Int, weightKg: Double, heightCm: Int): StepEntry {
        require(steps > 0) { "steps must be positive" }
        require(weightKg > 0) { "weight must be positive" }
        require(heightCm > 0) { "height must be positive" }
        val distance = distanceKm(steps, heightCm)
        return StepEntry(steps, distance, calories(weightKg, distance))
    }

    fun totalSteps(history: List<StepEntry>): Int = history.sumOf { it.steps }

    fun totalCalories(history: List<StepEntry>): Double = history.sumOf { it.calories }
}
