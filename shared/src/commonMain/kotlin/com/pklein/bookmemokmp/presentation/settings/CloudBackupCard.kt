package com.pklein.bookmemokmp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.settings_backup_now
import bookmemokmp.shared.generated.resources.settings_last_backup
import bookmemokmp.shared.generated.resources.settings_no_backup
import bookmemokmp.shared.generated.resources.settings_not_signed_in
import bookmemokmp.shared.generated.resources.settings_restore
import bookmemokmp.shared.generated.resources.settings_saving
import bookmemokmp.shared.generated.resources.settings_sign_in
import bookmemokmp.shared.generated.resources.settings_sign_out
import bookmemokmp.shared.generated.resources.settings_signed_in
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CloudBackupCard(
    email: String?,
    backupDate: String?,
    isBackupInProgress: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onBackupNow: () -> Unit,
    onRestore: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
            // ── Account row ───────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint =
                        if (email != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    modifier = Modifier.size(36.dp),
                )
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                ) {
                    if (email != null) {
                        Text(
                            text = stringResource(Res.string.settings_signed_in),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.settings_not_signed_in),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (email != null) {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_sign_out),
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Button(
                        onClick = onSignIn,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_sign_in),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            if (email != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // ── Last backup info ──────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text =
                            if (backupDate != null) {
                                stringResource(Res.string.settings_last_backup, backupDate)
                            } else {
                                stringResource(Res.string.settings_no_backup)
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Action buttons ────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = onBackupNow,
                        enabled = !isBackupInProgress,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text =
                                if (isBackupInProgress) {
                                    stringResource(Res.string.settings_saving)
                                } else {
                                    stringResource(Res.string.settings_backup_now)
                                },
                            textAlign = TextAlign.Center,
                        )
                    }
                    OutlinedButton(
                        onClick = onRestore,
                        enabled = backupDate != null && !isBackupInProgress,
                        modifier = Modifier.weight(1f),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_restore),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun PreviewCloudBackupNotSignedIn() {
    BookMemoTheme {
        CloudBackupCard(
            email = null,
            backupDate = null,
            isBackupInProgress = false,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewCloudBackupNotSignedInBigFont() {
    BookMemoTheme {
        CloudBackupCard(
            email = null,
            backupDate = null,
            isBackupInProgress = false,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

@Preview
@Composable
private fun PreviewCloudBackupSignedInNoBackup() {
    BookMemoTheme {
        CloudBackupCard(
            email = "test@gmail.com",
            backupDate = null,
            isBackupInProgress = false,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

@Preview
@Composable
private fun PreviewCloudBackupSignedInWithBackup() {
    BookMemoTheme {
        CloudBackupCard(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = false,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewCloudBackupSignedInWithBackupBigFont() {
    BookMemoTheme {
        CloudBackupCard(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = false,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

@Preview
@Composable
private fun PreviewCloudBackupInProgress() {
    BookMemoTheme {
        CloudBackupCard(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = true,
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}
