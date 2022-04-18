package com.crypto.exercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.crypto.exercise.activities.delegations.argument
import com.crypto.exercise.data.CurrencyInfo
import com.crypto.exercise.data.CurrencyTestData
import com.crypto.exercise.ui.theme.CryptoDotComExerciseTheme
import com.crypto.exercise.ui.theme.Typography
import com.crypto.exercise.untils.FragmentCallback

class CurrencyListFragment : Fragment() {

    private var currencyList: ArrayList<CurrencyInfo> by argument()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CryptoDotComExerciseTheme {
                    CurrentListContent(currencyList) {
                        Callback.onClickItem.callBack(this@CurrencyListFragment, it)
                    }
                }
            }
        }
    }

    object Callback {
        val onClickItem = FragmentCallback.WithData<CurrencyListFragment, CurrencyInfo>("onClickItem")
    }

    companion object {
        fun newInstance(currencies: ArrayList<CurrencyInfo>): CurrencyListFragment {
            return CurrencyListFragment().apply {
                this.currencyList = currencies
            }
        }
    }
}

@Composable
fun CurrentListContent(currencies: List<CurrencyInfo>, onClickItem: (CurrencyInfo) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .testTag("currencyList"),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(
            items = currencies,
            key = { it.id },
            itemContent = { it ->
                CryptoListItem(currencyInfo = it) {
                    onClickItem(it)
                }
                Divider(color = Color.Gray)
            }
        )
    }
}

@Composable
fun CryptoListItem(currencyInfo: CurrencyInfo, onClickItem: (CurrencyInfo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("currencyListItemRow")
            .height(48.dp)
            .clickable {
                onClickItem(currencyInfo)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = currencyInfo.name, style = Typography.h6)
        Text(text = currencyInfo.symbol, style = Typography.body1)
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentListContentPreview() {
    CryptoDotComExerciseTheme(true) {
        val currencies = CurrencyTestData.currencies
        CurrentListContent(currencies) {

        }
    }
}