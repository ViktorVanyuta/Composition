package com.sakhalinec.composition.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sakhalinec.composition.R
import com.sakhalinec.composition.databinding.FragmentGameBinding
import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.domain.entity.GameResult
import com.sakhalinec.composition.ui.viewModelFactory.GameViewModelFactory
import java.lang.RuntimeException

class GameFragment : Fragment() {

    // хранит уровень игры
    private lateinit var gameLevel: GameLevel

    // вью модель через ленивую инициализацию, то есть будет инициализирована в момент первого
    // обращения к ней
//    private val viewModel: GameViewModel by lazy {
//        ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//        )[GameViewModel::class.java]
//    }

    // вью модель через ленивую инициализацию
//    private val viewModel by lazy {
//        ViewModelProvider(
//            this,
//            // создается фабрика вью модели, куда передаются необходимые параметры
//            GameViewModelFactory(
//                // параметр gameLevel
//                gameLevel,
//                // а так же application
//                requireActivity().application
//            )
//        )[ExampleGameViewModelFactory::class.java]
//    }

    // аналог кода выше
    private val viewModelFactory by lazy {
        GameViewModelFactory(gameLevel = gameLevel, application = requireActivity().application)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }


    // колекция вьюшек для вариантов ответов
    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    /* использую ViewBinding для того чтобы не вызывать постоянно findViewById, создается класс
    binding который автоматически генерируется и хранит ссылки на все id элементов view.  */
    // для избежания проблем, _binding делается нулабельным на тот случай если обратиться к данной
    // переменной до метода onCreateView или после onDestroyView то поймаю исключение
    private var _binding: FragmentGameBinding? = null

    // чтобы не создавать постоянные проверки на null, создается не нулабельная переменная binding
    // у которой переопределяется геттер
    private val binding: FragmentGameBinding
        // геттер возвращает _binding если оно не равно null, а если оно равно null то кидает исключение
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // у сгенерированного класса вызывается метод inflate с параметрами и в onCreateView
        // возвращается root из объекта binding
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setClickListenersToOptional()

        // вызов startGame не нужен так как игра будет запущена из вью модели
        // viewModel.startGame(gameLevel = gameLevel)


        /* // обычный способ инициализации вью модели
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]*/
    }

    // установка слушателей клика на вью с вариантами ответов
    private fun setClickListenersToOptional() {
        for (tvOption in tvOptions) {
            tvOption.setOnClickListener{
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

    // подписка на вьюшки
    private fun observeViewModel() {
        // установка вопроса
        viewModel.gameQuestion.observe(viewLifecycleOwner){
            // установка суммы
            binding.tvSum.text = it.sum.toString()
            // установка известного значения
            binding.tvLeftNumber.text = it.visibleNunber.toString()
            // установка всех вариантов ответов
            for (i in 0 until tvOptions.size) {
                tvOptions[i].text = it.options[i].toString()
            }
        }
        // процент правильных ответов
        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner){
            binding.progressBar.setProgress(it, true)
        }
        // состояние количества ответов
        viewModel.enoughCount.observe(viewLifecycleOwner){
            binding.tvAnswersProgress.setTextColor(getColorByState(it))
        }
        // состояние процентов ответов
        viewModel.enoughPercent.observe(viewLifecycleOwner){
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }
        // прогрес ответов
        viewModel.progressAnswers.observe(viewLifecycleOwner){
            binding.tvAnswersProgress.text = it
        }
        // установка времени
        viewModel.formattedTime.observe(viewLifecycleOwner){
            binding.tvTimer.text = it
        }
        // минимальный процент правильных ответов
        viewModel.minPercent.observe(viewLifecycleOwner){
            binding.progressBar.secondaryProgress = it
        }
        // результат игры
        viewModel.gameResult.observe(viewLifecycleOwner){
            launchGameFinishedFragment(it)
        }


    }

    // состояние цвета
    private fun getColorByState(goodState: Boolean): Int {
        // если правильных ответов достаточное количество то, установим зеленый цвет иначе красный
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    // из аргументов получим парселабле объект он нулабельный и если он не равен null, то присвоим
    // значение переменной level
    private fun parseArgs() {
        requireArguments().getParcelable<GameLevel>(KEY_LEVEL)?.let {
            gameLevel = it
        }
    }

    // функция перехода на фрагмент GameFinishedFragment и предаем в него объект GameResult
    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult = gameResult))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // присваиваю null объекту _binding для того чтоб нельзя было к нему обратиться после
        // этого метода по цепочке жизненного цикла
        _binding = null
    }

    companion object {

        // константа для добавления в бекстек и вызова перехода к фрагменту
        const val NAME_FRAGMENT = " GameFragment "
        private const val KEY_LEVEL = " level "

        // фабричный метод для создания фрагмента который принимает уровень в качестве параметра
        fun newInstance(level: GameLevel): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    // в аргументы кладется сериализуемый объект
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }

    }

}