package com.kyberswap.android.presentation.main.balance.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.kyberswap.android.domain.model.Chart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by philipp on 02/06/16.
 */
class CandleStickXAxisValueFormatter(private val chart: Chart, private val resolution: Int) :
    ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        if (chart.t.isEmpty()) return ""
        if (chart.t.last().toFloat() < chart.t[0].toFloat() + value * 60.0 * resolution) {
            return ""
        }
        if (value.toInt().toFloat() == value && value < chart.t.size) {
            val time = chart.t[value.toInt()]
            val cal = Calendar.getInstance()
            cal.timeInMillis = time.toLong() * 1000L
            return SimpleDateFormat(
                if (resolution == 60 * 24) "HH:mm dd/MM/yyyy" else "HH:mm dd/MM",
                Locale.getDefault()
            ).format(cal.time)
        } else {
            return ""
        }
    }
}
