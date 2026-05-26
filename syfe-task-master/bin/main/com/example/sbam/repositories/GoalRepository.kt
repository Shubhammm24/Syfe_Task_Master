package com.example.sbam.repositories

import com.example.sbam.models.Goal
import com.example.sbam.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GoalRepository : JpaRepository<Goal, Long> {
    fun findByUser(user: User): List<Goal>
}
