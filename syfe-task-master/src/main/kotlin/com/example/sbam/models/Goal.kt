package com.example.sbam.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "goals")
class Goal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var goalName: String,

    @Column(nullable = false, precision = 15, scale = 2)
    var targetAmount: BigDecimal,

    @Column(nullable = false)
    var targetDate: LocalDate,

    @Column(nullable = false)
    var startDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
)
