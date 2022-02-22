package com.jokomanza.rekotlinsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.rekotlin.Action
import org.rekotlin.Subscriber
import org.rekotlin.store

data class AppState(
    val counter: Int = 0,
    val isLoading: Boolean = false
)

data class CounterActionIncrease(val unit: Unit = Unit) : Action
data class CounterActionDecrease(val unit: Unit = Unit) : Action
data class StartLoading(val unit: Unit = Unit) : Action
data class EndLoading(val unit: Unit = Unit) : Action

fun counterReducer(action: Action, state: AppState?): AppState {
    // if no state has been provided, create the default state
    var state = state ?: AppState()

    when (action) {
        is CounterActionIncrease -> {
            if (!state.isLoading) state = state.copy(counter = state.counter + 1)
        }
        is CounterActionDecrease -> {
            state = state.copy(counter = state.counter - 1)
        }
        is StartLoading -> {
            state = state.copy(isLoading = true)
        }
        is EndLoading -> {
            state = state.copy(isLoading = false)
        }
    }

    return state
}

val mainStore = store(
    reducer = ::counterReducer,
    state = null
)

class MainActivity : AppCompatActivity(), Subscriber<AppState> {

    var count = 0
        set(new: Int) {
            findViewById<TextView>(R.id.text).text = new.toString()
            field = new
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.findViewById<View>(R.id.container).setOnClickListener {
            // count++
            mainStore.dispatch(CounterActionIncrease())
        }
        this.findViewById<View>(R.id.container).setOnLongClickListener {
            if (mainStore.state.isLoading) mainStore.dispatch(EndLoading())
            else mainStore.dispatch(StartLoading())
            true
        }
    }

    override fun onStart() {
        super.onStart()
        mainStore.subscribe(this)
    }

    override fun onStop() {
        super.onStop()
        mainStore.unsubscribe(this)
    }

    @SuppressLint("SetTextI18n")
    override fun newState(state: AppState) {
        if (state.isLoading) {
            findViewById<TextView>(R.id.text).text = "Loading...."
        } else {
            findViewById<TextView>(R.id.text).text = state.counter.toString()
        }
    }
}