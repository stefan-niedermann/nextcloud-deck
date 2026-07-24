package it.niedermann.nextcloud.deck.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loginSuccess.collect {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Connect to Nextcloud",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = viewModel.url,
            onValueChange = { viewModel.url = it },
            label = { Text("Server URL") },
            placeholder = { Text("https://nextcloud.example.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            singleLine = true,
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !viewModel.isLoading
        )
        if (viewModel.error != null) {
            Text(
                text = viewModel.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (viewModel.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            val status = viewModel.syncStatus
            if (status != null && status.boardsTotalCount() > 0) {
                val progress = status.boardsFinishedCount().toFloat() / status.boardsTotalCount()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Importing boards: ${status.boardsFinishedCount()} / ${status.boardsTotalCount()}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }
    }
}
