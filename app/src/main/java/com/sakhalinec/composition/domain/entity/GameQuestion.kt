package com.sakhalinec.composition.domain.entity

data class GameQuestion (
    // значение суммы
    val sum: Int,
    // видимое число
    val visibleNunber: Int,
    // варианты ответов
    val options: List<Int>
){

    // значение правильного ответа
    val rightAnswer: Int
        get() = sum - visibleNunber

}
