package com.frogtest.movieguru.presentation.movies

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.frogtest.movieguru.data.mappers.toMovie
import com.frogtest.movieguru.domain.model.Movie
import com.frogtest.movieguru.domain.repository.AuthRepository
import com.frogtest.movieguru.domain.repository.MovieRepository
import com.frogtest.movieguru.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val TAG = "MovieViewModel"

    val settingsUiState: StateFlow<SettingsUiState> =
        userSettingsRepository.userSettings
            .map { userData ->
                SettingsUiState.Success(
                    settings = UserEditableSettings(
                        sort = userData.sort,
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                // Starting eagerly means the user data is ready when the SettingsDialog is laid out
                // for the first time. Without this, due to b/221643630 the layout is done using the
                // "Loading" text, then replaced with the user editable fields once loaded, however,
                // the layout height doesn't change meaning all the fields are squashed into a small
                // scrollable column.
                // TODO: Change to SharingStarted.WhileSubscribed(5_000) when b/221643630 is fixed
                started = SharingStarted.Eagerly,
                initialValue = SettingsUiState.Loading,
            )

    private var searchJob: Job? = null

    private val _searchQuery = mutableStateOf("")
    val searchQuery = _searchQuery

    private val _searchedMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val searchedMovies = _searchedMovies

    val getSignedInUser get() = authRepository.getSignedInUser()

    fun onMovieEvent(event: MovieEvent) {
        when (event) {
            is MovieEvent.OnSearchQueryChange -> {
                if (searchQuery.value != event.query) {
                    updateSearchQuery(event.query)
                    searchJob?.cancel()
                    searchJob = viewModelScope.launch {
                        getMovies(userSettingsRepository.userSettings.first().sort, event.query)
                    }
                }
            }

            is MovieEvent.OnSortToggled -> {
                viewModelScope.launch {
                    userSettingsRepository.toggleSort(event.sort)
                    getMovies(event.sort, searchQuery.value)
                }
            }

            is MovieEvent.OnSearchInitiated -> {
                viewModelScope.launch {
                    getMovies(userSettingsRepository.userSettings.first().sort, searchQuery.value)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            getMovies(userSettingsRepository.userSettings.first().sort, searchQuery.value)
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


    fun getMovies(sort: Boolean, query: String) {
        Log.d(TAG, "getMovies: $query")
        viewModelScope.launch {
            repository.getMovies(
                sort = sort,
                query = query.ifBlank { "love" }
            ).map { pagingData ->
                pagingData.map { movieEntity ->
                    movieEntity.toMovie()
                }
            }.cachedIn(viewModelScope).collect {
                _searchedMovies.value = it
            }
        }
    }

}

data class UserEditableSettings(
    val sort: Boolean,
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}