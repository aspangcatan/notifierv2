package csmc.hospital.notifier.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import csmc.hospital.notifier.data.model.Department
import csmc.hospital.notifier.data.model.Referral
import csmc.hospital.notifier.data.session.UserSession
import csmc.hospital.notifier.ui.theme.OnPrimaryContainer
import csmc.hospital.notifier.ui.theme.OnSecondaryContainer
import csmc.hospital.notifier.ui.theme.OnSurface
import csmc.hospital.notifier.ui.theme.OnSurfaceVariant
import csmc.hospital.notifier.ui.theme.Outline
import csmc.hospital.notifier.ui.theme.OutlineVariant
import csmc.hospital.notifier.ui.theme.Primary
import csmc.hospital.notifier.ui.theme.PrimaryContainer
import csmc.hospital.notifier.ui.theme.Secondary
import csmc.hospital.notifier.ui.theme.SecondaryContainer
import csmc.hospital.notifier.ui.theme.Surface
import csmc.hospital.notifier.ui.theme.SurfaceContainer
import csmc.hospital.notifier.ui.theme.SurfaceContainerLow
import csmc.hospital.notifier.ui.theme.SurfaceContainerLowest
import csmc.hospital.notifier.ui.viewmodel.ReferralViewModel
import csmc.hospital.notifier.util.AvatarUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriageReferralListScreen(
    onViewDetails: (id: String, viewOnly: Boolean, showHistory: Boolean) -> Unit = { _, _, _ -> },
    onNavigateToQueue: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: ReferralViewModel = viewModel()
) {
    val referrals by viewModel.referrals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val deptReferrals by viewModel.deptReferrals.collectAsState()
    val isDeptLoading by viewModel.isDeptLoading.collectAsState()
    val allDeptReferrals by viewModel.allDeptReferrals.collectAsState()
    val isAllDeptLoading by viewModel.isAllDeptLoading.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val isDepartmentsLoading by viewModel.isDepartmentsLoading.collectAsState()
    val isQueuing by viewModel.isQueuing.collectAsState()
    val queueSuccess by viewModel.queueSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val myDept = UserSession.role.ifBlank { "My Department" }
    val isTriage = remember { UserSession.role.equals("EMED", ignoreCase = true) }
    val tabs = if (isTriage) listOf("Referral", myDept, "All")
               else listOf(myDept)
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Non-triage: load dept referrals immediately on entry
    LaunchedEffect(Unit) {
        if (!isTriage) viewModel.loadDeptReferrals()
    }

    // Triage: load API data when switching to tab 1 or 2
    LaunchedEffect(selectedTabIndex) {
        if (isTriage) {
            when (selectedTabIndex) {
                1 -> viewModel.loadDeptReferrals()
                2 -> viewModel.loadAllDeptReferrals()
            }
        }
    }

    val activeReferrals = if (isTriage) {
        when (selectedTabIndex) {
            1 -> deptReferrals
            2 -> allDeptReferrals
            else -> referrals
        }
    } else {
        deptReferrals
    }

    val activeIsLoading = if (isTriage) {
        when (selectedTabIndex) {
            1 -> isDeptLoading
            2 -> isAllDeptLoading
            else -> isLoading
        }
    } else {
        isDeptLoading
    }

    val filteredReferrals = remember(activeReferrals, searchQuery) {
        if (searchQuery.isBlank()) activeReferrals
        else activeReferrals.filter {
            it.patientName.contains(searchQuery, ignoreCase = true) ||
            it.patientCode.contains(searchQuery, ignoreCase = true)
        }
    }
    var queuingReferralId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(queuingReferralId) {
        if (queuingReferralId != null) viewModel.loadDepartments()
    }

    queuingReferralId?.let { referralId ->
        QueueDepartmentDialog(
            departments = departments,
            isDepartmentsLoading = isDepartmentsLoading,
            isQueuing = isQueuing,
            onDismiss = { queuingReferralId = null },
            onConfirm = { selectedDepartment ->
                viewModel.queueReferral(referralId, selectedDepartment)
                queuingReferralId = null
            }
        )
    }

    if (isQueuing) LoadingOverlay("Adding to queue...")

    if (queueSuccess) {
        TransactionResultDialog(
            isSuccess = true,
            title = "Patient Queued",
            message = "The patient has been successfully added to the department queue.",
            onDismiss = { viewModel.clearQueueSuccess() }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = OnPrimaryContainer,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Notifier",
                                style = MaterialTheme.typography.headlineSmall.copy(color = Primary)
                            )
                            Text(
                                UserSession.name.ifBlank { "Triage" },
                                style = MaterialTheme.typography.labelMedium.copy(color = OnSurfaceVariant)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = "Settings", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
                NavigationBar(containerColor = SurfaceContainer) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                        label = { Text("Referrals") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnSecondaryContainer,
                            selectedTextColor = OnSecondaryContainer,
                            indicatorColor = SecondaryContainer
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToQueue,
                        icon = { Icon(Icons.Default.PlaylistAddCheck, contentDescription = null) },
                        label = { Text("Queue") }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Surface)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Patient or Code...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Outline) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Outline)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Surface,
                    contentColor = Primary,
                    divider = { HorizontalDivider(color = OutlineVariant) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            selectedContentColor = Primary,
                            unselectedContentColor = OnSurfaceVariant,
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        tabs[selectedTabIndex],
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnSurface
                    )
                    Surface(
                        color = SecondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "${filteredReferrals.size} NEW",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (activeIsLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (filteredReferrals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = OutlineVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No referrals found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(filteredReferrals) { referral ->
                ReferralCard(
                    referral = referral,
                    onViewDetails = {
                        val viewOnly = isTriage && selectedTabIndex == 2
                        val showHistory = !isTriage || selectedTabIndex != 0
                        onViewDetails(referral.id, viewOnly, showHistory)
                    },
                    onQueue = { queuingReferralId = referral.id },
                    showQueueButton = isTriage
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReferralCard(
    referral: Referral,
    onViewDetails: () -> Unit = {},
    onQueue: () -> Unit = {},
    showQueueButton: Boolean = true
) {
    val deptColor = when {
        referral.department.contains("ER", ignoreCase = true) ||
        referral.department.contains("Emergency", ignoreCase = true) -> Color(0xFFD32F2F)
        referral.department.contains("ED", ignoreCase = true) -> Color(0xFFE65100)
        referral.department.contains("ICU", ignoreCase = true) ||
        referral.department.contains("CCU", ignoreCase = true) -> Color(0xFFC62828)
        referral.department.contains("Surgery", ignoreCase = true) -> Color(0xFF6A1B9A)
        referral.department.contains("Cardio", ignoreCase = true) -> Color(0xFF1565C0)
        else -> Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, OutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(deptColor)
            )

            Column(modifier = Modifier.weight(1f)) {
                // Hospital header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.LocalHospital,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        referral.hospital.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = OnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFF00897B),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "NEW",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)

                // Patient info row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarUtils.PatientAvatar(
                            age = referral.patientAge,
                            gender = referral.patientGender,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            referral.patientName.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${referral.patientAge}Y  •  ${referral.patientGender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = deptColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, deptColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(deptColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        referral.department,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = deptColor
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                referral.timeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

                // Action buttons
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Primary)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "View Details",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary
                        )
                    }
                    if (showQueueButton) {
                        Button(
                            onClick = onQueue,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
                        ) {
                            Icon(
                                Icons.Default.PlaylistAdd,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Queue",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueDepartmentDialog(
    departments: List<Department>,
    isDepartmentsLoading: Boolean,
    isQueuing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Department) -> Unit
) {
    var selectedDepartment by remember { mutableStateOf<Department?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(departments) {
        if (selectedDepartment == null && departments.isNotEmpty()) {
            selectedDepartment = departments.first()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SecondaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlaylistAdd,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Queue Patient",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurface
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Select the department to add this patient to the queue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Department",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                if (isDepartmentsLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDepartment?.department ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Secondary,
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedContainerColor = SurfaceContainerLow
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = SurfaceContainerLowest
                        ) {
                            departments.forEach { dept ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            dept.department,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (dept == selectedDepartment) Secondary else OnSurface
                                        )
                                    },
                                    onClick = {
                                        selectedDepartment = dept
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedDepartment?.let { onConfirm(it) } },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                enabled = selectedDepartment != null && !isQueuing && !isDepartmentsLoading
            ) {
                if (isQueuing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add to Queue")
                }
            }
        }
    )
}
