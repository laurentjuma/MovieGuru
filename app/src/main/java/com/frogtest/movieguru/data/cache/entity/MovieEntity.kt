package com.frogtest.movieguru.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val imdbID: String,
    val title: String,
    val year: String,
    val poster: String,
    val type: String
)