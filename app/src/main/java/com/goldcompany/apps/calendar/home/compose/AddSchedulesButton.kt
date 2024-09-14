package com.goldcompany.apps.calendar.home.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldcompany.apps.calendar.R

@Composable
fun AddSchedulesButton(
    isExpanded: MutableState<Boolean>,
    goToAddSchedule: () -> Unit,
    goToAddTodo: () -> Unit
) {
    val fabSize = 64.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded.value) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f),
        label = ""
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded.value) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f),
        label = ""
    )

    Column {
        Box(
            modifier = Modifier
                .offset(y = (29).dp)
                .size(
                    width = expandedFabWidth,
                    height = (
                            animateDpAsState(
                                if (isExpanded.value) 86.dp else 0.dp,
                                animationSpec = spring(dampingRatio = 4f),
                                label = ""
                            )
                            ).value
                )
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            ExpandedFabItem(
                icon = Icons.Filled.CalendarToday,
                isExpanded = isExpanded.value
            ) {
                goToAddSchedule()
                isExpanded.value = !isExpanded.value
            }
        }

        FloatingActionButton(
            onClick = {
                if (isExpanded.value) {
                    goToAddTodo()
                }

                isExpanded.value = !isExpanded.value
            },
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            shape = RoundedCornerShape(18.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 0.dp,
                focusedElevation = 0.dp,
            )
        ) {
            Crossfade(targetState = isExpanded.value, label = "") { isExpanded ->
                if (isExpanded) {
                    Icon(
                        imageVector =  Icons.Filled.LibraryAddCheck,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(
                                x = animateDpAsState(
                                    targetValue = (-70).dp,
                                    animationSpec = spring(dampingRatio = 3f),
                                    label = ""
                                ).value
                            )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(
                                x = animateDpAsState(
                                    targetValue = 0.dp,
                                    animationSpec = spring(dampingRatio = 3f),
                                    label = ""
                                ).value
                            )
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.todo),
                fontSize = 16.sp,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded.value) 10.dp else 50.dp,
                            animationSpec = spring(dampingRatio = 3f),
                            label = ""
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded.value) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded.value) 350 else 100,
                                delayMillis = if (isExpanded.value) 100 else 0,
                                easing = EaseIn
                            ),
                            label = ""
                        ).value
                    )
            )
        }
    }
}

@Composable
private fun ExpandedFabItem(
    icon: ImageVector,
    isExpanded: Boolean,
    goToAddSchedule: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                goToAddSchedule()
            }
            .padding(all = dimensionResource(id = R.dimen.default_margin_large))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.schedule),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .alpha(
                    animateFloatAsState(
                        targetValue = if (isExpanded) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = if (isExpanded) 350 else 100,
                            delayMillis = if (isExpanded) 100 else 0,
                            easing = EaseIn
                        ), label = ""
                    ).value
                )
                .fillMaxWidth()
        )
    }
}