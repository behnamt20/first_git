package ir.behnamapps.cityguide.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import ir.behnamapps.cityguide.data.model.UiImage
import ir.behnamapps.cityguide.ui.components.common.ManagedAsyncImage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageGallery(
    images: List<UiImage>,
    pagerState: PagerState,
    primaryCategoryName: String?
) {
    if (images.isEmpty()) {
        // Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "بدون تصویر",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تصویری موجود نیست",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // گالری تصاویر
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val image = images[page]

            ManagedAsyncImage(
                uiImage = image,
                contentDescription = "تصویر گالری ${page + 1}",
                primaryCategoryName = primaryCategoryName,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                showOfflineMessageOnError = true
            )
        }

        // شمارنده تصاویر (اگر بیش از یک تصویر باشد)
        if (images.size > 1) {
            // شمارنده کامپکت
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${images.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Dots indicator
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = Color.White.copy(alpha = 0.5f),
                indicatorWidth = 6.dp,
                indicatorHeight = 6.dp,
                spacing = 4.dp
            )
        }
    }
}
