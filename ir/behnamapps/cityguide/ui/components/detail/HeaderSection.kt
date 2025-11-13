package ir.behnamapps.cityguide.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.behnamapps.cityguide.data.remote.dto.CategoryDto
import ir.behnamapps.cityguide.data.remote.dto.TagDto

@Composable
fun HeaderSection(
    name: String,
    contactPersonName: String?,
    userId: Int?,
    categories: List<CategoryDto>,
    tags: List<TagDto>,
    primaryPhone: String?,
    onContactPersonClick: (Int) -> Unit,
    onCallClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    var isBookmarked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // عنوان بزرگ (Instagram-style)
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // دسته‌بندی به صورت متنی ساده
        if (categories.isNotEmpty()) {
            val categoryText = categories.joinToString(" · ") { it.name }
            Text(
                text = categoryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // نام مخاطب
        contactPersonName?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.then(
                    if (userId != null) {
                        Modifier.clickable { onContactPersonClick(userId) }
                    } else {
                        Modifier
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // تگ‌ها
        if (tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            val tagText = tags.take(5).joinToString(" ") { "#${it.name}" } +
                    if (tags.size > 5) " +${tags.size - 5}" else ""
            Text(
                text = tagText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // دکمه‌های اکشن (Instagram-style: تماس + ذخیره کنار هم)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // دکمه تماس
            if (primaryPhone != null) {
                Button(
                    onClick = { onCallClick?.invoke() },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تماس",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // دکمه بوکمارک
            OutlinedButton(
                onClick = {
                    isBookmarked = !isBookmarked
                    onBookmarkClick?.invoke()
                },
                modifier = Modifier
                    .then(
                        if (primaryPhone == null) {
                            Modifier.weight(1f)
                        } else {
                            Modifier.width(90.dp)
                        }
                    )
                    .height(42.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isBookmarked) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isBookmarked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Filled.BookmarkBorder
                    },
                    contentDescription = "ذخیره",
                    tint = if (isBookmarked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(20.dp)
                )
                if (primaryPhone == null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBookmarked) "ذخیره شد" else "ذخیره",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
