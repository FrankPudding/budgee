package com.example.budgee.db

import androidx.room.*

@Dao
interface AssetTypeDao {
    @Query("SELECT * FROM asset_types ORDER BY position ASC")
    fun getAll(): List<AssetType>

    @Query("SELECT * FROM asset_types WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): AssetType

    @Insert()
    fun insert(assetType: AssetType)

    @Delete
    fun delete(assetType: AssetType)

    @Query("DELETE FROM asset_types")
    fun deleteAll()
}