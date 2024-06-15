package com.example.radr.data

import android.graphics.Bitmap
import com.example.radr.domain.SearchRepository
import com.example.radr.utils.Resource
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig

class SearchRepositoryImpl: SearchRepository {

    private  var generateModelVision : GenerativeModel

//    private var generateModelText : GenerativeModel

    init {

        val config = generationConfig {
            temperature = 0.70f
        }

        generateModelVision = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = "AIzaSyBGIl5RHqQa4VqsWTHnuLLc4QaymDiXZEk",
            generationConfig = config
        )

    }
    override suspend fun search(query: String, image: Bitmap): Resource<String> {
        return try {
            var output = ""
            val content = content {
                image(image)
                text(query)
            }

            val response= generateModelVision.generateContent(content)
            output += response.text
            Resource.Success(output)
        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred")

        }
    }
}