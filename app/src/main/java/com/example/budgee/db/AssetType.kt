package com.example.budgee.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "asset_types")
data class AssetType(
    @PrimaryKey val id: UUID,
    val name: String,
    val position: Int,
)
