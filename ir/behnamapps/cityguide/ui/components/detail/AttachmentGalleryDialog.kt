package ir.behnamapps.cityguide.ui.components.detail

import androidx.compose.runtime.Immutable
import ir.behnamapps.cityguide.ui.screens.detail.UiAttachment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.*
import ir.behnamapps.cityguide.data.model.UiImage
import ir.behnamapps.cityguide.data.remote.dto.AttachmentDto
import ir.behnamapps.cityguide.ui.components.common.ManagedAsyncImage
import ir.behnamapps.cityguide.ui.screens.detail.GalleryState

import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

import java.io.File

@Composable
private fun GalleryItem(
    uiAttachment: UiAttachment,
    onDownloadClick: (AttachmentDto) -> Unit
) {
    val attachment = uiAttachment.attachment
    val cachedFile = uiAttachment.cachedFile
    val isCached = cachedFile != null && File(cachedFile.localPath).exists()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isCached) {
            // Image is cached, show it with zoom capability
            val zoomState = rememberZoomState()
            ManagedAsyncImage(
                uiImage = UiImage(
                    id = attachment.id,
                    serverUrl = attachment.fileUrl,
                    localFile = File(cachedFile!!.localPath),
                    isCover = false
                ),
                contentScale = ContentScale.Fit,
                contentDescription = attachment.title,
                primaryCategoryName = null,
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState)
            )
        } else {
            // Image is not cached, show download prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = { onDownloadClick(attachment) },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "دانلود تصویر",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = "برای مشاهده دانلود کنید",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AttachmentGalleryDialog(
    galleryState: GalleryState,
    onDismiss: () -> Unit,
    onDownloadClick: (AttachmentDto) -> Unit
) {
    if (!galleryState.isVisible) return

    val pagerState = rememberPagerState(initialPage = galleryState.initialIndex)
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Pager for swiping between images
            HorizontalPager(
                count = galleryState.images.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                GalleryItem(
                    uiAttachment = galleryState.images[page],
                    onDownloadClick = onDownloadClick
                )
            }

            // Close Button (top right)
            Surface(
                onClick = onDismiss,
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Title at the bottom
            if (pagerState.currentPage < galleryState.images.size) {
                val currentAttachment = galleryState.images[pagerState.currentPage].attachment
                Text(
                    text = currentAttachment.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(12.dp)
                )
            }

            // Navigation Arrows (only if more than one image)
            if (galleryState.images.size > 1) {
                // Previous Button
                if (pagerState.currentPage > 0) {
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "قبلی",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Next Button
                if (pagerState.currentPage < galleryState.images.size - 1) {
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "بعدی",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
