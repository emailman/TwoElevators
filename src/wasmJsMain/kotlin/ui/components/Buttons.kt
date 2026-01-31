package ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FloorButton(
    floor: Int,
    isLit: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp = 52.dp
) {
    val buttonColor = if (isLit) {
        Color(0xFFFFB300)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isLit) {
        Color.Black
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val fontSize = (buttonSize.value * 0.38f).sp

    Button(
        onClick = onClick,
        modifier = Modifier.requiredSize(buttonSize),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isLit) 8.dp else 2.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = floor.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun CallButton(
    isUp: Boolean,
    isLit: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp = 30.dp
) {
    val buttonColor = if (isLit) {
        Color(0xFFFFB300)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val arrowColor = if (isLit) {
        Color.Black
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val arrowSize = (buttonSize.value * 0.47f).dp

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isLit) 8.dp else 2.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Canvas(modifier = Modifier.size(arrowSize)) {
            val arrowPath = Path().apply {
                if (isUp) {
                    moveTo(size.width / 2, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                } else {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
            }
            drawPath(arrowPath, color = arrowColor)
        }
    }
}
