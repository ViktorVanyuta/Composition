package com.sakhalinec.composition.ui


import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sakhalinec.composition.R
import com.sakhalinec.composition.data.GameRepositoryImpl
import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.domain.entity.GameQuestion
import com.sakhalinec.composition.domain.entity.GameResult
import com.sakhalinec.composition.domain.entity.GameSettings
import com.sakhalinec.composition.domain.usecases.GenerateQuestionUseCase
import com.sakhalinec.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    // вместо контекста передается апликейшен, чтоб вью могла пережить переворот экрана, при перевороте
    // экрана активити уничтожается и сборщик мусора не сможет ее уничтожить, будет утечка памяти
    // если передать application то вью модель будет уничтожена вместе с приложением и утечки не будет
    private val application: Application,
    private val gameLevel: GameLevel
    ): ViewModel() {
    // наследуемся от обычной вью модели так как есть свой собственный конструктор
    // который принимает нужные параметры

    // получение контекста
    private val context = application

    private lateinit var gameSettings: GameSettings


    // репозиторий
    private val repository = GameRepositoryImpl

    // ссылки на юзкейсы
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null // таймер игры
    private var countOfRightAnswers = 0 // кол. правильных ответов
    private var countOfQuestions = 0 // кол. вопросов

    // лайв дата которая хранит время игры для таймера
    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    // хранит вопросы
    private val _gameQuestion = MutableLiveData<GameQuestion>()
    val gameQuestion: LiveData<GameQuestion>
        get() = _gameQuestion

    // процент правильных ответов
    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    // прогресс по правильным ответам
    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    // достаточное количество правильных ответов
    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    // достаточный процент правильных ответов
    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    // шкала процентов ответов для секондариПрогресс
    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    // результаты по завершению игры
    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult


    // паблик метод для вызова его из фрагмента
    fun startGame(gameLevel: GameLevel) {
        getGameSettings(gameLevel)
        startTimer()
        generateQuestion()
        updateProgress()
    }

    // функция отвечающаая за отвенты игрока
    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    // обновление прогресса
    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            // получение строки прогресса из ресурсов
            context.resources.getString(R.string.progress_answers),
            // кол. правильных ответов
            countOfRightAnswers,
            // минимальное количество правильных ответов до победы
            gameSettings.minCountOfRightAnswers
        )
        // кладу в лайв дату кол. правильных ответов
        _enoughCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        // кладу в лайв дату процент правильных ответов
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    // вычисление процента правильных ответов
    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    // проверка правильного ответа
    private fun checkAnswer(number: Int) {
        val rightAnswer = gameQuestion.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++ // если ответ правильный увеличиваем кол. правильных ответов
        }
        countOfQuestions++ // в любом случае увеличиваем кол. вопросов
    }

    // получаю настройки игры
    private fun getGameSettings(gameLevel: GameLevel) {
        this.gameSettings = getGameSettingsUseCase(gameLevel)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    // генерация новых вопросов (примеров)
    private fun generateQuestion() {
        _gameQuestion.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    // старт тиймера игры
    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }

        }
        timer?.start()
    }

    // форматируе время
    private fun formatTime(millisUntilFinished: Long): String {
        // общее количество секунд
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        // количество минут
        val minutes = seconds / SECONDS_IN_MINUTES
        // оставшееся количество секунд
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        // "%02d:%02d" эта запись делает следующее, если число слева и справа содержит меньше 2х
        // символов, то они будут дополнены нулями (если время 2:15, будет 02:15 аналогично с секундами)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    // отмена таймера, когда игра закончена и мы уходим с фрагмента
    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    // завершение игры
    private fun finishGame() {
        _gameResult.value = GameResult(
            // победили только когда достаточное количество правильных ответои и процент ответов
            winner = enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
    }


    companion object {

        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60

    }

}