package csmc.hospital.notifier.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import csmc.hospital.notifier.data.model.QueueItem
import csmc.hospital.notifier.ui.theme.*
import csmc.hospital.notifier.ui.viewmodel.ReferralViewModel
import csmc.hospital.notifier.util.toTitleCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    onNavigateToReferrals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: ReferralViewModel = viewModel()
) {
    val queueItems by viewModel.queueItems.collectAsState()
    val isQueueLoading by viewModel.isQueueLoading.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val isDepartmentsLoading by viewModel.isDepartmentsLoading.collectAsState()
    var selectedCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadQueue()
        viewModel.loadDepartments()
    }

    val filteredItems = remember(queueItems, selectedCode) {
        if (selectedCode == null) queueItems
        else queueItems.filter { it.department.equals(selectedCode, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Department Queue",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Primary)
                        )
                        if (!isQueueLoading) {
                            Text(
                                "${filteredItems.size} patient${if (filteredItems.size != 1) "s" else ""} in queue",
                                style = MaterialTheme.typography.labelMedium.copy(color = OnSurfaceVariant)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadQueue() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Primary)
                    }
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
                        selected = false,
                        onClick = onNavigateToReferrals,
                        icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                        label = { Text("Referrals") }
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.PlaylistAddCheck, contentDescription = null) },
                        label = { Text("Queue") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnSecondaryContainer,
                            selectedTextColor = OnSecondaryContainer,
                            indicatorColor = SecondaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
        ) {
            // Department filter chips from get_departments API
            if (isDepartmentsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    item {
                        val isSelected = selectedCode == null
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCode = null },
                            label = { Text("ALL", style = MaterialTheme.typography.labelMedium) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary, selectedLabelColor = OnPrimary,
                                containerColor = SurfaceContainer, labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = isSelected,
                                borderColor = OutlineVariant, selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                    items(departments) { dept ->
                        val isSelected = selectedCode == dept.code
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCode = dept.code },
                            label = { Text(dept.department.uppercase(), style = MaterialTheme.typography.labelMedium) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary, selectedLabelColor = OnPrimary,
                                containerColor = SurfaceContainer, labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = isSelected,
                                borderColor = OutlineVariant, selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            when {
                isQueueLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Loading queue...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                filteredItems.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PlaylistAddCheck,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = OutlineVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (selectedCode == null) "No patients in queue"
                                else "No patients in ${departments.find { it.code == selectedCode }?.department ?: selectedCode}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Queue a referral from the referral list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Outline
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(filteredItems) { index, item ->
                            QueueItemCard(queueNumber = index + 1, item = item)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun QueueItemCard(queueNumber: Int, item: QueueItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(
            width = if (queueNumber == 1) 1.5.dp else 1.dp,
            color = if (queueNumber == 1) Primary else OutlineVariant
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Queue number badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = when (queueNumber) {
                            1 -> Primary
                            2 -> PrimaryContainer
                            else -> SurfaceContainerHigh
                        },
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "#$queueNumber",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = when {
                                queueNumber < 10 -> 20.sp
                                queueNumber < 100 -> 16.sp
                                else -> 13.sp
                            },
                            color = when (queueNumber) {
                                1 -> OnPrimary
                                2 -> OnPrimaryContainer
                                else -> OnSurfaceVariant
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Patient name + age/gender
                Row(verticalAlignment = Alignment.CenterVertically) {
                    csmc.hospital.notifier.util.AvatarUtils.PatientAvatar(
                        age = item.age,
                        gender = item.gender,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            item.patient.uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${item.age}Y · ${item.gender}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Department badge
                Surface(
                    color = SecondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        item.department,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSecondaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Referring hospital
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Festival,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        item.referring_hospital,
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Remarks
                if (item.remarks.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Notes,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            item.remarks,
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Date queued
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        item.created_at,
                        style = MaterialTheme.typography.labelSmall,
                        color = Outline
                    )
                }
            }
        }
    }
}
