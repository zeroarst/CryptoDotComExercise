package com.crypto.exercise.data

import com.crypto.exercise.data.CurrencyInfo

object CurrencyTestData {
    val currencies = listOf(
        CurrencyInfo(
            id = "BTC",
            name = "Bitcoin",
            symbol = "BTC",
        ),
        CurrencyInfo(
            id = "ETH",
            name = "Ethereum",
            symbol = "ETH",
        ),
        CurrencyInfo(
            id = "XRP",
            name = "XRP",
            symbol = "XRP",
        ),
    )
}