package com.example.budgee.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AssetTypeDao {
    @Query("SELECT * FROM asset_types ORDER BY position ASC")
    suspend fun getAll(): List<AssetType>

    @Query("SELECT * FROM asset_types WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): AssetType

    @Insert
    suspend fun insert(assetType: AssetType)

    @Update
    suspend fun update(assetType: AssetType)

    @Delete
    suspend fun delete(assetType: AssetType)

    @Query("DELETE FROM asset_types")
    suspend fun deleteAll()
}