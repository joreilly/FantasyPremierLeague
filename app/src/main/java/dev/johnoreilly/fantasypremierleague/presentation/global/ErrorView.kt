package dev.johnoreilly.fantasypremierleague.presentation.global

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Type of error to display appropriate icon and message
 */
enum class ErrorType {
    NETWORK,      // Network connectivity issues
    SERVER,       // Server/API errors
    UNKNOWN       // Generic/unknown errors
}

/**
 * A comprehensive error view component with retry functionality.
 *
 * @param message The error message to display
 * @param errorType The type of error (affects icon and default message)
 * @param onRetry Callback when retry button is clicked (null to hide retry button)
 * @param modifier Additional modifiers
 */
@Composable
fun ErrorView(
    message: String? = null,
    errorType: ErrorType = ErrorType.UNKNOWN,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val icon = when (errorType) {
        ErrorType.NETWORK -> Icons.Default.WifiOff
        ErrorType.SERVER, ErrorType.UNKNOWN -> Icons.Default.Error
    }

    val defaultMessage = when (errorType) {
        ErrorType.NETWORK -> "No internet connection.\nPlease check your network settings."
        ErrorType.SERVER -> "Unable to load data from server.\nPlease try again later."
        ErrorType.UNKNOWN -> "Something went wrong.\nPlease try again."
    }

    ErrorViewContent(
        icon = icon,
        message = message ?: defaultMessage,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Internal composable that renders the error UI
 */
@Composable
private fun ErrorViewContent(
    icon: ImageVector,
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon
        Icon(
            imageVector = icon,
            contentDescription = "Error",
            modifier = Modifier.size(IconSize.extraLarge * 2),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

        // Error message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // Retry button (if onRetry is provided)
        onRetry?.let {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))

            Button(
                onClick = it,
                modifier = Modifier
                    .defaultMinSize(minWidth = 120.dp)
                    .height(ButtonHeight.large)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.medium)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text("Retry")
            }
        }
    }
}

/**
 * A compact error message component for use in smaller spaces (e.g., cards, list items).
 *
 * @param message The error message
 * @param onRetry Optional retry callback
 * @param modifier Additional modifiers
 */
@Composable
fun CompactErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.mediumLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(IconSize.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        onRetry?.let {
            Spacer(modifier = Modifier.width(Spacing.small))
            IconButton(onClick = it) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry"
                )
            }
        }
    }
}
