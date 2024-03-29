package com.rivaldy.id.compose.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldy.id.core.data.UiState
import com.rivaldy.id.core.data.datasource.local.db.entity.ProductEntity
import com.rivaldy.id.core.data.model.Product
import com.rivaldy.id.core.data.model.mapper.ProductMapper.mapFromProductToEntity
import com.rivaldy.id.core.data.repository.DbProductRepositoryImpl
import com.rivaldy.id.core.data.repository.ProductRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by github.com/im-o on 12/16/2022. */

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ProductRepositoryImpl,
    private val dbRepository: DbProductRepositoryImpl
) : ViewModel() {

    private val _uiStateProduct: MutableStateFlow<UiState<Product>> = MutableStateFlow(UiState.Loading)
    val uiStateProduct: StateFlow<UiState<Product>>
        get() = _uiStateProduct

    private val _uiStateDbProduct: MutableStateFlow<UiState<ProductEntity>> = MutableStateFlow(UiState.Loading)
    val uiStateDbProduct: StateFlow<UiState<ProductEntity>>
        get() = _uiStateDbProduct


    fun getProductByIdApiCall(id: Int) {
        viewModelScope.launch {
            try {
                repository.getProductByIdApiCall(id)
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

    fun getProductByIdDb(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbRepository.getProductByIdDb(id).catch {
                    _uiStateDbProduct.value = UiState.Error(it.message.toString())
                }.collect { product ->
                    _uiStateDbProduct.value = UiState.Success(product)
                }
            } catch (e: Exception) {
                _uiStateDbProduct.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun insertProductDb(product: Product) {
        viewModelScope.launch {
            val longInsertStatus = dbRepository.insertProductDb(mapFromProductToEntity(product))
            if (longInsertStatus > 0) getProductByIdDb((product.id ?: -1).toLong())
        }
    }
}