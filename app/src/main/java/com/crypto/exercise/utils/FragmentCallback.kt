package com.crypto.exercise.utils

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner

/**
 * D: Result data type.
 * R: Response data type.
 * C: call back function type
 * @param requestKey the request key will be combined with fragment tag for [FragmentManager.putFragment] and [FragmentManager.setFragmentResult].
 */
abstract class BaseFragmentCallback<F : Fragment, D, R, C : Function<R>>(private val requestKey: String) {

    /**
     * Bundles the result data into bundle.
     */
    private fun bundleResult(data: D?): Bundle {
        return bundleOf().apply { data?.let { put(DATA_KEY, data) } }
    }

    /**
     * Converts the
     */
    protected fun parseResult(bundle: Bundle): D {
        return bundle.get(DATA_KEY) as D
    }

    protected fun bundleReceiverResponse(data: R?): Bundle {
        return bundleOf().apply { data?.let { put(DATA_KEY_RECEIVER_RESPONSE, data) } }
    }

    private fun parseReceiverResponse(bundle: Bundle): R {
        return bundle.get(DATA_KEY_RECEIVER_RESPONSE) as R
    }

    fun listen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        genericCallback: C
    ) {
        nativeListen(fragmentManager, lifecycleOwner, mapOf("" to genericCallback))
    }

    fun listen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        targetFragmentTags: List<String>,
        callback: C,
    ) {
        nativeListen(fragmentManager, lifecycleOwner, targetFragmentTags.associateWith { callback })
    }

    fun listen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        vararg targetFragmentTagAndCallbackPairs: Pair<String, C>,
    ) {
        nativeListen(fragmentManager, lifecycleOwner, mapOf(*targetFragmentTagAndCallbackPairs))
    }

    fun listen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        genericCallback: C,
        vararg targetFragmentTagAndCallbackMap: Pair<String, C>
    ) {
        nativeListen(fragmentManager, lifecycleOwner, mapOf("" to genericCallback, *targetFragmentTagAndCallbackMap))
    }

    abstract fun nativeListen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        targetFragmentTagAndCallbackMap: Map<String, C>,
    )

    protected fun actualListen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        targetFragmentTagAndCallbackPairs: Map<String, C>? = null,
        onResult: (targetFragmentTagAndCallback: Map.Entry<String, C>, requestKey: String, result: Bundle) -> Unit
    ) {
        targetFragmentTagAndCallbackPairs?.forEach { entry ->
            fragmentManager.setFragmentResultListener(generateUniqueRequestKey(entry.key), lifecycleOwner) { requestKey, result ->
                onResult(entry, requestKey, result)
            }
        }
    }

    /**
     * @param listenerCallback the listener's response to callback. This is for callback fragment that requires response
     * from the listener.
     */
    protected fun actualCallBack(callbackFragment: F, callbackData: D? = null, listenerCallback: ((R) -> Unit)? = null) {
        val bundledResult = bundleResult(callbackData)

        listenerCallback?.let {
            // for those listeners that do not specify fragment tag.
            callbackFragment.parentFragmentManager.setFragmentResultListener(
                generateUniqueListenerCallbackKey(""), callbackFragment.viewLifecycleOwner
            ) { _, result ->
                it.invoke(parseReceiverResponse(result))
            }
            // for those listeners that specify fragment tag.
            callbackFragment.parentFragmentManager.setFragmentResultListener(
                generateUniqueListenerCallbackKey(callbackFragment.tag), callbackFragment.viewLifecycleOwner
            ) { _, result ->
                it.invoke(parseReceiverResponse(result))
            }
        }

        // for those listeners that do not specify fragment tag.
        callbackFragment.parentFragmentManager.putFragment(bundledResult, requestKey, callbackFragment)
        callbackFragment.setFragmentResult(requestKey, bundledResult)

        // for those listeners that specify fragment tag.
        val uniqueRequestKey = generateUniqueRequestKey(callbackFragment.tag)
        callbackFragment.parentFragmentManager.putFragment(bundledResult, uniqueRequestKey, callbackFragment)
        callbackFragment.setFragmentResult(uniqueRequestKey, bundledResult)
    }

    protected fun generateUniqueRequestKey(fragmentTag: String?) =
        "${if (fragmentTag.isNullOrEmpty()) "" else "${fragmentTag}_"}${requestKey}"

    protected fun generateUniqueListenerCallbackKey(fragmentTag: String?) =
        "${if (fragmentTag.isNullOrEmpty()) "" else "${fragmentTag}_"}${requestKey}_LISTENER_CALLBACK"

    companion object {
        const val DATA_KEY = "DATA_KEY"
        const val DATA_KEY_RECEIVER_RESPONSE = "DATA_KEY_RECEIVER_RESPONSE"
    }
}


