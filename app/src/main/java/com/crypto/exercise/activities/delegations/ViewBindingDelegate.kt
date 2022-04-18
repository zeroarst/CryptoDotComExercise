package com.crypto.exercise.activities.delegations

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding> constructor(
    val fragment: Fragment,
    private val viewBindingFactory: ((view: View) -> T)?,
) : ReadOnlyProperty<Fragment, T> {
    
    internal var binding: T? = null
    
    init {
        
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            // override fun onCreate(owner: LifecycleOwner) {
            //     fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
            //         viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            //             override fun onDestroy(owner: LifecycleOwner) {
            //                 binding = null
            //             }
            //         })
            //     }
            // }
            
            /* We replaced above with below because it fixes the issue if we want to access view binding in onDestroyView
               (ex. clear RecycleView's adapter, refer to PlaylistFragment) and above is called, which happens before
               Fragment's onDestroyView get called, so we will get error in Fragment's onDestroyView.
            */
            override fun onDestroy(owner: LifecycleOwner) {
                binding = null
            }
        })
    }
    
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }
        
        // TODO below is not true. we cannot access view in onCreateDialog.
        // TODO don't why this is needed. It is causing error if accessing binding in DialogFragment's onCreateDialog.
        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }
        if (thisRef.view == null)
            throw Exception("cannot bind view. Check if passing content layout id to Fragment constructor and make sure not accessing this before onViewCreated.")
        return when {
            viewBindingFactory != null -> viewBindingFactory.invoke(thisRef.requireView())
                .also { this.binding = it }
            else -> throw Exception("viewBindingFactory cannot be null")
        }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (view: View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (inflater: LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }