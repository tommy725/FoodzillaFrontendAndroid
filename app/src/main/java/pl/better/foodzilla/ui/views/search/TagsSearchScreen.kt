package pl.better.foodzilla.ui.views.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pl.better.foodzilla.R
import pl.better.foodzilla.ui.components.*
import pl.better.foodzilla.ui.navigation.BottomBarNavGraph
import pl.better.foodzilla.ui.viewmodels.search.TagsSearchScreenViewModel
import pl.better.foodzilla.utils.SizeNormalizer

@Composable
@Destination
@BottomBarNavGraph
fun TagsSearchScreen(
    navigator: DestinationsNavigator,
    viewModel: TagsSearchScreenViewModel = hiltViewModel(),
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Column(modifier = Modifier.fillMaxSize()) {
        ImageCenter(
            modifier = Modifier.height(SizeNormalizer.normalize(70.dp, screenHeight)),
            imageModifier = Modifier.height(SizeNormalizer.normalize(30.dp, screenHeight)),
            painterResource = painterResource(id = R.drawable.foodzilla_logo)
        )
        ListAdderWithSuggestions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .padding(horizontal = 15.dp),
            label = "Add tags",
            possibleItems = viewModel.possibleItemsFiltered.collectAsStateWithLifecycle().value,
            onChangeAddingSearchItem = viewModel::changeAddingSearchItem,
            addingItemSearch = viewModel.addingItemSearch.collectAsStateWithLifecycle().value,
            onChangeSearchState = viewModel::changeSearchState,
            searchState = viewModel.searchState.collectAsStateWithLifecycle().value,
            onChangeChosenItem = viewModel::changeChosenItem,
            addItem = viewModel::addItem
        )
        if (viewModel.chosenItems.collectAsStateWithLifecycle().value.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    SizeNormalizer.normalize(
                        8.dp,
                        screenHeight
                    )
                )
            ) {
                items(viewModel.chosenItems.value) { tag ->
                    LabelWithDelete(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .defaultMinSize(
                                minHeight = SizeNormalizer.normalize(
                                    50.dp,
                                    screenHeight
                                ), minWidth = 0.dp
                            )
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colors.primary),
                        label = tag.name,
                        item = tag,
                        removeTag = viewModel::removeItem
                    )
                }
            }
        }
    }
}