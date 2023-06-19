package jp.morux2.composespotlightsample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Spotlight(
    targetRect: Rect,
    onTargetClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    var showSpotlight: Boolean by remember { mutableStateOf(false) }
    var startAnimation: Boolean by remember { mutableStateOf(false) }

    val animatedSize: Size by animateSizeAsState(
        targetValue = if (startAnimation) targetRect.size else Size.Zero,
        animationSpec = tween(5000),
        label = "spotlight"
    )
    val deltaX = animatedSize.width / 2
    val deltaY = animatedSize.height / 2
    val animatedRect = Rect(
        left = targetRect.center.x - deltaX,
        top = targetRect.center.y - deltaY,
        right = targetRect.center.x + deltaX,
        bottom = targetRect.center.y + deltaY
    )

    LaunchedEffect(targetRect) {
        showSpotlight = true
        startAnimation = true
    }

    AnimatedVisibility(
        visible = showSpotlight,
        enter = fadeIn(tween(5000)),
        exit = fadeOut(tween(5000))
    ) {
        Box {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        if (targetRect.contains(offset)) {
                            onTargetClicked()
                        } else {
                            showSpotlight = false
                            onDismiss()
                        }
                    })
                }) {
                val spotlightPath = Path().apply {
                    addRect(animatedRect)
                }
                clipPath(
                    path = spotlightPath,
                    clipOp = ClipOp.Difference
                ) {
                    drawRect(Color.Black.copy(alpha = 0.8f))
                }
            }
            GuideLabel(targetRect = targetRect)
        }
    }
}

@Composable
private fun GuideLabel(targetRect: Rect) {
    Text(
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(
                    x = (targetRect.topLeft.x + (targetRect.width - placeable.width) / 2).toInt(),
                    y = targetRect.topLeft.y.toInt() - (placeable.height + 16.dp.toPx().toInt())
                )
            }
        },
        text = "Click Here",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}