package ir.behnamapps.cityguide.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.behnamapps.cityguide.data.remote.dto.ContactDto

@Composable
fun DescriptionSection(description: String?) {
    if (!description.isNullOrBlank()) {
        DetailSectionCard(title = "درباره ما") {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ContactSection(primaryPhone: String?, contacts: List<ContactDto>) {
    DetailSectionCard(title = "اطلاعات تماس") {
        Column {
            // شماره اصلی
            primaryPhone?.let {
                ContactItem(
                    department = "شماره اصلی",
                    phone = it,
                    isPrimary = true
                )
            }

            // سایر شماره‌ها
            contacts.forEachIndexed { index, contact ->
                if (primaryPhone != null || index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
                ContactItem(
                    department = contact.departmentName,
                    phone = contact.phoneNumber,
                    isPrimary = false
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    department: String,
    phone: String,
    isPrimary: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // اطلاعات تماس
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = department,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isPrimary) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = phone,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // دکمه تماس
        Surface(
            onClick = { /* TODO: Call Intent */ },
            shape = CircleShape,
            color = if (isPrimary) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "تماس با $department",
                    modifier = Modifier.size(20.dp),
                    tint = if (isPrimary) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }
        }
    }
}
