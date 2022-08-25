package com.sakhalinec.composition.domain.repository

import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.domain.entity.GameQuestion
import com.sakhalinec.composition.domain.entity.GameSettings

interface GameRepository {

    fun generateQuestion(
        // максимальное значение которое будет сгенерированно в поле сумма
        maxSumValue: Int,
        // сколько нужно генерировать вариантов ответов
        countOfOptions: Int
    ): GameQuestion

    // примает уровень игры и возвращает настройки под выбранный уровень
    fun getGameSettings(gameLevel: GameLevel): GameSettings

}