package com.goldcompany.apps.calendar.home.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.goldcompany.apps.calendar.R
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
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(5.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
    ) {
        items(
            items = schedules,
            key = { schedule -> schedule.id }
        ) { schedule ->
            ScheduleItem(schedule = schedule) {

            }
        }
    }
}

@Composable
private fun ScheduleItem(
    schedule: Schedule,
    goToScheduleDetail: () -> Unit
) {
    Text(text = schedule.title)
}