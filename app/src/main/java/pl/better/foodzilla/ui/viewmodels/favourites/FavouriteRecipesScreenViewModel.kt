package pl.better.foodzilla.ui.viewmodels.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.better.foodzilla.data.models.Recipe
import pl.better.foodzilla.data.repositories.FavouriteRecipesRepository
import javax.inject.Inject

@HiltViewModel
class FavouriteRecipesScreenViewModel @Inject constructor(
    private val favouriteRecipesRepository: FavouriteRecipesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavouriteRecipesScreenUIState>(FavouriteRecipesScreenUIState.Loading())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            var recentRecipes: List<Recipe>? = null
            var favRecipes: List<Recipe>? = null
            favouriteRecipesRepository.getFavouriteRecipes()?.let {
                favRecipes = it
                recentRecipes = it
            } ?: run {
                _uiState.value = FavouriteRecipesScreenUIState.Error("Error while loading favourite recipes")
            }
            //TODO: RETRIVE RECENT RECIPES FROM API
            if (recentRecipes != null && favRecipes != null) {
                _uiState.value = FavouriteRecipesScreenUIState.Success(recentRecipes, favRecipes)
            } else {
                _uiState.value = FavouriteRecipesScreenUIState.Error("Error while loading recipes")
            }
        }
    }

    sealed class FavouriteRecipesScreenUIState(open val recentRecipes: List<Recipe>?,
                                               open val favRecipes: List<Recipe>?) {
        data class Success(override val recentRecipes: List<Recipe>? = null,
                           override val favRecipes: List<Recipe>? = null) :
            FavouriteRecipesScreenUIState(recentRecipes, favRecipes)

        data class Error(val message: String?, override val recentRecipes: List<Recipe>? = null,
                         override val favRecipes: List<Recipe>? = null) :
            FavouriteRecipesScreenUIState(recentRecipes, favRecipes)

        data class Loading(override val recentRecipes: List<Recipe>? = null,
                           override val favRecipes: List<Recipe>? = null) :
            FavouriteRecipesScreenUIState(recentRecipes, favRecipes)
    }
}