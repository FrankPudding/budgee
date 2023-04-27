package com.example.budgee

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.databinding.FragmentAssetsBinding
import com.example.budgee.db.AppDatabase
import com.example.budgee.db.AssetType
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AssetsFragment : Fragment() {
    private lateinit var appDb : AppDatabase
    private lateinit var tabs: TabLayout
    private var assetTypes: ArrayList<AssetType> = arrayListOf()

    lateinit var binding : FragmentAssetsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_assets, container, false)

        appDb = AppDatabase.getDatabase(requireActivity())

        tabs = view.findViewById(R.id.asset_tabs)
        GlobalScope.launch(Dispatchers.IO) {
            val assetTypesFromDb = appDb.assetTypeDao().getAll()
            assetTypes = ArrayList(assetTypesFromDb)
            if (assetTypes.isEmpty()) {
                assetTypes = arrayListOf(
                    AssetType(id=UUID.randomUUID(), name="Assets", position=1),
                    AssetType(id=UUID.randomUUID(), name="Liabilities", position=2)
                )
                for (assetType in assetTypes) {
                    appDb.assetTypeDao().insert(assetType)
                }
            }
            withContext(Dispatchers.Main) {
                for (assetType in assetTypes.sortedBy { it.position }) {
                    tabs.addTab(tabs.newTab().setText(assetType.name))
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.assets_text)
        textView.text = tabs.getTabAt(tabs.selectedTabPosition)?.text
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                textView.text = tab.text
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        defineAddButton(view)
    }

    private fun defineAddButton(view: View) {
        val addButton = view.findViewById<Button>(R.id.assets_add_button)
        addButton.setOnClickListener {addButtonCLickListener(view)}
    }

    private fun addButtonCLickListener(view: View) {
        val addAssetTypeButton = view.findViewById<Button>(R.id.assets_add_asset_type_button)
        if (addAssetTypeButton.visibility == View.GONE) {
            val slideUp = AnimationUtils.loadAnimation(this.context, R.anim.slide_up)
            addAssetTypeButton.visibility = View.VISIBLE
            addAssetTypeButton.startAnimation(slideUp)
            addAssetTypeButton.setOnClickListener {addAssetTypeClickListener(view)}
        }
        else {
            val slideDown = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
            addAssetTypeButton.visibility = View.GONE
            addAssetTypeButton.startAnimation(slideDown)
        }
    }

    private fun addAssetTypeClickListener(view: View) {
        showAddAssetDialog(view)
    }

    private fun showAddAssetDialog(view: View) {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Add Asset Type")

        val input = EditText(this.context)

        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, _ ->
            val text = input.text.toString()

            val newPosition = assetTypes.maxByOrNull { it.position }!!.position + 1
            val newAssetType = AssetType(
                id=UUID.randomUUID(), name=text, position=newPosition
            )
            addAssetType(newAssetType)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun addAssetType(newAssetType: AssetType) {
        GlobalScope.launch(Dispatchers.IO) {
            appDb.assetTypeDao().insert(newAssetType)
        }
        assetTypes.add(newAssetType)
        tabs.addTab(tabs.newTab().setText(newAssetType.name))
    }
}