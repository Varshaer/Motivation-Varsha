package com.example.ui.screens.motivation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MotivationalQuote
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.MotivationalQuoteCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun MotivationScreen(
    viewModel: MainViewModel,
    quotes: List<MotivationalQuote>,
    selectedCategory: String,
    onlyFavorites: Boolean,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val categories = listOf(
        "All",
        "Discipline",
        "Success",
        "Confidence",
        "Career",
        "Fitness",
        "Money",
        "Personal Growth",
        "Overcoming Failure"
    )

    // Daily spotlight quote based on day of year
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val dailySpotlight = if (quotes.isNotEmpty()) quotes[dayOfYear % quotes.size] else null

    // Filter quotes
    val filteredQuotes = quotes.filter { quote ->
        val catMatches = selectedCategory == "All" || quote.category.equals(selectedCategory, ignoreCase = true)
        val favMatches = !onlyFavorites || quote.isFavorite
        val queryMatches = searchQuery.isBlank() ||
                quote.quote.contains(searchQuery, ignoreCase = true) ||
                quote.author.contains(searchQuery, ignoreCase = true)
        catMatches && favMatches && queryMatches
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Motivation Vault",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fuel your ambition with timeless words of strength and wisdom.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setQuoteSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quote_search_bar"),
                placeholder = { Text("Search quotes or authors...", color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuoteSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
        }

        // Daily Motivation Spotlight Hero Card (if not searching)
        if (dailySpotlight != null && searchQuery.isBlank() && !onlyFavorites && selectedCategory == "All") {
            item {
                SectionHeader(title = "Daily Spotlight", subtitle = "Your primary spark of fire today")
            }
            item {
                DailySpotlightHeroCard(
                    quote = dailySpotlight,
                    onToggleFavorite = { viewModel.toggleQuoteFavorite(dailySpotlight) },
                    onShare = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "“${dailySpotlight.quote}”\n— ${dailySpotlight.author}\n\nShared from Motivator App")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Motivation"))
                    }
                )
            }
        }

        // Filter and Favorites row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorites toggle chip
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setOnlyFavorites(!onlyFavorites) }
                        .testTag("filter_favorites_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (onlyFavorites) GoldPrimary else DarkSurfaceVariant,
                    border = BorderStroke(1.dp, if (onlyFavorites) GoldPrimary else DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (onlyFavorites) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (onlyFavorites) Color(0xFF140F00) else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (onlyFavorites) Color(0xFF140F00) else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Categories Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = { viewModel.setQuoteCategory(category) },
                            modifier = Modifier.testTag("quote_category_$category")
                        )
                    }
                }
            }
        }

        // Quote List Header
        item {
            SectionHeader(
                title = if (onlyFavorites) "Saved Quotes" else "Library",
                subtitle = "${filteredQuotes.size} wisdom insights found"
            )
        }

        // Quotes Items
        if (filteredQuotes.isEmpty()) {
            item {
                EmptyQuotesCard(onlyFavorites = onlyFavorites)
            }
        } else {
            items(filteredQuotes, key = { it.id }) { quote ->
                MotivationalQuoteCard(
                    quote = quote,
                    onToggleFavorite = { viewModel.toggleQuoteFavorite(quote) },
                    onShare = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "“${quote.quote}”\n— ${quote.author}\n\nShared from Motivator App")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Motivation"))
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DailySpotlightHeroCard(
    quote: MotivationalQuote,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(GoldLight, GoldPrimary, GoldAccent.copy(alpha = 0.2f)))
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(GoldPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QUOTE OF THE DAY",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    CategoryChip(category = quote.category)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "“${quote.quote}”",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp).testTag("spotlight_fav")
                        ) {
                            Icon(
                                imageVector = if (quote.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favorite",
                                tint = if (quote.isFavorite) GoldPrimary else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.size(36.dp).testTag("spotlight_share")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyQuotesCard(onlyFavorites: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(DarkSurfaceElevated, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (onlyFavorites) Icons.Default.BookmarkBorder else Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (onlyFavorites) "No Favorite Quotes Yet" else "No Quotes Found",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (onlyFavorites) "Tap the bookmark icon on any message in the library to save it here." else "Try adjusting your search query or category filter.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
