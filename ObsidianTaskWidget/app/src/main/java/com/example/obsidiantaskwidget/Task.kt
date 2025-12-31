package com.example.obsidiantaskwidget

data class Task(
    val title: String,
    val status: String,
    val due: String?,
    val priority: String?,
    val filename: String
)
