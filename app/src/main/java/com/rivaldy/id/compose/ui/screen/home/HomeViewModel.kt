package com.rivaldy.id.compose.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldy.id.core.data.UiState
import com.rivaldy.id.core.data.model.ProductResponse
import com.rivaldy.id.core.data.repository.ProductRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by github.com/im-o on 12/16/2022. */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepositoryImpl
) : ViewModel() {

    private val _uiStateProduct: MutableStateFlow<UiState<ProductResponse>> = MutableStateFlow(UiState.Loading)
    val uiStateProduct: StateFlow<UiState<ProductResponse>>
        get() = _uiStateProduct

    private val _query = mutableStateOf("")
    val query: State<String> get() = _query

    fun getProductsApiCall() {
        viewModelScope.launch {
            try {
                repository.getProductsApiCall()
                    .catch {
                        _uiStateProduct.value = UiState.Error(it.message.toString())
                    }
                    .collect { product ->
                        _uiStateProduct.value = UiState.Success(product)
                    }
            } catch (e: Exception) {
                _uiStateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun searchProductApiCall(query: String) {
        _query.value = query
        viewModelScope.launch {
            try {
                repository.searchProductApiCall(_query.value)
                    .catch {
                        _uiStateProduct.value = UiState.Error(it.message.toString())
                    }
                    .collect { product ->
                        _uiStateProduct.value = UiState.Success(product)
                    }
            } catch (e: Exception) {
                _uiStateProduct.value = UiState.Error(e.message.toString())
            }
        }
    }
}