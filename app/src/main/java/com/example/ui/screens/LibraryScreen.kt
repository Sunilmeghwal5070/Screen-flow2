package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RecordingEntity
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.LibraryFilter
import com.example.ui.viewmodel.LibrarySort
import com.example.util.DeviceCapabilities
import com.example.util.VideoMetadataHelper
import java.io.File

@Composable
fun LibraryScreen(
    recordings: List<RecordingEntity>,
    selectedFilter: LibraryFilter,
    selectedSort: LibrarySort,
    searchQuery: String,
    onFilterSelect: (LibraryFilter) -> Unit,
    onSortSelect: (LibrarySort) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPlayRecording: (RecordingEntity) -> Unit,
    onEditRecording: (RecordingEntity) -> Unit,
    onShareRecording: (RecordingEntity) -> Unit,
    onToggleFavorite: (RecordingEntity) -> Unit,
    onRenameRecording: (RecordingEntity, String) -> Unit,
    onDeleteRecording: (RecordingEntity) -> Unit,
    onStartRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    var showRenameDialogFor by remember { mutableStateOf<RecordingEntity?>(null) }
    var renameText by remember { mutableStateOf("") }
    var showDeleteConfirmFor by remember { mutableStateOf<RecordingEntity?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter list
    val filteredList = remember(recordings, selectedFilter, selectedSort, searchQuery) {
        var list = when (selectedFilter) {
            LibraryFilter.ALL -> recordings
            LibraryFilter.VIDEOS -> recordings.filter { !it.isScreenshot }
            LibraryFilter.SCREENSHOTS -> recordings.filter { it.isScreenshot }
            LibraryFilter.FAVORITES -> recordings.filter { it.isFavorite }
        }

        if (searchQuery.isNotBlank()) {
            list = list.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }

        when (selectedSort) {
            LibrarySort.NEWEST -> list.sortedByDescending { it.timestamp }
            LibrarySort.OLDEST -> list.sortedBy { it.timestamp }
            LibrarySort.LARGEST -> list.sortedByDescending { it.fileSizeBytes }
            LibrarySort.LONGEST -> list.sortedByDescending { it.durationMs }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("library_screen_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Media Library",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = StudioTextPrimary
                        )
                    )
                    Text(
                        text = "${filteredList.size} media items available",
                        style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                    )
                }

                // Sort Button
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent.container)
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort Items", tint = accent.primary)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest First") },
                            onClick = { onSortSelect(LibrarySort.NEWEST); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Oldest First") },
                            onClick = { onSortSelect(LibrarySort.OLDEST); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Largest File Size") },
                            onClick = { onSortSelect(LibrarySort.LARGEST); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Longest Duration") },
                            onClick = { onSortSelect(LibrarySort.LONGEST); showSortMenu = false }
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search recordings by name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StudioTextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent.primary,
                    unfocusedBorderColor = StudioCardBorder,
                    focusedContainerColor = StudioSurfaceLight,
                    unfocusedContainerColor = StudioSurfaceLight
                ),
                singleLine = true
            )
        }

        // Filter Pills Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(LibraryFilter.values()) { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) accent.primary else accent.container)
                            .clickable { onFilterSelect(filter) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("filter_pill_${filter.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (filter) {
                                LibraryFilter.ALL -> "All Media"
                                LibraryFilter.VIDEOS -> "Videos"
                                LibraryFilter.SCREENSHOTS -> "Screenshots"
                                LibraryFilter.FAVORITES -> "Favorites"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else accent.primary
                            )
                        )
                    }
                }
            }
        }

        // Empty state
        if (filteredList.isEmpty()) {
            item {
                EmptyLibraryView(filter = selectedFilter, onStart = onStartRecording)
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                LibraryItemCard(
                    recording = item,
                    onPlay = { onPlayRecording(item) },
                    onEdit = { onEditRecording(item) },
                    onShare = { onShareRecording(item) },
                    onToggleFavorite = { onToggleFavorite(item) },
                    onRename = {
                        showRenameDialogFor = item
                        renameText = item.title
                    },
                    onDelete = { showDeleteConfirmFor = item }
                )
            }
        }
    }

    // Rename Dialog
    if (showRenameDialogFor != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialogFor = null },
            title = { Text("Rename Recording", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent.primary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRenameDialogFor?.let { onRenameRecording(it, renameText) }
                        showRenameDialogFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialogFor = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmFor != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmFor = null },
            title = { Text("Delete Recording?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete \"${showDeleteConfirmFor?.title}\" from your device storage.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmFor?.let { onDeleteRecording(it) }
                        showDeleteConfirmFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRecordRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmFor = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LibraryItemCard(
    recording: RecordingEntity,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = LocalAccentTheme.current
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onPlay() }
            .testTag("library_item_card"),
        shape = RoundedCornerShape(20.dp),
        color = StudioSurfaceLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail Box
                Box(
                    modifier = Modifier
                        .size(width = 86.dp, height = 86.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    if (recording.thumbnailPath != null && File(recording.thumbnailPath).exists()) {
                        AsyncImage(
                            model = recording.thumbnailPath,
                            contentDescription = "Thumbnail",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = if (recording.isScreenshot) Icons.Default.CameraAlt else Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    if (recording.durationMs > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = DeviceCapabilities.formatDuration(recording.durationMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Metadata Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recording.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (recording.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (recording.isFavorite) StudioRecordRed else StudioTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${recording.resolution} · ${DeviceCapabilities.formatBytes(recording.fileSizeBytes)} · ${recording.fps}fps",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )
                    )

                    Text(
                        text = VideoMetadataHelper.formatDate(recording.timestamp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StudioTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Play Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.container)
                            .clickable { onPlay() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = accent.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play", style = MaterialTheme.typography.labelSmall.copy(color = accent.primary, fontWeight = FontWeight.Bold))
                        }
                    }

                    // Edit Button (if video)
                    if (!recording.isScreenshot) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { onEdit() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = StudioTextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary, fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    // Share Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { onShare() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = StudioTextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // More Menu (Rename, Delete)
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = StudioTextSecondary)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) },
                            onClick = { onRename(); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = StudioRecordRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StudioRecordRed) },
                            onClick = { onDelete(); showMenu = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLibraryView(filter: LibraryFilter, onStart: () -> Unit) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accent.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (filter) {
                        LibraryFilter.SCREENSHOTS -> Icons.Default.CameraAlt
                        LibraryFilter.FAVORITES -> Icons.Default.Favorite
                        else -> Icons.Default.Movie
                    },
                    contentDescription = null,
                    tint = accent.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = when (filter) {
                    LibraryFilter.SCREENSHOTS -> "No screenshots yet"
                    LibraryFilter.FAVORITES -> "No favorites marked"
                    else -> "No recordings in library"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
            )

            Text(
                text = "Screen recordings and studio captures saved to your device will be organized here.",
                style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStart,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
            ) {
                Text("Start Recording Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}
