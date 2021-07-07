package natec.androidapp.masterpomodoro.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import natec.androidapp.masterpomodoro.data.repositories.AddTimerRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class AddTimerViewModelFactory @Inject constructor(
    private val repository: AddTimerRepository
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddTimerViewModel(repository) as T
    }
}