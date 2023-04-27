package com.example.budgee.db

import androidx.room.*

@Dao
interface AssetTypeDao {
    @Query("SELECT * FROM asset_types ORDER BY position ASC")
    suspend fun getAll(): List<AssetType>

    @Query("SELECT * FROM asset_types WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): AssetType

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(assetType: AssetType)

    @Delete
    suspend fun delete(assetType: AssetType)

    @Query("DELETE FROM asset_types")
    suspend fun deleteAll()
}