class FragmentCallback<F : Fragment>(requestKey: String) : BaseFragmentCallback<F, Unit?, Unit?, (F) -> Unit>(requestKey) {
    override fun nativeListen(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        targetFragmentTagAndCallbackMap: Map<String, (f: F) -> Unit>,
    ) {
        this.actualListen(
            fragmentManager,
            lifecycleOwner,
            targetFragmentTagAndCallbackMap
        ) { targetFragmentTagAndCallback, requestKey, result ->
            targetFragmentTagAndCallback.value.invoke(
                fragmentManager.getFragment(result, requestKey) as F,
            )
        }
    }

    fun callBack(callbackFragment: F) {
        this.actualCallBack(callbackFragment)
    }

    class WithDataReceiveResponse<F : Fragment, D, R>(requestKey: String) :
        BaseFragmentCallback<F, D, R, (F, D) -> R>(requestKey) {
        override fun nativeListen(
            fragmentManager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            targetFragmentTagAndCallbackMap: Map<String, (f: F, callbackData: D) -> R>,
        ) {
            this.actualListen(
                fragmentManager,
                lifecycleOwner,
                targetFragmentTagAndCallbackMap
            ) { targetFragmentTagAndCallback, requestKey, callbackData ->
                val receiverResponse = targetFragmentTagAndCallback.value.invoke(
                    fragmentManager.getFragment(callbackData, requestKey) as F,
                    parseResult(callbackData)
                )
                fragmentManager.setFragmentResult(
                    generateUniqueListenerCallbackKey(targetFragmentTagAndCallback.key),
                    bundleReceiverResponse(receiverResponse)
                )
            }
        }

        fun callBack(callbackFragment: F, data: D, onReceiverResponse: (R) -> Unit) {
            this.actualCallBack(callbackFragment = callbackFragment, callbackData = data, listenerCallback = onReceiverResponse)
        }
    }

    class WithData<F : Fragment, D>(requestKey: String) : BaseFragmentCallback<F, D, Unit, (F, D) -> Unit>(requestKey) {
        override fun nativeListen(
            fragmentManager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            targetFragmentTagAndCallbackMap: Map<String, (f: F, callbackData: D) -> Unit>
        ) {
            this.actualListen(
                fragmentManager,
                lifecycleOwner,
                targetFragmentTagAndCallbackMap
            ) { targetFragmentTagAndCallback, requestKey, result ->
                targetFragmentTagAndCallback.value.invoke(
                    fragmentManager.getFragment(result, requestKey) as F,
                    parseResult(result)
                )
            }
        }

        fun callBack(callbackFragment: F, data: D) {
            this.actualCallBack(callbackFragment = callbackFragment, callbackData = data)
        }
    }

    class ReceiveResponse<F : Fragment, R>(requestKey: String) : BaseFragmentCallback<F, Unit?, R, (F) -> R>(requestKey) {
        override fun nativeListen(
            fragmentManager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            targetFragmentTagAndCallbackMap: Map<String, (f: F) -> R>
        ) {
            this.actualListen(
                fragmentManager,
                lifecycleOwner,
                targetFragmentTagAndCallbackMap
            ) { targetFragmentTagAndCallback, requestKey, result ->
                val receiverResponse = targetFragmentTagAndCallback.value.invoke(
                    fragmentManager.getFragment(result, requestKey) as F,
                )
                fragmentManager.setFragmentResult(
                    generateUniqueListenerCallbackKey(targetFragmentTagAndCallback.key),
                    bundleReceiverResponse(receiverResponse)
                )
            }
        }

        fun callBack(callbackFragment: F, onReceiverResponse: (R) -> Unit) {
            this.actualCallBack(callbackFragment = callbackFragment, listenerCallback = onReceiverResponse)
        }
    }

}


