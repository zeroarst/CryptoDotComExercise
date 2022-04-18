package com.crypto.exercise.uitests.tests

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.crypto.exercise.R
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.kakao.text.KButton

class DemoActivityScreen(semanticsProvider: SemanticsNodeInteractionsProvider) : ComposeScreen<DemoActivityScreen>(
    semanticsProvider = semanticsProvider,
) {
    val loadCurrencyBtn = KButton { withId(R.id.bt_load_currency_list) }
    val currencyList = CurrencyListNode(this, semanticsProvider)
}