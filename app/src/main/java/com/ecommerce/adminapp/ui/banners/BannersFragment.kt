package com.ecommerce.adminapp.ui.banners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ecommerce.adminapp.databinding.FragmentBannersBinding

class BannersFragment : Fragment() {
    
    private var _binding: FragmentBannersBinding? = null
    private val binding get() = _binding!!
    
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
        
        binding.textBanners.text = "Banners Management\n\nImplement banners list here"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
