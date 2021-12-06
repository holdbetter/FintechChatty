package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.PeopleEffect
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.PeopleEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.PeopleState
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.PeopleStore
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.DetailUserFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.IPeopleViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.ShimmerPlaceholderUserListAdapter
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.UserAdapter
import com.holdbetter.fintechchatproject.databinding.FragmentPeopleBinding
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.FragmentExtensions.createStyledSnackbar
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.io.IOException
import javax.inject.Inject

class PeopleFragment :
    ElmFragment<PeopleEvent, PeopleEffect, PeopleState>(R.layout.fragment_people), IPeopleViewer {
    companion object {
        fun newInstance(): PeopleFragment {
            val bundle = Bundle()
            return PeopleFragment().apply {
                arguments = bundle
            }
        }
    }

    private val binding by viewBinding(FragmentPeopleBinding::bind)

    @Inject
    lateinit var peopleStore: PeopleStore

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app.appComponent.peopleComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            shimmerContent.apply {
                adapter = ShimmerPlaceholderUserListAdapter(this.context)
            }

            usersList.apply {
                addItemDecoration(DividerItemDecoration(
                    view.context,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(
                        ContextCompat.getDrawable(
                            view.context,
                            R.drawable.user_divider_empty_background
                        )!!
                    )
                })
                adapter = UserAdapter(::navigateToUser)
            }
        }

        if (store.currentState.users == null) {
            store.accept(PeopleEvent.Ui.Started)
        }
    }

    override val initEvent: PeopleEvent
        get() {
            return PeopleEvent.Ui.Init
        }

    override fun createStore(): Store<PeopleEvent, PeopleEffect, PeopleState> {
        return peopleStore.provide()
    }

    override fun render(state: PeopleState) {
        shimming(state.isLoading)
        state.users?.let(::setUsers)
    }

    override fun shimming(turnOn: Boolean) {
        with(binding) {
            shimmer.isVisible = turnOn
            if (turnOn) shimmer.startShimmer() else shimmer.stopShimmer()
            usersList.isVisible = !turnOn
        }
    }

    override fun handleEffect(effect: PeopleEffect): Unit {
        return when (effect) {
            is PeopleEffect.NavigateToUser -> {
            }
            is PeopleEffect.ShowError -> handleError(effect.error)
        }
    }

    private fun handleError(error: Throwable) {
        val snackbar = createStyledSnackbar()
        when (error) {
            is UnexpectedRoomException -> {
                snackbar.setText(R.string.unexpeteced_room_exception)
            }
            is IOException, is NotConnectedException -> {
                snackbar.setText(R.string.no_connection)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.try_again) { store.accept(PeopleEvent.Ui.Retry) }
            }
            else -> {
                snackbar.setText(R.string.undefined_error_message)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.reload) { store.accept(PeopleEvent.Ui.Retry) }
            }
        }

        snackbar.show()
    }

    private fun navigateToUser(
        context: Context,
        user: User,
    ) {
        val mainActivity = context as MainActivity
        val detailUserFragment = DetailUserFragment.newInstance(user.id)

        mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_host_fragment, detailUserFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun setUsers(users: List<User>) {
        (binding.usersList.adapter as UserAdapter).submitList(users)
    }
}