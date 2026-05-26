package com.example.sbam.repositories

import com.example.sbam.models.Category
import com.example.sbam.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByUserOrUserNull(user: User): List<Category>
    fun findByNameIgnoreCaseAndUserNull(name: String): Category?
    fun findByNameIgnoreCaseAndUser(name: String, user: User): Category?
}
