package com.crypto.exercise.uitests.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.crypto.exercise.activities.DemoActivity
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
abstract class BaseTest {

    @get:Rule
    var composeTestRule = createAndroidComposeRule<DemoActivity>()

    @Before
    open fun setup() {

    }
}