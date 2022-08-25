package com.sakhalinec.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

// enum классы неявно реализуют интерфейс Serializable, но лучше использовать Parcelable
@Parcelize
enum class GameLevel: Parcelable {
    TEST, EASY, NORMAL, HARD
}
