package com.example.budgee.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "asset_types")
data class AssetType(
    @PrimaryKey val id: UUID,
    var name: String,
    var position: Int,
)
