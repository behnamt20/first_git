package ir.behnamapps.cityguide.ui.components.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.behnamapps.cityguide.R
import ir.behnamapps.cityguide.data.remote.dto.CategoryDto
import ir.behnamapps.cityguide.ui.navigation.AppDestinations

/**
 * Data class representing colors for a category item in different themes
 */
private data class CategoryColorScheme(
    val lightBackground: Color,
    val lightContent: Color,
    val darkBackground: Color,
    val darkContent: Color
)

/**
 * Predefined color schemes that work well in both light and dark modes
 * Each scheme is carefully designed for accessibility and visual appeal
 */
private val categoryColorSchemes = listOf(
    // Red/Pink scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFFFEBEE),
        lightContent = Color(0xFFC62828),
        darkBackground = Color(0xFF5D1F1F),
        darkContent = Color(0xFFFFCDD2)
    ),
    // Blue scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFE3F2FD),
        lightContent = Color(0xFF1565C0),
        darkBackground = Color(0xFF1A3A52),
        darkContent = Color(0xFF90CAF9)
    ),
    // Green scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFE8F5E9),
        lightContent = Color(0xFF2E7D32),
        darkBackground = Color(0xFF1B3A1F),
        darkContent = Color(0xFFA5D6A7)
    ),
    // Orange/Amber scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFFFF3E0),
        lightContent = Color(0xFFE65100),
        darkBackground = Color(0xFF4A3520),
        darkContent = Color(0xFFFFCC80)
    ),
    // Deep Orange scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFFBE9E7),
        lightContent = Color(0xFFD84315),
        darkBackground = Color(0xFF4A2C1F),
        darkContent = Color(0xFFFFAB91)
    ),
    // Purple scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFF3E5F5),
        lightContent = Color(0xFF6A1B9A),
        darkBackground = Color(0xFF3A1F4A),
        darkContent = Color(0xFFCE93D8)
    ),
    // Teal/Cyan scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFE0F2F1),
        lightContent = Color(0xFF00695C),
        darkBackground = Color(0xFF1F3A38),
        darkContent = Color(0xFF80CBC4)
    ),
    // Pink scheme
    CategoryColorScheme(
        lightBackground = Color(0xFFFCE4EC),
        lightContent = Color(0xFFC2185B),
        darkBackground = Color(0xFF4A1F35),
        darkContent = Color(0xFFF48FB1)
    )
)

@Composable
fun CategoryGrid(
    categories: List<CategoryDto>,
    navController: NavController,
    cityId: Int,
    cityName: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section title with theme-aware styling
        Text(
            text = "دسته‌بندی‌های اصلی",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Responsive grid layout
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories, key = { it.id }) { category ->
                val colorScheme = categoryColorSchemes[category.id % categoryColorSchemes.size]
                CategoryItem(
                    category = category,
                    colorScheme = colorScheme,
                    onClick = {
                        val isParent = category.parentId == null
                        val encodedCategoryName = Uri.encode(category.name)
                        Log.d("CategoryGrid", "Navigating to category: $encodedCategoryName")

                        val route = "${AppDestinations.LISTING_ROUTE}/" +
                                "${category.id}/" +
                                "$cityId/" +
                                "$encodedCategoryName?" +
                                "${AppDestinations.ARG_IS_PARENT}=$isParent"

                        navController.navigate(route)
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryDto,
    colorScheme: CategoryColorScheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = remember { RoundedCornerShape(20.dp) }

    // Select appropriate colors based on theme
    val backgroundColor = if (isDarkTheme) {
        colorScheme.darkBackground
    } else {
        colorScheme.lightBackground
    }

    val contentColor = if (isDarkTheme) {
        colorScheme.darkContent
    } else {
        colorScheme.lightContent
    }

    // Add subtle elevation for better visual hierarchy
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (isDarkTheme) 4.dp else 2.dp,
                shape = shape,
                clip = false
            ),
        shape = shape,
        color = backgroundColor,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Icon container with subtle background
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor.copy(alpha = 0.5f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(category.iconUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_content_food),
                        error = painterResource(R.drawable.ic_content_food),
                        contentDescription = category.name,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(contentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category name with proper theming
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
