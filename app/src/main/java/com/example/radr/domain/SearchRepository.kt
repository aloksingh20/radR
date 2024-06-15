package com.example.radr.domain

import android.graphics.Bitmap
import com.example.radr.utils.Resource

interface SearchRepository {
    suspend fun search(query: String, image: Bitmap): Resource<String>
}