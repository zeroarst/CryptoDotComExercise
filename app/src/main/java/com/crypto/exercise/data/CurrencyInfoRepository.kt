package com.crypto.exercise.data

class CurrencyInfoRepository(
    private val currencyInfoDao: CurrencyInfoDao
) {

    fun getCurrencies(order: Order? = null) = currencyInfoDao.getCurrencyListOptionalOrderBy(order)

}