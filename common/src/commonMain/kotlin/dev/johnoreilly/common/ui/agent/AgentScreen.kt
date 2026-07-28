package dev.johnoreilly.common.ui.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ElevatedCard
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.ui.fixtures.ClubInFixtureView
import dev.johnoreilly.common.ui.players.PlayerView
import dev.johnoreilly.common.viewmodel.AgentViewModel
import dev.johnoreilly.common.viewmodel.Message
import org.koin.compose.viewmodel.koinViewModel

private val suggestions = listOf(
    "Who are the top scoring players?",
    "What are the next fixtures?",
    "Best value midfielders?",
)

@Composable
fun AgentScreen(onPlayerSelected: (playerId: Int) -> Unit = {}) {
    val viewModel = koinViewModel<AgentViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    AgentScreenContent(
        messages = uiState.messages,
        inputText = uiState.inputText,
        isInputEnabled = uiState.isInputEnabled,
        isLoading = uiState.isLoading,
        isChatEnded = uiState.isChatEnded,
        onInputTextChanged = viewModel::updateInputText,
        onSendClicked = viewModel::sendMessage,
        onRestartClicked = viewModel::restartChat,
        onSuggestionClicked = {
            viewModel.updateInputText(it)
            viewModel.sendMessage()
        },
        onPlayerSelected = onPlayerSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentScreenContent(
    messages: List<Message>,
    inputText: String,
    isInputEnabled: Boolean,
    isLoading: Boolean,
    isChatEnded: Boolean,
    onInputTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    onSuggestionClicked: (String) -> Unit,
    onPlayerSelected: (playerId: Int) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FPL Assistant") },
                actions = {
                    IconButton(onClick = onRestartClicked) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Restart chat")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    when (message) {
                        is Message.UserMessage -> UserMessageBubble(message.text)
                        is Message.AgentMessage -> AgentMessageBubble(message.text, message.players, message.fixtures, onPlayerSelected)
                        is Message.SystemMessage -> SystemMessageItem(message.text)
                        is Message.ErrorMessage -> LabelledBubble("Error", message.text, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
                        is Message.ToolCallMessage -> LabelledBubble("Tool call", message.text, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                        is Message.ResultMessage -> LabelledBubble("Result", message.text, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                if (isLoading) item { AgentMessageBubble("…") }
                item { Spacer(Modifier.height(8.dp)) }
            }

            // Suggestion chips (only before the chat has started)
            if (messages.count { it is Message.UserMessage } == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { s ->
                        AssistChip(onClick = { onSuggestionClicked(s) }, label = { Text(s) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            InputArea(
                text = inputText,
                onTextChanged = onInputTextChanged,
                onSendClicked = onSendClicked,
                isEnabled = isInputEnabled && !isChatEnded,
                isLoading = isLoading,
            )
        }
    }
}

@Composable
private fun UserMessageBubble(text: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp)
        ) {
            Text(text, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.width(8.dp))
        Avatar(isUser = true)
    }
}

@Composable
private fun AgentMessageBubble(
    text: String,
    players: List<Player> = emptyList(),
    fixtures: List<GameFixture> = emptyList(),
    onPlayerSelected: (playerId: Int) -> Unit = {},
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Avatar(isUser = false)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.widthIn(max = 320.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp)
            ) {
                Markdown(
                    content = text,
                    colors = markdownColor(text = MaterialTheme.colorScheme.onPrimaryContainer),
                    typography = markdownTypography(text = MaterialTheme.typography.bodyLarge)
                )
            }
            // Rich cards for any players/fixtures the answer referenced
            if (players.isNotEmpty() || fixtures.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                ElevatedCard {
                    Column {
                        players.forEach { player ->
                            PlayerView(player = player, onPlayerSelected = { onPlayerSelected(it.id) }, isDataLoading = false)
                        }
                        fixtures.forEach { fixture -> FixtureCard(fixture) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FixtureCard(fixture: GameFixture) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ClubInFixtureView(teamName = fixture.homeTeam, teamPhotoUrl = fixture.homeTeamPhotoUrl)
        }
        val middle = if (fixture.homeTeamScore != null && fixture.awayTeamScore != null) {
            "${fixture.homeTeamScore} - ${fixture.awayTeamScore}"
        } else {
            fixture.localKickoffTime?.date?.toString() ?: "v"
        }
        Text(
            text = middle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ClubInFixtureView(teamName = fixture.awayTeam, teamPhotoUrl = fixture.awayTeamPhotoUrl)
        }
    }
}

@Composable
private fun Avatar(isUser: Boolean) {
    val bg = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val fg = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    Box(Modifier.size(32.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        Text(if (isUser) "U" else "A", color = fg, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SystemMessageItem(text: String) {
    Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LabelledBubble(
    label: String,
    text: String,
    labelColor: androidx.compose.ui.graphics.Color,
    container: androidx.compose.ui.graphics.Color,
    onContainer: androidx.compose.ui.graphics.Color,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Column(Modifier.widthIn(max = 300.dp)) {
            Text(label, color = labelColor, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 8.dp))
            Box(Modifier.clip(RoundedCornerShape(16.dp)).background(container).padding(12.dp)) {
                Text(text, color = onContainer, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun InputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean,
) {
    Surface(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about players or fixtures…") },
                enabled = isEnabled,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClicked() }),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(40.dp).padding(8.dp))
            } else {
                IconButton(
                    onClick = onSendClicked,
                    enabled = isEnabled && text.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isEnabled && text.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (isEnabled && text.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
