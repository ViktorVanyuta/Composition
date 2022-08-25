package com.sakhalinec.composition.domain.entity

import android.os.Parcel
import android.os.Parcelable

data class GameSettings (
    // максимальное значение
    val maxSumValue: Int,
    // минимальное количество правильных ответов для победы
    val minCountOfRightAnswers: Int,
    // минимальный процент правильных ответов
    val minPercentOfRightAnswers: Int,
    // время игры в секундах
    val gameTimeInSeconds: Int
): Parcelable // интерфейс нужен для сериализации аргументов при создании нового инстанса
{
    // конструктор в качестве параметра принимает объект Parcel и в нем вызываем первичный конструктор,
    // тут считываются все поля из объекта в том же порядке как они расположены в первичном конструкторе
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    // записываются все поля объекта в объект Parcel обязательно в том порядке в котором они
    // расположены в конструкторе
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maxSumValue)
        parcel.writeInt(minCountOfRightAnswers)
        parcel.writeInt(minPercentOfRightAnswers)
        parcel.writeInt(gameTimeInSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<GameSettings> {
        // вызывает наш конструктор в который передает объект Parcel
        override fun createFromParcel(parcel: Parcel): GameSettings {
            return GameSettings(parcel)
        }

        // создает массив объектов, в данном случае обьектов GameSettings
        override fun newArray(size: Int): Array<GameSettings?> {
            return arrayOfNulls(size)
        }
    }
}

/*
ВАЖНО ЗАПОМНИТЬ!
Процес сериализации происходит быстрее, если реализовать интерфейс Parcelable вместо Serializable.
Serializable - это интерфейс маркер(интерфейс без абстрактных методов) ничего переопределять не нужно.
Parcelable - интерфейс, в котором есть абстрактные методы, которые нужно переопределить указав
какие поля и в каком порядке нужно записывать и считывать.
 */
