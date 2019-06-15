package ru.tkasatkin.timesheet_calculator

import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import tornadofx.*
import java.time.Duration
import kotlin.math.absoluteValue

class CalculatorView : View("Time calculator") {

    private val timeModel: TimeCalculationModel by inject()
    private val durationModel: DurationCalculationModel by inject()

    private val calculatorController: CalculatorController by inject()

    override val root: Parent = form {
        fieldset {
            vbox {
                field("Input time expression:") {
                    textfield(timeModel.timeExpression) {
                        onKeyPressed = EventHandler { keyEvent ->
                            if (keyEvent.code == KeyCode.ENTER && timeModel.validate()) {
                                val calculatedDuration =
                                    calculatorController.calculateDurationByLastValidatedTimeExpression()

                                timeModel.timeResult.value = calculatedDuration.toFormattedString()
                                timeModel.commit()
                            }
                        }
                        validator(trigger = ValidationTrigger.None) {
                            when {
                                it.isNullOrBlank() -> error("Expression must be specified")
                                !calculatorController.validateTimeExpression(it) ->
                                    error("""Invalid expression format. Example: '16:23 - 13:47'.""")
                                else -> null
                            }
                        }
                    }
                }
                label(timeModel.timeResult)
                field("Input duration expression:") {
                    textfield(durationModel.durationExpression) {
                        onKeyPressed = EventHandler { keyEvent ->
                            if (keyEvent.code == KeyCode.ENTER && durationModel.validate()) {
                                val calculatedDuration =
                                    calculatorController.calculateDurationByLastValidatedExpression()

                                durationModel.durationResult.value = calculatedDuration.toFormattedString()
                                durationModel.commit()
                            }
                        }
                        validator(trigger = ValidationTrigger.None) {
                            when {
                                it.isNullOrBlank() -> error("Expression must be specified")
                                !calculatorController.validateDurationExpression(it) ->
                                    error("""Invalid expression format. Example: '5h 3m - 1h5m + 8m'.""")
                                else -> null
                            }
                        }
                    }
                }
                label(durationModel.durationResult)
                button("Reset") {
                    action {
                        timeModel.rollback()
                        durationModel.rollback()
                    }
                }
            }
        }
    }

    private fun Duration.toFormattedString(): String {
        val minutes = toMinutes().absoluteValue
        return "${minutes / 60}h ${minutes % 60}m"
    }

}