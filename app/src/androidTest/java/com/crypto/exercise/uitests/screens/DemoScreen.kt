package com.crypto.exercise.uitests.screens

import com.crypto.exercise.R
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton

class DemoScreen : Screen<DemoScreen>() {
    val loadCurrencyBtn = KButton { withId(R.id.bt_load_currency_list) }
}