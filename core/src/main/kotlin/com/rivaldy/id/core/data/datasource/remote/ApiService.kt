package com.rivaldy.id.core.data.datasource.remote

import com.rivaldy.id.core.data.model.ProductResponse
import retrofit2.http.*

/** Created by github.com/im-o on 10/1/2022. */

interface ApiService {
    @GET("products")
    suspend fun fetchProducts(): ProductResponse
}