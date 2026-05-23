package csmc.hospital.notifier.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import csmc.hospital.notifier.data.model.ActivityLog
import csmc.hospital.notifier.ui.theme.*
import csmc.hospital.notifier.ui.viewmodel.ReferralViewModel
import csmc.hospital.notifier.util.AvatarUtils
import csmc.hospital.notifier.util.toDisplayDate
import csmc.hospital.notifier.util.toTitleCase
import androidx.lifecycle.viewmodel.compose.viewModel

private val logColors = listOf(
    Color(0xFF1565C0),
    Color(0xFF2E7D32),
    Color(0xFF6A1B9A),
    Color(0xFFE65100),
    Color(0xFF00838F),
    Color(0xFFC62828),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralDetailsScreen(
    referralId: String,
    viewOnly: Boolean = false,
    showHistory: Boolean = false,
    onBack: () -> Unit = {},
    viewModel: ReferralViewModel = viewModel()
) {
    val referrals by viewModel.referrals.collectAsState()
    val deptReferrals by viewModel.deptReferrals.collectAsState()
    val allDeptReferrals by viewModel.allDeptReferrals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val isLogsLoading by viewModel.isLogsLoading.collectAsState()
    val logsError by viewModel.logsError.collectAsState()

    LaunchedEffect(referralId) {
        if (showHistory) viewModel.loadActivityLogs(referralId)
    }

    val referral = referrals.find { it.id == referralId }
        ?: deptReferrals.find { it.id == referralId }
        ?: allDeptReferrals.find { it.id == referralId }

    val departments by viewModel.departments.collectAsState()
    val isDepartmentsLoading by viewModel.isDepartmentsLoading.collectAsState()
    val isForwarding by viewModel.isForwarding.collectAsState()
    val isTagDoneLoading by viewModel.isTagDoneLoading.collectAsState()
    val forwardSuccess by viewModel.forwardSuccess.collectAsState()
    val tagDoneSuccess by viewModel.tagDoneSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    if (isForwarding) LoadingOverlay("Forwarding referral...")
    if (isTagDoneLoading) LoadingOverlay("Processing...")

    var showForwardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showForwardDialog) {
        if (showForwardDialog) viewModel.loadDepartments()
    }

    if (forwardSuccess) {
        TransactionResultDialog(
            isSuccess = true,
            title = "Referral Forwarded",
            message = "The referral has been successfully forwarded and removed from the queue.",
            onDismiss = { viewModel.clearForwardSuccess(); onBack() }
        )
    }

    if (tagDoneSuccess) {
        TransactionResultDialog(
            isSuccess = true,
            title = "Marked as Done",
            message = "The referral has been successfully tagged as done.",
            onDismiss = { viewModel.clearTagDoneSuccess(); onBack() }
        )
    }

    error?.let {
        TransactionResultDialog(
            isSuccess = false,
            title = "Error",
            message = it,
            onDismiss = { viewModel.clearError() }
        )
    }

    if (isLoading && referral == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    if (referral == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Referral not found")
        }
        return
    }

    if (showForwardDialog) {
        ForwardReferralDialog(
            departments = departments,
            isDepartmentsLoading = isDepartmentsLoading,
            isForwarding = isForwarding,
            onDismiss = { showForwardDialog = false },
            onConfirm = { selectedDept, remarks ->
                viewModel.forwardReferral(referral, selectedDept, remarks)
                showForwardDialog = false
            }
        )
    }

    val deptColor = when {
        referral.department.contains("ER", ignoreCase = true) ||
        referral.department.contains("Emergency", ignoreCase = true) -> Color(0xFFD32F2F)
        referral.department.contains("ED", ignoreCase = true) -> Color(0xFFE65100)
        referral.department.contains("ICU", ignoreCase = true) ||
        referral.department.contains("CCU", ignoreCase = true) -> Color(0xFFC62828)
        else -> Primary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Referral Detail",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Primary)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
        },
        bottomBar = {
            if (!viewOnly) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showForwardDialog = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Primary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp), tint = Primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Forward", style = MaterialTheme.typography.labelLarge, color = Primary)
                        }
                        Button(
                            onClick = { viewModel.tagAsDone(referralId) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Done", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF4F6FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Patient Profile Header ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarUtils.PatientAvatar(
                            age = referral.patientAge,
                            gender = referral.patientGender,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "PATIENT PROFILE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            referral.patientName.uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${referral.patientGender}  •  ${referral.patientAge} Years",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                            Surface(
                                color = deptColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, deptColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    referral.department,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = deptColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ── Info Cards ─────────────────────────────────────────────────
            DetailInfoCard(
                icon = Icons.Default.CalendarMonth,
                label = "Referred Date",
                value = referral.dateReferred,
                trailingIcon = Icons.Default.EditCalendar
            )
            DetailInfoCard(
                icon = Icons.Default.LocalHospital,
                label = "Referring Hospital",
                value = referral.hospital.uppercase()
            )
            DetailInfoCard(
                icon = Icons.Default.MedicalServices,
                label = "DIAGNOSIS",
                value = referral.diagnosis
            )
            DetailInfoCard(
                icon = Icons.Default.Description,
                label = "CHIEF COMPLAINT",
                value = referral.chiefComplaint
            )

            // ── History Logs ───────────────────────────────────────────────
            if (showHistory) {
                // Section header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "HISTORY LOGS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.sp,
                            color = OnSurfaceVariant
                        )
                    )
                }

                when {
                    isLogsLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Primary, strokeWidth = 2.dp)
                        }
                    }
                    logsError != null -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ErrorContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, null, tint = Error, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(logsError!!, style = MaterialTheme.typography.bodySmall, color = Error)
                            }
                        }
                    }
                    activityLogs.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.History, null, modifier = Modifier.size(40.dp), tint = OutlineVariant)
                                Spacer(Modifier.height(8.dp))
                                Text("No history available", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            }
                        }
                    }
                    else -> {
                        activityLogs.forEachIndexed { index, log ->
                            TimelineLogItem(
                                log = log,
                                index = index + 1,
                                isLast = index == activityLogs.lastIndex,
                                color = logColors[index % logColors.size]
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    trailingIcon: ImageVector? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    value.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyLarge.copy(color = OnSurface),
                    lineHeight = 22.sp
                )
            }
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(trailingIcon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineLogItem(
    log: ActivityLog,
    index: Int,
    isLast: Boolean,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Left: numbered circle + connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$index",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 2.dp)
                        .background(color.copy(alpha = 0.25f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right: log card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Origin → Destination
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        log.origin.ifBlank { "—" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = color
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        log.destination.ifBlank { "—" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = color,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFECEFF1), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Detail rows
                LogRow(label = "Forwarded By", value = log.forwarded_by_name?.ifBlank { "System" } ?: "System")
                if (!log.tagged_by_name.isNullOrBlank()) {
                    LogRow(label = "Tagged By", value = log.tagged_by_name!!)
                }
                if (!log.remarks.isNullOrBlank()) {
                    LogRow(label = "Remarks", value = log.remarks!!)
                }
                LogRow(label = "Date Forwarded", value = log.date_forwarded?.toDisplayDate() ?: "—", valueColor = Outline)
                if (!log.date_tagged.isNullOrBlank()) {
                    LogRow(label = "Date Tagged", value = log.date_tagged!!.toDisplayDate(), valueColor = Outline)
                }
            }
        }
    }
}

@Composable
private fun LogRow(label: String, value: String, valueColor: Color = OnSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant),
            modifier = Modifier.width(110.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.labelSmall.copy(color = valueColor),
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardReferralDialog(
    departments: List<csmc.hospital.notifier.data.model.Department>,
    isDepartmentsLoading: Boolean,
    isForwarding: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (department: csmc.hospital.notifier.data.model.Department, remarks: String) -> Unit
) {
    var selectedDepartment by remember { mutableStateOf<csmc.hospital.notifier.data.model.Department?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var remarks by remember { mutableStateOf("N/A") }

    LaunchedEffect(departments) {
        if (selectedDepartment == null && departments.isNotEmpty()) {
            selectedDepartment = departments.first()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(PrimaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.Forward, null, tint = Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Forward Referral", style = MaterialTheme.typography.headlineSmall, color = OnSurface)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Send to Department", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (isDepartmentsLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Primary, strokeWidth = 2.dp)
                        }
                    } else {
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = selectedDepartment?.department ?: "",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = OutlineVariant, focusedBorderColor = Primary,
                                    unfocusedContainerColor = SurfaceContainerLow, focusedContainerColor = SurfaceContainerLow
                                )
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = Surface) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept.department, style = MaterialTheme.typography.bodyMedium, color = if (dept == selectedDepartment) Primary else OnSurface) },
                                        onClick = { selectedDepartment = dept; expanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
                Column {
                    Text("Remarks", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4, maxLines = 6,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = OutlineVariant, focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceContainerLow, focusedContainerColor = SurfaceContainerLow
                        )
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, OutlineVariant)) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedDepartment?.let { onConfirm(it, remarks) } },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = selectedDepartment != null && !isForwarding
            ) {
                if (isForwarding) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Proceed")
                }
            }
        }
    )
}
