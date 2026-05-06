package pl.warszawianin.ui.screens.reportdraft

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDraftScreen(
    reportId: Long,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zgłoszenie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Podgląd zgłoszenia #$reportId",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu pojawi się zdjęcie, kategoria, tytuł i opis wygenerowany przez AI.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { /* TODO: Refine with AI */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔄 Popraw")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubmitted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✉️ Wyślij")
            }
        }
    }
}
