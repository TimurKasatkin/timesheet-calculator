package ru.tkasatkin.timesheet_calculator

import tornadofx.Controller
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalculatorController : Controller() {

    companion object {
        private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        private const val hoursPattern = """([01]?\d|2[0-3])"""
        private const val minutesPattern = """([0-5]?\d)"""
        private const val minutesPatternWithZeroRequired = """([0-5]\d)"""

        private const val timePattern = "$hoursPattern:$minutesPatternWithZeroRequired"
        private val timeExpressionRegex = "(?<leftOperand>$timePattern) ?- ?(?<rightOperand>$timePattern)".toRegex()

        private const val durationPattern =
            "($hoursPattern[hH] ?$minutesPattern[mM])|($hoursPattern[hH])|($minutesPattern[mM])"
        private val durationRegex =
            ("((?<fullHours>$hoursPattern)[hH] ?(?<fullMinutes>$minutesPattern)[mM])" +
                    "|((?<hours>$hoursPattern)[hH])" +
                    "|((?<minutes>$minutesPattern)[mM])").toRegex()
        private val operatorRegex = "[+-]".toRegex()
        private val durationExpressionRegex = "(($durationPattern) ?[+-] ?)+($durationPattern)".toRegex()
    }

    private var lastTimeExpressionMatchResult: MatchResult? = null
    private var lastDurationExpressionMatchResult: MatchResult? = null

    fun validateTimeExpression(checkedExpression: String): Boolean =
        timeExpressionRegex.matchEntire(checkedExpression)?.let {
            lastTimeExpressionMatchResult = it
        } != null

    fun calculateDurationByLastValidatedTimeExpression(): Duration {
        val matchResult = lastTimeExpressionMatchResult
            ?: throw IllegalStateException("Expression validation must be called first.")
        val (leftOperand, _) = matchResult.groups["leftOperand"]!!
        val (rightOperand, _) = matchResult.groups["rightOperand"]!!
        val leftTime = LocalTime.parse(leftOperand, timeFormatter)
        val rightTime = LocalTime.parse(rightOperand, timeFormatter)
        return Duration.between(leftTime, rightTime)
    }

    fun validateDurationExpression(checkedExpression: String): Boolean =
        durationExpressionRegex.matchEntire(checkedExpression)?.let {
            lastDurationExpressionMatchResult = it
        } != null

    fun calculateDurationByLastValidatedExpression(): Duration {
        val validatedExpression: String = lastDurationExpressionMatchResult?.value
            ?: throw IllegalStateException("Expression validation must be called first.")

        return durationRegex.findAll(validatedExpression)
            .zip(sequenceOf('+') + operatorRegex.findAll(validatedExpression).map { it.value[0] })
            .fold(Duration.ZERO) { result, (operandMatchResult, operator) ->

                val operand = operandMatchResult.run {

                    fun MatchResult.toDuration(): Duration = this.groups["fullHours"]?.let {
                        Duration.ofHours(it.value.toLong())
                            .plusMinutes(this.groups["fullMinutes"]!!.value.toLong())
                    } ?: this.groups["hours"]?.let {
                        Duration.ofHours(it.value.toLong())
                    } ?: this.groups["minutes"]!!.let {
                        Duration.ofMinutes(it.value.toLong())
                    }

                    this.toDuration()
                }

                fun Char.perform(duration1: Duration, duration2: Duration): Duration = when (this) {
                    '+' -> duration1 + duration2
                    '-' -> duration1 - duration2
                    else -> throw IllegalArgumentException("Invalid operator character $this")
                }

                operator.perform(result, operand)
            }

    }

}