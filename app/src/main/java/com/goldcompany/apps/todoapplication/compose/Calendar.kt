package com.goldcompany.apps.todoapplication.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.goldcompany.apps.todoapplication.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    onCalendarItemClick: (Long) -> Unit = {},
    currentDateMilli: Long
) {
    Column(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        var currentSelectedDate by remember { mutableStateOf(LocalDate.now()) }
        val lastDay by remember { mutableIntStateOf(currentSelectedDate.lengthOfMonth()) }
        val firstDayOfWeek = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1).dayOfWeek.value
        val previousWeekLength = if (firstDayOfWeek == 7) {
            0
        } else {
            firstDayOfWeek
        }
        val days by remember { mutableStateOf(IntArray(previousWeekLength).toList() +  IntRange(1, lastDay).toList()) }
        val defaultMargin = Modifier.height(20.dp)

        LaunchedEffect(key1 = currentSelectedDate.dayOfWeek) {
            currentSelectedDate = LocalDate.now()
        }

        DayOfWeekView()
        Spacer(modifier = defaultMargin)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days) { day ->
                val date = currentSelectedDate.withDayOfMonth(day)

                CalendarItem(
                    date = date,
                    isToday = (date == LocalDate.now()),
                    currentDateMilli = currentDateMilli,
                    onItemClick = onCalendarItemClick
                )
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
private fun DayOfWeekView() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        (listOf(DayOfWeek.entries.last()) + DayOfWeek.entries.subList(0, 6)).forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
                    .uppercase(Locale.ROOT),
            )
        }
    }
}

@Composable
private fun CalendarItem(
    date: LocalDate,
    isToday: Boolean,
    currentDateMilli: Long,
    onItemClick: (Long) -> Unit = {},
) {
    val millis = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.0f)
            .border(
                width = 1.dp,
                color = if (isToday) {
                    Color.Red
                } else if (currentDateMilli == millis) {
                    Color.Gray
                } else {
                    MaterialTheme.colorScheme.background
                },
                shape = RoundedCornerShape(10.dp)
            )
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable {
                println(millis)
                onItemClick(millis)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = (date.dayOfMonth).toString(),
        )
    }
}