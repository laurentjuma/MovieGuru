package com.frogtest.movieguru.data.cache.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frogtest.movieguru.data.cache.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    // Select movies with release date greater that 2000
    @Query("SELECT * FROM MovieEntity"
            + " WHERE year > 2000"
            + " ORDER BY year ASC"
    )
    fun getSortedMovies(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM MovieEntity"
            + " WHERE year > 2000"
    )
    fun getMovies(): PagingSource<Int, MovieEntity>

   //Get item count
    @Query("SELECT COUNT(*) FROM MovieEntity")
    suspend fun getCount(): Int

    @Query("DELETE FROM MovieEntity")
    suspend fun clearAll()
}