package csmc.hospital.notifier.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import csmc.hospital.notifier.ui.theme.*
import csmc.hospital.notifier.ui.viewmodel.ReferralViewModel
import csmc.hospital.notifier.data.model.Referral
import csmc.hospital.notifier.data.model.Priority
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalUserReferralsScreen(
    onViewDetails: (String) -> Unit = {},
    viewModel: ReferralViewModel = viewModel()
) {
    val referrals by viewModel.referrals.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val isDepartmentsLoading by viewModel.isDepartmentsLoading.collectAsState()
    val isForwarding by viewModel.isForwarding.collectAsState()
    val isTagDoneLoading by viewModel.isTagDoneLoading.collectAsState()
    val forwardSuccess by viewModel.forwardSuccess.collectAsState()
    val tagDoneSuccess by viewModel.tagDoneSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val filters = listOf("All Items (12)", "High Priority", "Routine", "Consults")
    var selectedFilter by remember { mutableStateOf("All Items (12)") }
    var selectedReferral by remember { mutableStateOf<Referral?>(null) }

    selectedReferral?.let { referral ->
        ForwardReferralDialog(
            departments = departments,
            isDepartmentsLoading = isDepartmentsLoading,
            isForwarding = isForwarding,
            onDismiss = { selectedReferral = null },
            onConfirm = { dept, remarks ->
                viewModel.forwardReferral(referral, dept, remarks)
                selectedReferral = null
            }
        )
    }

    if (isForwarding) LoadingOverlay("Forwarding referral...")
    if (isTagDoneLoading) LoadingOverlay("Processing...")

    if (forwardSuccess) {
        TransactionResultDialog(
            isSuccess = true,
            title = "Referral Forwarded",
            message = "The referral has been successfully forwarded and removed from the queue.",
            onDismiss = { viewModel.clearForwardSuccess() }
        )
    }

    if (tagDoneSuccess) {
        TransactionResultDialog(
            isSuccess = true,
            title = "Marked as Done",
            message = "The referral has been successfully tagged as done.",
            onDismiss = { viewModel.clearTagDoneSuccess() }
        )
    }

    error?.let { errorMsg ->
        TransactionResultDialog(
            isSuccess = false,
            title = "Error",
            message = errorMsg,
            onDismiss = { viewModel.clearError() }
        )
    }

    LaunchedEffect(selectedReferral) {
        if (selectedReferral != null) viewModel.loadDepartments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("JD", style = MaterialTheme.typography.labelLarge, color = OnPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Notifier",
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp, color = Primary)
                            )
                            Text(
                                "Cardiology Dept.",
                                style = MaterialTheme.typography.labelMedium.copy(color = OnSurfaceVariant)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = Primary,
                contentColor = OnPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Referral", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Active Referrals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface
                )
                Text(
                    "Manage pending cases for Cardiology.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { filter ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = OnPrimary,
                                containerColor = SurfaceContainerHigh,
                                labelColor = OnSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(referrals) { referral ->
                NormalReferralCard(
                    referral = referral,
                    onViewDetails = { onViewDetails(referral.id) },
                    onTagAsDone = { viewModel.tagAsDone(referral.id) },
                    onForward = { selectedReferral = referral }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun NormalReferralCard(
    referral: Referral,
    onViewDetails: () -> Unit = {},
    onTagAsDone: () -> Unit = {},
    onForward: () -> Unit = {}
) {
    val priorityColor = when(referral.priority) {
        Priority.STAT -> Error
        Priority.URGENT -> Error
        Priority.HIGH -> Secondary
        else -> OutlineVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Box {
            // Side Stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(priorityColor)
            )

            Column(modifier = Modifier.padding(16.dp).padding(start = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Surface(
                            color = priorityColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                referral.priority.name.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 10.sp),
                                color = if (priorityColor == Error) Error else if (priorityColor == Secondary) Secondary else Outline
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            referral.hospital,
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface
                        )
                        Text(
                            "Forwarded: ${referral.timeLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant
                        )
                    }
                    Surface(
                        color = PrimaryContainer.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            referral.patientCode,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp),
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Patient", style = MaterialTheme.typography.labelMedium, color = Outline)
                        Text("${referral.patientName}, ${referral.patientAge}${referral.patientGender.take(1)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Condition", style = MaterialTheme.typography.labelMedium, color = Outline)
                        Text(referral.department, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("View Details", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onTagAsDone,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                    ) {
                        Text("Tag as Done", style = MaterialTheme.typography.labelMedium, color = Primary)
                    }
                    OutlinedIconButton(
                        onClick = onForward,
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Forward, contentDescription = "Forward", tint = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
