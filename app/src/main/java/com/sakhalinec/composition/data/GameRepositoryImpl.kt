package com.sakhalinec.composition.data

import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.domain.entity.GameQuestion
import com.sakhalinec.composition.domain.entity.GameSettings
import com.sakhalinec.composition.domain.repository.GameRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object GameRepositoryImpl: GameRepository {

    // минимальное значение суммы
    private const val MIN_SUM_VALUE = 2
    // минимальное значение видимого числа
    private const val MIN_ANSWER_VALUE = 1

    // генерация вопроса
    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): GameQuestion {
        // получаю значение суммы
        val sum = Random.nextInt(MIN_SUM_VALUE, maxSumValue + 1)
        // значение видимого числа, от единицы до суммы не включительно
        val visibleNumber = Random.nextInt(MIN_ANSWER_VALUE, sum)
        // тут лежат все варианты ответов использую HashSet, чтоб не было одинаковых значений в коллекции
        val options = HashSet<Int>()
        // получаем значение правильного ответа
        val rightAnswer = sum - visibleNumber
        // и кладем его в коллекцию с вариантами ответов
        options.add(rightAnswer)
        // генерим нижнию границу диапазона вариантов ответов
        val from = max(rightAnswer - countOfOptions, MIN_ANSWER_VALUE)
        // генерим верхнию границу диапазона вариантов ответов
        val to = min(maxSumValue, rightAnswer + countOfOptions)
        // генерим варианты ответов до тех пор пока размер коллекции не будет равен числу countOfOptions
        while (options.size < countOfOptions) {
            options.add(Random.nextInt(from, to))
        }
        // возвращаем все полученные значения
        return GameQuestion(sum = sum, visibleNunber = visibleNumber, options = options.toList())
    }

    // генерация уровней сложности игры
    override fun getGameSettings(gameLevel: GameLevel): GameSettings {
        return when (gameLevel) {
            GameLevel.TEST -> {
                GameSettings(
                    // максимальное значение суммы
                    10,
                    // минимальное колличество ответов до победы
                    3,
                    // процент правильных ответов
                    50,
                    // время игры в секундах
                    8
                )
            }
            GameLevel.EASY -> {
                GameSettings(
                    10,
                    10,
                    70,
                    80
                )
            }
            GameLevel.NORMAL -> {
                GameSettings(
                    20,
                    20,
                    80,
                    60
                )
            }
            GameLevel.HARD -> {
                GameSettings(
                    30,
                    30,
                    90,
                    45
                )
            }
        }
    }
}