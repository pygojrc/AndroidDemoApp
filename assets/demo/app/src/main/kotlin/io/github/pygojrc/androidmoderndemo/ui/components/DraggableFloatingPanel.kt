package io.github.pygojrc.androidmoderndemo.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.pygojrc.androidmoderndemo.R
import kotlin.math.roundToInt

@Composable
fun DraggableFloatingPanelHost(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        var offsetX by rememberSaveable { mutableFloatStateOf(24f) }
        var offsetY by rememberSaveable { mutableFloatStateOf(24f) }
        var panelSize by remember { mutableStateOf(IntSize.Zero) }

        val maxX = (constraints.maxWidth - panelSize.width).coerceAtLeast(0).toFloat()
        val maxY = (constraints.maxHeight - panelSize.height).coerceAtLeast(0).toFloat()

        LaunchedEffect(maxX, maxY) {
            offsetX = offsetX.coerceIn(0f, maxX)
            offsetY = offsetY.coerceIn(0f, maxY)
        }

        if (visible) {
            ElevatedCard(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .onSizeChanged { panelSize = it }
                    .then(
                        Modifier.pointerInput(maxX, maxY) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val next = Offset(offsetX, offsetY) + dragAmount
                                offsetX = next.x.coerceIn(0f, maxX)
                                offsetY = next.y.coerceIn(0f, maxY)
                            }
                        },
                    )
                    .align(Alignment.TopStart)
                    .then(
                        Modifier.offset {
                            IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                        },
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.floating_panel_title),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        IconButton(onClick = onClose) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.action_close),
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.floating_panel_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
