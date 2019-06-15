package ru.tkasatkin.timesheet_calculator

import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

data class TimeCalculation(var timeExpression: String, var timeResult: String)

data class DurationCalculation(var durationExpression: String, var durationResult: String)

class TimeCalculationModel : ItemViewModel<TimeCalculation>() {
    var timeExpression = bind { SimpleStringProperty(item?.timeExpression ?: "") }
    var timeResult = bind { SimpleStringProperty(item?.timeResult ?: "") }
}

class DurationCalculationModel : ItemViewModel<DurationCalculation>() {
    var durationExpression = bind { SimpleStringProperty(item?.durationExpression ?: "") }
    var durationResult = bind { SimpleStringProperty(item?.durationResult ?: "") }
}
