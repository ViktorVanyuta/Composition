package com.sakhalinec.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize // аннотация освобождает от переопределения методов интерфейса Parcelable
data class GameResult (
    // победили или нет, для вывода правильного смайлика
    val winner: Boolean,
    // сколько было отвечено правильных вопросов
    val countOfRightAnswers: Int,
    // общее количество вопросов
    val countOfQuestions: Int,
    // настройки игры
    val gameSettings: GameSettings
): Parcelable // интерфейс нужен для сериализации аргументов при создании нового инстанса

