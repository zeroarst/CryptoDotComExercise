package com.crypto.exercise.activities

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.crypto.exercise.R
import com.crypto.exercise.activities.delegations.viewBinding
import com.crypto.exercise.databinding.ActivityDemoBinding
import com.crypto.exercise.fragments.CurrencyListFragment
import com.crypto.exercise.viewmodels.CurrencyListViewModel
import com.crypto.exercise.viewmodels.CurrencyListViewModelFactory

class DemoActivity : AppCompatActivity() {

    private val viewModel: CurrencyListViewModel by viewModels { CurrencyListViewModelFactory(application) }
    private val binding: ActivityDemoBinding by viewBinding(ActivityDemoBinding::inflate)
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupData()
        setupCallbacks()
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        toast = null
    }

    private fun setupData() {
        viewModel.currencies.observe(this) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fcv_currency_list, CurrencyListFragment.newInstance(ArrayList(it)))
                .commit()
        }
        viewModel.uiState.observe(this) {
            binding.cpi.isVisible = it == CurrencyListViewModel.UIState.LOADING
            when (it) {
                CurrencyListViewModel.UIState.LOADING -> {
                    binding.cpi.isVisible = true
                }
                CurrencyListViewModel.UIState.SHOW_DATA -> {
                    binding.cpi.isVisible = false
                }
                else -> {}
            }
        }
    }

    private fun setupCallbacks() {
        CurrencyListFragment.Callback.onClickItem.listen(supportFragmentManager, this) { f, data ->
            toast?.cancel()
            toast = Toast.makeText(this, data.name, Toast.LENGTH_SHORT).apply { show() }
        }
    }

    private fun setupViews() {
        binding.btLoadCurrencyList.setOnClickListener {
            viewModel.loadData()
        }
        binding.btSorting.clickWithDebounce {
            viewModel.loadData(true)
        }
    }
}

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}