package dev.johnoreilly.common.ui.global

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.seiko.imageloader.rememberImagePainter

/**
 * The Premier League has no headshot for a sizeable share of players - overwhelmingly recent
 * signings and academy players - and its CDN answers those URLs with a 403 rather than serving
 * anything. Left alone that renders as an empty gap in the row, so fall back to the placeholder
 * silhouette the official site uses for the same players.
 *
 * Deliberately the 110x140 asset even where it's drawn larger: it shares the portrait aspect ratio
 * of the real photos, so a row mixing the two keeps a consistent shape.
 */
private const val MISSING_PLAYER_PHOTO_URL =
    "https://resources.premierleague.com/premierleague/photos/players/110x140/Photo-Missing.png"

@Composable
fun rememberPlayerPhotoPainter(photoUrl: String): Painter =
    rememberImagePainter(
        photoUrl,
        errorPainter = { rememberImagePainter(MISSING_PLAYER_PHOTO_URL) },
    )
