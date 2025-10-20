package dev.johnoreilly.fantasypremierleague.presentation.global

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates an animated shimmer brush for loading states.
 * The shimmer effect moves from left to right, creating a smooth loading animation.
 */
@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_animation"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation, translateAnimation),
            end = Offset(translateAnimation + 200f, translateAnimation + 200f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

/**
 * A generic shimmer placeholder component.
 *
 * @param modifier The modifier to be applied to the placeholder
 * @param shape The shape of the placeholder (default is small rounded corners)
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CornerRadius.small)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush())
    )
}

/**
 * A circular shimmer placeholder, typically used for profile images or icons.
 *
 * @param size The diameter of the circle
 * @param modifier Additional modifiers
 */
@Composable
fun ShimmerCircle(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

/**
 * A shimmer placeholder for text lines.
 *
 * @param width The width of the text line
 * @param height The height of the text line (default is 16.dp for typical body text)
 * @param modifier Additional modifiers
 */
@Composable
fun ShimmerText(
    width: Dp,
    height: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        modifier = modifier
            .width(width)
            .height(height),
        shape = RoundedCornerShape(CornerRadius.small)
    )
}

/**
 * A shimmer loading placeholder for a player list item.
 * Matches the structure of the actual player list item.
 */
@Composable
fun PlayerListItemShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.mediumLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player image placeholder
        ShimmerCircle(size = ImageSize.medium)

        Spacer(modifier = Modifier.width(Spacing.mediumLarge))

        // Player info placeholder
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ShimmerText(width = 120.dp, height = 18.dp)
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            ShimmerText(width = 80.dp, height = 14.dp)
        }

        // Points placeholder
        ShimmerText(width = 40.dp, height = 24.dp)
    }
}

/**
 * A shimmer loading placeholder for a fixture list item.
 */
@Composable
fun FixtureListItemShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.mediumLarge),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home team
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShimmerCircle(size = ImageSize.medium)
            Spacer(modifier = Modifier.height(Spacing.small))
            ShimmerText(width = 60.dp, height = 14.dp)
        }

        // Score
        ShimmerText(width = 60.dp, height = 28.dp)

        // Away team
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShimmerCircle(size = ImageSize.medium)
            Spacer(modifier = Modifier.height(Spacing.small))
            ShimmerText(width = 60.dp, height = 14.dp)
        }
    }
}

/**
 * A shimmer loading placeholder for a league entry.
 */
@Composable
fun LeagueEntryShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.mediumLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank placeholder
        ShimmerText(width = 30.dp, height = 20.dp)

        Spacer(modifier = Modifier.width(Spacing.mediumLarge))

        // Entry info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ShimmerText(width = 140.dp, height = 18.dp)
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            ShimmerText(width = 100.dp, height = 14.dp)
        }

        // Points placeholder
        ShimmerText(width = 50.dp, height = 20.dp)
    }
}
