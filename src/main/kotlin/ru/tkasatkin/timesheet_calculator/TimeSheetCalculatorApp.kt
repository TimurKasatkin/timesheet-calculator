package ru.tkasatkin.timesheet_calculator

import tornadofx.*

fun main(args: Array<String>) {
    launch<TimeSheetCalculatorApp>(args)
}

class TimeSheetCalculatorApp : App(CalculatorView::class)