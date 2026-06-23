package com.pklein.bookmemokmp.presentation.additem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pklein.bookmemokmp.data.UserPreferencesRepository
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.SearchResult
import com.pklein.bookmemokmp.domain.usecase.BookSearchUseCase
import com.pklein.bookmemokmp.domain.usecase.GetCollectionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchState {
    data object Idle : SearchState

    data object Loading : SearchState

    data class Success(
        val results: List<SearchResult>,
    ) : SearchState

    data object Empty : SearchState

    data object Error : SearchState

    data object NotFoundException : SearchState
}

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModel(
    private val bookSearchUseCase: BookSearchUseCase,
    private val userPrefs: UserPreferencesRepository,
    private val getCollection: GetCollectionUseCase,
) : ViewModel() {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    var englishDescriptionPref: Boolean
        get() = userPrefs.saveEnglishDescription
        set(value) = saveEnglishDescriptionPref(value)

    private fun saveEnglishDescriptionPref(enabled: Boolean) {
        userPrefs.saveEnglishDescription = enabled
    }

    fun searchByIsbn(
        isbn: String?,
        isNotFoundException: Boolean = false,
    ) {
        if (_searchState.value is SearchState.Loading) return
        if (isNotFoundException) {
            _searchState.value = SearchState.NotFoundException
            return
        }
        if (isbn.isNullOrBlank()) {
            _searchState.value = SearchState.Error
            return
        }
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            _searchState.value =
                runCatching {
                    bookSearchUseCase.searchByIsbn(isbn)
                }.fold(
                    onSuccess = { searchResults ->
                        if (searchResults.isEmpty()) {
                            return@fold SearchState.Empty
                        }
                        SearchState.Success(searchResults)
                    },
                    onFailure = { SearchState.Error },
                )
        }
    }

    fun search(
        query: String,
        type: ItemType,
        langRestrict: String? = null,
    ) {
        if (_searchState.value is SearchState.Loading) return
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            _searchState.value =
                runCatching {
                    bookSearchUseCase.search(query, type, langRestrict)
                }.fold(
                    onSuccess = { searchResults ->
                        if (searchResults.isEmpty()) {
                            return@fold SearchState.Empty
                        }
                        SearchState.Success(searchResults)
                    },
                    onFailure = { SearchState.Error },
                )
        }
    }

    suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean = getCollection.existsByTitleAndType(title, type, excludeId)
}
