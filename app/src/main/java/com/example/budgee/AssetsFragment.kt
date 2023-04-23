package com.example.budgee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgee.db.AppDatabase
import com.example.budgee.db.AssetType
import com.google.android.material.tabs.TabLayout
import java.util.*

class AssetsFragment : Fragment() {
    private lateinit var appDb: AppDatabase
    private lateinit var tabs: TabLayout
    private var assetTypes: List<AssetType> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appDb = AppDatabase.getDatabase(requireActivity())
        return inflater.inflate(R.layout.fragment_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs = view.findViewById(R.id.asset_tabs)
        assetTypes = listOf(
            AssetType(id = UUID.randomUUID(), name = "Assets", position = 1),
            AssetType(id = UUID.randomUUID(), name = "Liabilities", position = 2)
        )
        for (assetType in assetTypes.sortedBy { it.position }) {
            tabs.addTab(tabs.newTab().setText(assetType.name))
        }
    }
}