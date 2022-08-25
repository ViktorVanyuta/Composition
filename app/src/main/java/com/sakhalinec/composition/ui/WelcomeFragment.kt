package com.sakhalinec.composition.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sakhalinec.composition.R
import com.sakhalinec.composition.databinding.FragmentWelcomeBinding
import java.lang.RuntimeException

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding: FragmentWelcomeBinding
        get() = _binding ?: throw RuntimeException( "FragmentWelcomeBinding == null" )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonUnderstand.setOnClickListener {
            launchChooseLevelFragment()
        }

    }

    // функция перехода на фрагмент ChooseLevelFragment
    private fun launchChooseLevelFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ChooseLevelFragment.newInstance())
            // в бекстек передан фрагмент для возврата к нему при клике на кнопку назад
            .addToBackStack(ChooseLevelFragment.NAME)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}