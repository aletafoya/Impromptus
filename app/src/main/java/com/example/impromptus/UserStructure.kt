package com.example.impromptus

data class UserStructure(
    val id: Int,
    val username: String,
    val email: String,
    val password_hash: String,
    val role: String,
    val created_at: String,
    val status: Int)