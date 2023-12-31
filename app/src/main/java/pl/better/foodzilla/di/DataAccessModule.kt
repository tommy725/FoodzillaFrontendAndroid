package pl.better.foodzilla.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.better.foodzilla.BuildConfig
import pl.better.foodzilla.data.api.recipe.RecipeFlowClient
import pl.better.foodzilla.data.api.login.LoginFlowClient
import pl.better.foodzilla.data.api.recipe.FavouriteAndRecentRecipesFlowClient
import pl.better.foodzilla.data.api.search.FavouriteSearchesClient
import pl.better.foodzilla.data.repositories.*
import pl.better.foodzilla.data.repositories.login.LoginRepository
import pl.better.foodzilla.data.repositories.login.LoginRepositoryImpl
import pl.better.foodzilla.data.repositories.recipe.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataAccessModule {

    @Provides
    @Singleton
    fun provideApolloClient(sharedPreferencesRepository: SharedPreferencesRepository): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BACKEND_URL)
            .okHttpClient(
                OkHttpClient.Builder()
                    .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                    .callTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor { chain: Interceptor.Chain ->
                        val token = sharedPreferencesRepository.getLoggedUserData()?.token
                            ?: return@addInterceptor chain.proceed(chain.request())
                        val requestBuilder: Request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                        chain.proceed(requestBuilder)
                    }
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginFlowClient(apolloClient: ApolloClient): LoginFlowClient {
        return LoginFlowClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(loginFlowClient: LoginFlowClient): LoginRepository {
        return LoginRepositoryImpl(loginFlowClient)
    }

    @Provides
    @Singleton
    fun provideRecipeFlowClient(apolloClient: ApolloClient): RecipeFlowClient {
        return RecipeFlowClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeFlowClient: RecipeFlowClient): RecipeRepository {
        return RecipeRepositoryImpl(recipeFlowClient)
    }

    @Provides
    @Singleton
    fun provideFavouriteRecipesFlowClient(apolloClient: ApolloClient): FavouriteAndRecentRecipesFlowClient {
        return FavouriteAndRecentRecipesFlowClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideFavouriteRecipesRepository(favouriteAndRecentRecipesFlowClient: FavouriteAndRecentRecipesFlowClient): FavouriteAndRecentRecipesRepository {
        return FavouriteAndRecentRecipesRepositoryImpl(favouriteAndRecentRecipesFlowClient)
    }

    @Provides
    @Singleton
    fun provideFavouriteSearchesClient(apolloClient: ApolloClient): FavouriteSearchesClient {
        return FavouriteSearchesClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideSavedSearchesRepository(favouriteSearchesClient: FavouriteSearchesClient): SavedSearchesRepository {
        return SavedSearchesRepositoryImpl(favouriteSearchesClient)
    }
}