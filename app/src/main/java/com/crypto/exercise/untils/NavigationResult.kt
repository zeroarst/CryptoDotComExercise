package com.crypto.exercise.untils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NavigationResult<T>(
    val key: String,
) {
    fun listenBy(listenerFragment: Fragment) =
        listenerFragment.findNavController().currentBackStackEntry?.run {
            savedStateHandle.getLiveData<T>(getUniqueKey(this.id))
        }

    fun setResult(fragment: Fragment, result: T) {
        fragment.findNavController().previousBackStackEntry?.apply {
            savedStateHandle.set(getUniqueKey(this.id), result)
        }
    }

    private fun getUniqueKey(backStackEntryId: String) = "${backStackEntryId}_$key"
}