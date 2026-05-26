package com.example.sbam.models

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var fullName: String,

    @Column(nullable = false)
    var phoneNumber: String
)
