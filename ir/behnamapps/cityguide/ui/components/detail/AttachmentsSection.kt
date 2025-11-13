package ir.behnamapps.cityguide.ui.components.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import ir.behnamapps.cityguide.data.model.UiImage
import ir.behnamapps.cityguide.data.remote.dto.AttachmentDto
import ir.behnamapps.cityguide.ui.components.common.ManagedAsyncImage
import ir.behnamapps.cityguide.ui.screens.detail.UiAttachment
import java.io.File

// --- Helper Enums and Functions ---

enum class AttachmentType(
    val title: String,
    val icon: ImageVector
) {
    DOCUMENT("مدارک", Icons.Outlined.Description),
    AWARD("افتخارات", Icons.Outlined.Star),
    PORTFOLIO("نمونه کارها", Icons.Outlined.Work)
}

private fun isImageFile(fileUrl: String): Boolean {
    return fileUrl.endsWith(".jpg", true) || fileUrl.endsWith(".jpeg", true) ||
            fileUrl.endsWith(".png", true) || fileUrl.endsWith(".webp", true) ||
            fileUrl.endsWith(".gif", true)
}

private fun getFileIcon(fileUrl: String): ImageVector {
    return when {
        fileUrl.endsWith(".pdf", true) -> Icons.Default.PictureAsPdf
        fileUrl.endsWith(".doc", true) || fileUrl.endsWith(".docx", true) -> Icons.Default.Description
        fileUrl.endsWith(".zip", true) || fileUrl.endsWith(".rar", true) -> Icons.Default.Archive
        else -> Icons.Default.InsertDriveFile
    }
}

private fun getFileExtension(fileUrl: String): String {
    val extension = fileUrl.substringAfterLast('.', "").uppercase()
    return if (extension.length > 4 || extension.isBlank()) "FILE" else extension
}

// --- Main Composables ---

@Composable
fun AttachmentsSection(
    uiAttachments: List<UiAttachment>,
    onAttachmentClick: (UiAttachment) -> Unit
) {
    val groupedAttachments = remember(uiAttachments) {
        uiAttachments.groupBy { uiAtt ->
            when (uiAtt.attachment.type.lowercase()) {
                "document" -> AttachmentType.DOCUMENT
                "award" -> AttachmentType.AWARD
                "portfolio" -> AttachmentType.PORTFOLIO
                else -> AttachmentType.DOCUMENT
            }
        }
    }

    val availableTabs = remember(groupedAttachments) {
        AttachmentType.entries.filter { groupedAttachments[it]?.isNotEmpty() == true }
    }

    if (availableTabs.isEmpty()) return

    var selectedTab by remember(availableTabs) { mutableStateOf(availableTabs.first()) }

    DetailSectionCard(title = "پیوست‌ها (${uiAttachments.size})") {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (availableTabs.size > 1) {
                AttachmentTabs(availableTabs, selectedTab, groupedAttachments, onTabSelected = { selectedTab = it })
                Spacer(modifier = Modifier.height(12.dp))
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "AttachmentTabContent"
            ) { tab ->
                val items = groupedAttachments[tab] ?: emptyList()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { uiAttachment ->
                        AttachmentItem(
                            uiAttachment = uiAttachment,
                            type = tab,
                            onClick = { onAttachmentClick(uiAttachment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentTabs(
    availableTabs: List<AttachmentType>,
    selectedTab: AttachmentType,
    groupedAttachments: Map<AttachmentType, List<UiAttachment>>,
    onTabSelected: (AttachmentType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        availableTabs.forEach { type ->
            val count = groupedAttachments[type]?.size ?: 0
            AttachmentTab(
                type = type,
                count = count,
                isSelected = selectedTab == type,
                onClick = { onTabSelected(type) }
            )
        }
    }
}

@Composable
private fun AttachmentTab(
    type: AttachmentType,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = type.title,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = type.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                shape = CircleShape,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AttachmentItem(
    uiAttachment: UiAttachment,
    type: AttachmentType,
    onClick: () -> Unit
) {
    val attachment = uiAttachment.attachment
    val cachedFile = uiAttachment.cachedFile
    val isImage = isImageFile(attachment.fileUrl)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Image/Icon Preview
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isImage) {
                    ManagedAsyncImage(
                        uiImage = UiImage(
                            id = attachment.id,
                            serverUrl = attachment.fileUrl,
                            localFile = cachedFile?.let { File(it.localPath) },
                            isCover = false
                        ),
                        contentDescription = attachment.title,
                        primaryCategoryName = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getFileIcon(attachment.fileUrl),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getFileExtension(attachment.fileUrl),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (attachment.verificationStatus == "verified") {
                        Text(text = "•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "تایید شده",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Action Icon
            Surface(
                onClick = onClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (cachedFile != null) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Download
                        },
                        contentDescription = if (cachedFile != null) "دانلود شده" else "دانلود",
                        tint = if (cachedFile != null) {
                            Color(0xFF10B981)
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
