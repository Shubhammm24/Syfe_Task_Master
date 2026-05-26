package com.example.sbam.controllers

import com.example.sbam.dtos.CategoryResponse
import com.example.sbam.dtos.CustomCategoryRequest
import com.example.sbam.services.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryService.getCategoriesForCurrentUser()
        val response = categories.map { category ->
            CategoryResponse(
                id = category.id!!,
                name = category.name,
                type = category.type,
                isCustom = category.isCustom
            )
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createCustomCategory(@Valid @RequestBody request: CustomCategoryRequest): ResponseEntity<CategoryResponse> {
        val category = categoryService.createCustomCategory(request)
        val response = CategoryResponse(
            id = category.id!!,
            name = category.name,
            type = category.type,
            isCustom = category.isCustom
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{name}")
    fun deleteCustomCategory(@PathVariable name: String): ResponseEntity<Map<String, String>> {
        categoryService.deleteCustomCategory(name)
        return ResponseEntity.ok(mapOf("message" to "Category deleted successfully"))
    }
}
