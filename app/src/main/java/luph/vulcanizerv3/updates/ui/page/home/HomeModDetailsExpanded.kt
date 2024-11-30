package luph.vulcanizerv3.updates.ui.page.home

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.common.reflect.TypeToken
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.NavigationAnim
import luph.vulcanizerv3.updates.ui.components.HomeModDetailsCard
import luph.vulcanizerv3.updates.ui.components.ImageCarousel
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.showNavigation

data class modList (
    val modlist: List<ModDetails>
)

@Composable
fun HomeModDetailsExpanded(navController: NavController, view: View) {
    val modCategoriesState = ModDetailsStore.getAllModKeywords()
    val modDetailString =
        remember { mutableStateOf(RouteParams.peek(String::class.java) ?: "All Mods") }
    val modDetails = remember {
        derivedStateOf {
            modCategoriesState.value[modDetailString.value] ?: RouteParams.pop(modList::class.java)?.modlist ?:  emptyList()
        }
    }


    val searchQuery = remember { mutableStateOf("") }
    val filteredModDetails = remember { mutableStateOf(modDetails.value) }
    val isSearchVisible = remember { mutableStateOf(false) }

    showNavigation.show = false


    BackHandler {
        RouteParams.pop(String::class.java)
        NavigationAnim.popExit.value = shrinkVertically()
        navController.popBackStack()
    }

    Column(Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    shape = ShapeDefaults.Large
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isSearchVisible.value
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery.value,
                        shape = ShapeDefaults.Large,
                        onValueChange = { query ->
                            searchQuery.value = query
                            filteredModDetails.value = modDetails.value.filter {
                                it.name.contains(
                                    query,
                                    ignoreCase = true
                                ) || it.keywords.any { keyword ->
                                    keyword.contains(
                                        query,
                                        ignoreCase = true
                                    )
                                } //todo  || it.briefDescription.contains(query, ignoreCase = true)
                            }
                        },
                        placeholder = { Text("Search") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,

                            ),
                        modifier = Modifier
                            .weight(1f)
                    )
                    IconButton(onClick = {
                        searchQuery.value = ""
                        filteredModDetails.value = modDetails.value
                        isSearchVisible.value = false
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    RouteParams.pop(String::class.java)
                    NavigationAnim.popExit.value = shrinkVertically()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(text = modDetailString.value, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isSearchVisible.value = true }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            }
        }

        LazyColumn {
            filteredModDetails.value.forEach { modDetail ->
                item {
                    Column(Modifier.padding(bottom = 32.dp)) {
                        ImageCarousel(
                            (1..modDetail.images).map { index -> "${modDetail.url}$index.jpg" },
                            modifier = Modifier
                                .height(168.dp)
                                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        HomeModDetailsCard(modDetail, 16.dp, navController, view)
                    }
                }
            }
        }
    }
}

