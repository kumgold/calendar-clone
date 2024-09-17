package com.goldcompany.apps.calendar.home.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.util.convertMilliToDate
import com.goldcompany.apps.data.data.schedule.Schedule

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    goToScheduleDetail: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
    LazyColumn(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
    ) {
        items(
            items = schedules,
            key = { schedule -> schedule.id }
        ) { schedule ->
            ScheduleItem(
                schedule = schedule,
                goToScheduleDetail = { id ->
                    goToScheduleDetail(id)
                }
            )
        }
    }
}

@Composable
private fun ScheduleItem(
    schedule: Schedule,
    goToScheduleDetail: (String) -> Unit
) {
    val color = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .padding(start = 2.dp)
            .fillMaxWidth()
            .drawBehind {
                val height = size.height

                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 15f
                )
            }
            .clickable { goToScheduleDetail(schedule.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
        Column(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_margin_large)),
        ) {
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin_small)))
            Row {
                Text(
                    text = schedule.startDateTimeMilli.convertMilliToDate(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
                Text(
                    text = " - ",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
                Text(
                    text = schedule.endDateTimeMilli.convertMilliToDate(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            }
        }
    }
}
