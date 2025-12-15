package com.ecommerce.adminapp.ui.banners

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ecommerce.adminapp.R
import com.ecommerce.adminapp.data.model.Banner
import com.ecommerce.adminapp.databinding.DialogAddEditBannerBinding
import com.ecommerce.adminapp.databinding.FragmentBannersBinding
import com.ecommerce.adminapp.ui.adapter.BannerAdapter
import com.ecommerce.adminapp.utils.ImageUploadHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BannersFragment : Fragment() {

    private var _binding: FragmentBannersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BannersViewModel by viewModels()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var imageUploadHelper: ImageUploadHelper

    private var selectedImageUri: Uri? = null
    private var currentEditingBanner: Banner? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            currentDialog?.let { dialog ->
                val dialogBinding = DialogAddEditBannerBinding.bind(dialog.window!!.decorView)
                Glide.with(this)
                    .load(uri)
                    .into(dialogBinding.bannerImagePreview)
            }
        }
    }

    private var currentDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBannersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUploadHelper = ImageUploadHelper(requireContext())

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bannerAdapter = BannerAdapter(
            onEditClick = { banner ->
                showAddEditDialog(banner)
            },
            onDeleteClick = { banner ->
                showDeleteConfirmationDialog(banner)
            }
        )

        binding.bannersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bannerAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddBanner.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.banners.collect { banners ->
                bannerAdapter.submitList(banners)
                binding.emptyStateLayout.visibility = if (banners.isEmpty()) View.VISIBLE else View.GONE
                binding.bannersRecyclerView.visibility = if (banners.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddEditDialog(banner: Banner?) {
        val dialogBinding = DialogAddEditBannerBinding.inflate(layoutInflater)
        currentEditingBanner = banner
        selectedImageUri = null

        dialogBinding.dialogTitle.text = if (banner == null) "Add Banner" else "Edit Banner"

        banner?.let {
            dialogBinding.bannerNameEditText.setText(it.name)
            dialogBinding.imageUrlEditText.setText(it.picUrl)
            dialogBinding.activeSwitch.isChecked = it.active
            Glide.with(this)
                .load(it.picUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(dialogBinding.bannerImagePreview)
        }

        dialogBinding.imageUrlEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val imageUrl = dialogBinding.imageUrlEditText.text.toString().trim()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(dialogBinding.bannerImagePreview)
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        currentDialog = dialog

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
            currentDialog = null
        }

        dialogBinding.saveButton.setOnClickListener {
            val name = dialogBinding.bannerNameEditText.text.toString().trim()
            val imageUrl = dialogBinding.imageUrlEditText.text.toString().trim()
            val isActive = dialogBinding.activeSwitch.isChecked

            if (name.isEmpty()) {
                dialogBinding.bannerNameEditText.error = "Name is required"
                return@setOnClickListener
            }

            if (imageUrl.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an image URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dialogBinding.progressIndicator.visibility = View.VISIBLE
            dialogBinding.saveButton.isEnabled = false

            lifecycleScope.launch {
                try {

                    val bannerData = Banner(
                        id = banner?.id ?: "",
                        name = name,
                        picUrl = imageUrl,
                        active = isActive
                    )

                    val resultFlow = if (banner == null) {
                        viewModel.addBanner(bannerData)
                    } else {
                        viewModel.updateBanner(bannerData)
                    }

                    resultFlow.collectLatest { result ->
                        if (result.isSuccess) {
                            Toast.makeText(
                                requireContext(),
                                if (banner == null) "Banner added successfully" else "Banner updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            currentDialog = null
                        } else {
                            throw result.exceptionOrNull() ?: Exception("Failed to save banner")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    dialogBinding.progressIndicator.visibility = View.GONE
                    dialogBinding.saveButton.isEnabled = true
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(banner: Banner) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Banner")
            .setMessage("Are you sure you want to delete this banner?")
            .setPositiveButton("Delete") { _, _ ->
                deleteBanner(banner)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteBanner(banner: Banner) {
        lifecycleScope.launch {
            viewModel.deleteBanner(banner.id).collectLatest { result ->
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Banner deleted successfully", Toast.LENGTH_SHORT).show()
                    if (banner.picUrl.isNotEmpty()) {
                        imageUploadHelper.deleteImage(banner.picUrl)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        result.exceptionOrNull()?.message ?: "Failed to delete banner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentDialog?.dismiss()
        currentDialog = null
        _binding = null
    }
}
