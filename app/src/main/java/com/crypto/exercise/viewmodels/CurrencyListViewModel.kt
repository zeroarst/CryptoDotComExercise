package com.crypto.exercise.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.crypto.exercise.data.AppDatabase
import com.crypto.exercise.data.CurrencyInfo
import com.crypto.exercise.data.CurrencyInfoRepository
import com.crypto.exercise.data.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyListViewModel(
    application: Application,
) : ViewModel() {

    private val currencyInfoRepository: CurrencyInfoRepository

    init {
        val db = AppDatabase.getInstance(application)
        val dao = db.currencyInfoDao()
        currencyInfoRepository = CurrencyInfoRepository(dao)
    }

    val currencies: LiveData<List<CurrencyInfo>> = MutableLiveData()
    val currenciesSortDirection: LiveData<Order> = MutableLiveData()
    val uiState: LiveData<UIState> = MutableLiveData()

    enum class UIState {
        LOADING, SHOW_DATA,
    }

    fun loadData(sorting: Boolean = false) {
        if (sorting) {
            currenciesSortDirection as MutableLiveData
            currenciesSortDirection.value = when (currenciesSortDirection.value) {
                Order.ASC -> Order.DESC
                Order.DESC, null -> Order.ASC
            }
        }
        uiState as MutableLiveData
        uiState.value = UIState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000) // pretend taking some time to load.
            currencies as MutableLiveData
            currencies.postValue(currencyInfoRepository.getCurrencies(currenciesSortDirection.value))
            uiState.postValue(UIState.SHOW_DATA)
        }
    }
}

class CurrencyListViewModelFactory(private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CurrencyListViewModel(application) as T
}
