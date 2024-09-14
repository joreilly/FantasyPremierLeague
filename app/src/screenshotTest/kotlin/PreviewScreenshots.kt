import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.johnoreilly.common.ui.PlayerDetailsViewSharedPreview

class PreviewScreenshots {

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MaterialTheme {
            PlayerDetailsViewSharedPreview()
        }
    }
}
