package com.crypto.exercise.uitests.tests

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.builder.NodeMatcher
import io.github.kakaocup.compose.node.core.BaseNode
import io.github.kakaocup.compose.node.element.KNode

class CurrencyListNode(parentNode: BaseNode<*>, semanticsProvider: SemanticsNodeInteractionsProvider) :
    BaseNode<CurrencyListNode>(
        semanticsProvider = semanticsProvider,
        parentNode = parentNode,
        nodeMatcher = NodeMatcher(hasTestTag("currencyList"))
    ) {
    val currencyListItemRow = KNode(
        parentNode = this,
        semanticsProvider = semanticsProvider,
        nodeMatcher = NodeMatcher(hasTestTag("currencyListItemRow") and hasClickAction())
    )
}