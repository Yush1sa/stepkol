package com.example.stepkol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StepCalculatorTest {

    private val delta = 1e-9

    @Test
    fun stepLength_isHeightTimesCoefficient() {
        assertEquals(72.625, StepCalculator.stepLengthCm(175), delta)
    }

    @Test
    fun distance_forTenThousandSteps_height175() {
        // 10000 * 72.625 / 100000 = 7.2625 км
        assertEquals(7.2625, StepCalculator.distanceKm(10000, 175), delta)
    }

    @Test
    fun distance_isProportionalToSteps() {
        val one = StepCalculator.distanceKm(1000, 170)
        val two = StepCalculator.distanceKm(2000, 170)
        assertEquals(two, one * 2, delta)
    }

    @Test
    fun distance_growsWithHeight() {
        assertTrue(StepCalculator.distanceKm(5000, 190) > StepCalculator.distanceKm(5000, 160))
    }

    @Test
    fun calories_weight70_distance5km() {
        assertEquals(199.5, StepCalculator.calories(70.0, 5.0), delta)
    }

    @Test
    fun calories_zeroDistance_isZero() {
        assertEquals(0.0, StepCalculator.calories(80.0, 0.0), delta)
    }

    @Test
    fun calculate_fullEntry() {
        val e = StepCalculator.calculate(10000, 70.0, 175)
        assertEquals(10000, e.steps)
        assertEquals(7.2625, e.distanceKm, delta)
        assertEquals(0.57 * 70.0 * 7.2625, e.calories, delta)
    }

    @Test
    fun calculate_acceptsFractionalWeight() {
        val e = StepCalculator.calculate(1000, 65.5, 170)
        assertEquals(0.57 * 65.5 * (1000 * 170 * 0.415 / 100000), e.calories, delta)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculate_zeroSteps_throws() {
        StepCalculator.calculate(0, 70.0, 175)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculate_negativeWeight_throws() {
        StepCalculator.calculate(1000, -1.0, 175)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculate_zeroHeight_throws() {
        StepCalculator.calculate(1000, 70.0, 0)
    }

    @Test
    fun totals_ofEmptyHistory_areZero() {
        assertEquals(0, StepCalculator.totalSteps(emptyList()))
        assertEquals(0.0, StepCalculator.totalCalories(emptyList()), delta)
    }

    @Test
    fun totals_sumAllEntries() {
        val history = listOf(
            StepEntry(1000, 0.7, 28.0),
            StepEntry(2000, 1.4, 56.0)
        )
        assertEquals(3000, StepCalculator.totalSteps(history))
        assertEquals(84.0, StepCalculator.totalCalories(history), delta)
    }

    @Test
    fun totals_afterRemovingEntry_areRecalculated() {
        val history = mutableListOf(
            StepEntry(1000, 0.7, 28.0),
            StepEntry(2000, 1.4, 56.0)
        )
        history.removeAt(0)
        assertEquals(2000, StepCalculator.totalSteps(history))
        assertEquals(56.0, StepCalculator.totalCalories(history), delta)
    }

    @Test
    fun stepEntry_toString_containsAllValues() {
        val s = StepEntry(1234, 0.5, 20.0).toString()
        assertTrue(s.contains("1234"))
        assertTrue(s.contains("шагов"))
        assertTrue(s.contains("ккал"))
    }
}
