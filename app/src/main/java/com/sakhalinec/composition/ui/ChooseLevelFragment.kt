package com.sakhalinec.composition.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sakhalinec.composition.R
import com.sakhalinec.composition.databinding.FragmentChooseLevelBinding
import com.sakhalinec.composition.databinding.FragmentWelcomeBinding
import com.sakhalinec.composition.domain.entity.GameLevel
import java.lang.RuntimeException

class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseLevelBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnLevelTest.setOnClickListener {
                launchGameFragment(level = GameLevel.TEST)
            }
            btnLevelEasy.setOnClickListener {
                launchGameFragment(level = GameLevel.EASY)
            }
            btnLevelNormal.setOnClickListener {
                launchGameFragment(level = GameLevel.NORMAL)
            }
            btnLevelHard.setOnClickListener {
                launchGameFragment(level = GameLevel.HARD)
            }
        }
    }

    // функция запуска игры, которая примает левел игры в параметрах
    private fun launchGameFragment(level: GameLevel) {
        // получаю ссылку на активити и вызываю фрагмент менеджер
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFragment.newInstance(level))
            .addToBackStack(GameFragment.NAME_FRAGMENT)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        // константа для передачи фрагмента ChooseLevelFragment в бекстек
        // функции launchChooseLevelFragment во фрагменте WelcomeFragment
        const val NAME = "ChooseLevelFragment"

        // фабричный метод для создания фрагмента
        fun newInstance(): ChooseLevelFragment {
            return ChooseLevelFragment()
        }

    }

}