package pl.better.foodzilla.ui.viewmodels.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.better.foodzilla.data.models.recipe.Recipe
import pl.better.foodzilla.data.models.search.SearchRequest
import pl.better.foodzilla.data.repositories.recipe.RecipeRepository
import pl.better.foodzilla.utils.DispatchersProvider
import javax.inject.Inject

@HiltViewModel
class RecipesListPagedViewModel @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecipesListPagedUIState>(RecipesListPagedUIState.Loading())
    val uiState = _uiState.asStateFlow()
    private val exceptionHandler = kotlinx.coroutines.CoroutineExceptionHandler { _, error ->
        var exceptionMessage = error.message
        if (error.message == null) {
            exceptionMessage = "Unexpected error occurred. Try again later!"
        }
        _uiState.value = RecipesListPagedUIState.Error(exceptionMessage)
    }

    fun searchRecipes(search: SearchRequest) {
        viewModelScope.launch(dispatchers.io + exceptionHandler) {
            _uiState.value = RecipesListPagedUIState.Success(recipeRepository.searchRecipes(search))
        }
    }

    sealed class RecipesListPagedUIState {
        data class Success(val recipes: Flow<PagingData<Recipe>>) : RecipesListPagedUIState()
        data class Error(val message: String? = null) : RecipesListPagedUIState()
        data class Loading(val message: String? = null) : RecipesListPagedUIState()
    }
}