package com.goldenraven.devkitwallet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldenraven.devkitwallet.domain.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class WalletViewModel : ViewModel() {

    private var _balance: MutableLiveData<ULong> = MutableLiveData(0u)
    val balance: LiveData<ULong>
        get() = _balance

    private var _syncing: MutableLiveData<Boolean> = MutableLiveData(false)
    val syncing: LiveData<Boolean>
        get() = _syncing

    private var _unit: MutableLiveData<CurrencyUnit> = MutableLiveData(CurrencyUnit.Bitcoin)
    val unit: LiveData<CurrencyUnit>
        get() = _unit

    fun switchUnit() {
        _unit.value = when (_unit.value) {
            CurrencyUnit.Bitcoin -> CurrencyUnit.Satoshi
            CurrencyUnit.Satoshi -> CurrencyUnit.Bitcoin
            null -> CurrencyUnit.Bitcoin
        }
    }

    fun updateBalance() {
        _syncing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            Wallet.sync()
            withContext(Dispatchers.Main) {
                _balance.value = Wallet.getBalance()
                _syncing.value = false
            }
        }
    }
}

enum class CurrencyUnit {
    Bitcoin,
    Satoshi
}
