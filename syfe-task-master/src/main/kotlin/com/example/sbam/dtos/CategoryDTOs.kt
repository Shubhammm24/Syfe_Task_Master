package com.example.sbam.dtos

import com.example.sbam.models.CategoryType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CustomCategoryRequest(
    @field:NotBlank(message = "Category name is required")
    val name: String,

    @field:NotNull(message = "Category type is required")
    val type: CategoryType
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val isCustom: Boolean
)
