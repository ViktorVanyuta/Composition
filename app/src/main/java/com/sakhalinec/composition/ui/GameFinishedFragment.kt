package com.sakhalinec.composition.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sakhalinec.composition.R
import com.sakhalinec.composition.databinding.FragmentChooseLevelBinding
import com.sakhalinec.composition.databinding.FragmentGameFinishedBinding
import com.sakhalinec.composition.domain.entity.GameResult
import java.lang.RuntimeException

class GameFinishedFragment : Fragment() {

    // хранит результат игры
    private lateinit var gameResult: GameResult

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        bindViews()


    }

    // слушатель клика на кнопку назад
    private fun setupClickListeners() {
        // получим ссылку на активити и у нее берем ссылку на onBackPressedDispatcher и добавляем
        // слушатель на кнопку НАЗАД, когда находясь в активити будет нажата кнопка НАЗАД
        // сработает колбек addCallback и вызовится метод retryGame() который вернет к экрану выбора уровня
        // в перегруженном методе addCallback нужен viewLifecycleOwner для того, чтобы после того
        // как сработала функция retryGame ссылка на объект была удалена сборщиком мусора и слушатель
        // так же будет удален, это поможет избежать утечек памяти и крашей
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryGame()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    // биндим все необходимые значения во вьюшки
    private fun bindViews() {
        with(binding) {
            // установка картинки (смайлика)
            emojiResult.setImageResource(getSmileResId())
            // установка значений при помощи String.format
            tvRequiredAnswers.text = String.format(
                // получаю строку из ресурсов
                getString(R.string.required_score),
                // получаю строку из gameResult.gameSettings
                gameResult.gameSettings.minCountOfRightAnswers
            )
            tvScoreAnswers.text = String.format(
                getString(R.string.score_answers),
                gameResult.countOfRightAnswers
            )
            tvRequiredPercentage.text = String.format(
                getString(R.string.required_percentage),
                gameResult.gameSettings.minPercentOfRightAnswers
            )
            tvScorePercentage.text = String.format(
                getString(R.string.score_percentage),
                getPercentOfRightAnswers()
            )
        }
    }

    // простая проверка, если победили то картинка с веселым смайликом, если нет то грыстный
    private fun getSmileResId(): Int {
        return if (gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }

    // возвращает процент правильных ответов
    private fun getPercentOfRightAnswers() = with(gameResult) {
        // если количество вопросов равно 0 то вернуть 0
        if (countOfQuestions == 0) {
            0
        } else {
            // делим количество правильных ответов на количество вопросов и умножаем на 100
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    // из аргументов получим парселабле объект он нулабельный и если он не равен null, то присвоим
    // значение переменной gameResult
    private fun parseArgs() {
        requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    // функция возврата к экрану выбора уровня
    private fun retryGame() {
        requireActivity().supportFragmentManager
            // переход к фрагменту GameFragment, но он будет удален из стека это нужно на тот
            // случай если в какой то момент времени появится еще один фрагмент перед GameFragment,
            // поэтому всегда будет переход к фрагменту который находится перед фрагментом GameFragment
            .popBackStack(GameFragment.NAME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        private const val KEY_GAME_RESULT = "game_result"

        // фабричный метод для создания фрагмента и передаем в него GameResult
        fun newInstance(gameResult: GameResult): GameFinishedFragment {
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
        }

    }

}