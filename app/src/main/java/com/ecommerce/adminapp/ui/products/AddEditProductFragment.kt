package com.ecommerce.adminapp.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ecommerce.adminapp.databinding.FragmentAddEditProductBinding

class AddEditProductFragment : Fragment() {
    
    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!
    
    private val productId: String? by lazy {
        arguments?.getString("productId")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (productId != null) {
            binding.textTitle.text = "Edit Product: $productId"
        } else {
            binding.textTitle.text = "Add New Product"
        }
        
        binding.textMessage.text = "Implement product add/edit form here\n\n" +
                "Features to add:\n" +
                "- Title, description fields\n" +
                "- Price, old price, rating\n" +
                "- Category selection\n" +
                "- Size and color management\n" +
                "- Image upload (multiple)\n" +
                "- Stock quantity\n" +
                "- Show in recommended toggle"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
