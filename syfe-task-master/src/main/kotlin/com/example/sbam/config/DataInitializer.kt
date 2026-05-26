package com.example.sbam.config

import com.example.sbam.services.CategoryService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val categoryService: CategoryService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        categoryService.seedDefaultCategories()
    }
}
