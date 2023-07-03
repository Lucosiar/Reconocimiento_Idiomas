package com.example.traductor

data class Detection (
    val language: String,
    val isReliable: Boolean,
    val condifence: Double)