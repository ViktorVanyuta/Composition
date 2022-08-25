package com.sakhalinec.composition.domain.usecases

import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.domain.entity.GameSettings
import com.sakhalinec.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    // передаем репозиторий в качестве параметра конструктору
    private val repository: GameRepository
) {

    // принимает уровень игры и возвращает настройки игры
    operator fun invoke(gameLevel: GameLevel): GameSettings {
        return repository.getGameSettings(gameLevel = gameLevel)
    }

}