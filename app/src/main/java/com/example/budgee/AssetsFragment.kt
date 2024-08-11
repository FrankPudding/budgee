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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgee.db.AppDatabase
import com.example.budgee.db.AssetType
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AssetsFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var appDb: AppDatabase
    private lateinit var tabs: TabLayout
    private var assetTypes: ArrayList<AssetType> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_assets, container, false)

        appDb = AppDatabase.getDatabase(requireActivity())

        tabs = rootView.findViewById(R.id.asset_tabs)
        viewLifecycleOwner.lifecycleScope.launch {
            val assetTypesFromDb = appDb.assetTypeDao().getAll()
            assetTypes = ArrayList(assetTypesFromDb)
            if (assetTypes.isEmpty()) {
                assetTypes = arrayListOf(
                    AssetType(id = UUID.randomUUID(), name = "Assets", position = 0),
                    AssetType(id = UUID.randomUUID(), name = "Liabilities", position = 1)
                )
                for (assetType in assetTypes) {
                    appDb.assetTypeDao().insert(assetType)
                }
            }
            withContext(Dispatchers.Main) {
                for (assetType in assetTypes.sortedBy { it.position }) {
                    tabs.addTab(tabs.newTab().setText(assetType.name))
                }
                defineTabLongClickListeners()
            }
        }

        return rootView
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

        defineAddButton()
    }

    private fun defineAddButton() {
        val addButton = rootView.findViewById<Button>(R.id.assets_add_button)
        addButton.setOnClickListener { onClickListenerForAddButton() }
    }

    private fun onClickListenerForAddButton() {
        val addAssetTypeButton = rootView.findViewById<Button>(R.id.assets_add_asset_type_button)
        if (addAssetTypeButton.visibility == View.GONE) {
            val slideUp = AnimationUtils.loadAnimation(this.context, R.anim.slide_up)
            addAssetTypeButton.visibility = View.VISIBLE
            addAssetTypeButton.startAnimation(slideUp)
            addAssetTypeButton.setOnClickListener { onClickListenerForAddAssetTypeButton() }
            rootView.setOnClickListener {
                hideAddAssetTypeButton()
            }
        } else {
            hideAddAssetTypeButton()
        }
    }

    private fun onClickListenerForAddAssetTypeButton() {
        hideAddAssetTypeButton()
        showAddAssetDialog()
    }

    private fun showAddAssetDialog() {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Add Asset Type")

        val input = EditText(this.context)

        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, _ ->
            val text = input.text.toString()

            val newPosition = assetTypes.maxByOrNull { it.position }!!.position + 1
            val newAssetType = AssetType(
                id = UUID.randomUUID(), name = text, position = newPosition
            )
            addAssetType(newAssetType = newAssetType)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun addAssetType(newAssetType: AssetType) {
        viewLifecycleOwner.lifecycleScope.launch {
            appDb.assetTypeDao().insert(newAssetType)
        }
        assetTypes.add(newAssetType)
        tabs.addTab(tabs.newTab().setText(newAssetType.name))
        defineTabLongClickListeners()
    }

    private fun defineTabLongClickListeners() {
        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            tab?.view?.setOnLongClickListener {
                hideAddAssetTypeButton()
                showEditAssetTypeDialog(assetType = assetTypes[i])
                true
            }
        }
    }

    private fun showEditAssetTypeDialog(assetType: AssetType) {
        val parent = rootView.findViewById<ConstraintLayout>(R.id.assets_constraint_layout)
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_asset_type, parent, false)
        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val titleTextView = dialogView.findViewById<TextView>(R.id.assets_edit_asset_type_title)
        titleTextView.text = getString(R.string.assets_edit_asset_type_title, assetType.name)

        val deleteButton =
            dialogView.findViewById<Button>(R.id.assets_edit_asset_type_delete_button)
        deleteButton.setOnClickListener {
            if (assetTypes.size == 1) {
                Toast.makeText(
                    this.context, "You can't delete your only tab!", Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    appDb.assetTypeDao().delete(assetType)
                }
                val index = assetTypes.indexOf(assetType)
                assetTypes.removeAt(index)
                tabs.removeTabAt(index)
                Toast.makeText(this.context, "${assetType.name} deleted!", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        }

        val renameButton =
            dialogView.findViewById<Button>(R.id.assets_edit_asset_type_edit_name_button)
        renameButton.setOnClickListener {
            showEditAssetNameDialog(assetType = assetType)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun hideAddAssetTypeButton() {
        val addAssetTypeButton = rootView.findViewById<Button>(R.id.assets_add_asset_type_button)
        if (addAssetTypeButton.visibility == View.GONE) {
            return
        }
        val slideDown = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
        addAssetTypeButton.visibility = View.GONE
        addAssetTypeButton.startAnimation(slideDown)
        rootView.setOnClickListener(null)
    }

    private fun showEditAssetNameDialog(assetType: AssetType) {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Rename ${assetType.name}")

        val input = EditText(this.context)

        builder.setView(input)
        builder.setPositiveButton("Rename") { dialog, _ ->
            assetType.name = input.text.toString()
            tabs.getTabAt(assetType.position)?.setText(assetType.name)
            viewLifecycleOwner.lifecycleScope.launch {
                appDb.assetTypeDao().update(assetType)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}