package com.sakhalinec.composition.domain.usecases

import com.sakhalinec.composition.domain.entity.GameQuestion
import com.sakhalinec.composition.domain.repository.GameRepository

class GenerateQuestionUseCase(
    // передаем репозиторий в качестве параметра конструктору
    private val repository: GameRepository
) {

    // operator fun invoke() позволяет в любом месте программы где используем usecase делать вызов
    // класса следующим образом generateQuestionUseCase() или generateQuestionUseCase.invoke()

    // оператор invoke возвращает вопрос для игры
    operator fun invoke(maxSumValue: Int): GameQuestion {
        // для генерации вопроса передаем макс.сумму и количество вариантов ответов
        return repository.generateQuestion(maxSumValue = maxSumValue, countOfOptions = COUNT_OF_OPTIONS)
    }

    private companion object {
        // константа для количества вариантов ответов
        private const val COUNT_OF_OPTIONS = 6
    }

}