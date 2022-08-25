package com.sakhalinec.composition.ui.viewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sakhalinec.composition.domain.entity.GameLevel
import com.sakhalinec.composition.ui.GameViewModel


// если в конструктор вью модели нужно передавать какие то параметры то,
// нужно создать viewModelFactory (фабрику вью моделей)
class GameViewModelFactory(
    // принимает параметры
    private val gameLevel: GameLevel,
    private val application: Application
): ViewModelProvider.Factory {
// наследуемся обязательно от ViewModelProvider.Factory

    // переопределяется обязательный метод, который создает вью модель
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // делается проверка, что с помощью этого метода пытаемся получить объект вью модел
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            // возвращает вью модель которую нужно создать с необходимыми параметрами
            // и обязательно приводим к типу Т
            return GameViewModel(application, gameLevel) as T
        }
        // кидаем исключение если что то пошло не так, например пытаемся вернуть вью модель другого класса
        throw RuntimeException("Unknown view model class $modelClass")
    }
}