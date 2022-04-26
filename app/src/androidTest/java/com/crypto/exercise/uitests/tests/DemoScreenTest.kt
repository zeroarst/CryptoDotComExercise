package com.crypto.exercise.uitests.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoScreenTest : BaseTest() {

    @Test
    fun demoScreen_clickShowCurrencyListButton_ClickableItemIsVisible() {
        onComposeScreen<DemoActivityScreen>(composeTestRule) {
            loadCurrencyBtn {
                click()
            }
            currencyList {
                currencyListItem {
                    assertIsDisplayed()
                }
            }
        }
    }
}