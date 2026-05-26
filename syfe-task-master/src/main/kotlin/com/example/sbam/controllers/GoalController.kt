package com.example.sbam.controllers

import com.example.sbam.dtos.GoalRequest
import com.example.sbam.dtos.GoalResponse
import com.example.sbam.services.GoalService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/goals")
class GoalController(
    private val goalService: GoalService
) {

    @PostMapping
    fun createGoal(@Valid @RequestBody request: GoalRequest): ResponseEntity<GoalResponse> {
        val goal = goalService.createGoal(request)
        val response = goalService.calculateProgress(goal)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllGoals(): ResponseEntity<List<GoalResponse>> {
        val goals = goalService.getGoals()
        return ResponseEntity.ok(goals)
    }

    @GetMapping("/{id}")
    fun getGoalById(@PathVariable id: Long): ResponseEntity<GoalResponse> {
        val goal = goalService.getGoalById(id)
        return ResponseEntity.ok(goal)
    }

    @PutMapping("/{id}")
    fun updateGoal(
        @PathVariable id: Long,
        @Valid @RequestBody request: GoalRequest
    ): ResponseEntity<GoalResponse> {
        val goal = goalService.updateGoal(id, request)
        val response = goalService.calculateProgress(goal)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteGoal(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        goalService.deleteGoal(id)
        return ResponseEntity.ok(mapOf("message" to "Goal deleted successfully"))
    }
}
