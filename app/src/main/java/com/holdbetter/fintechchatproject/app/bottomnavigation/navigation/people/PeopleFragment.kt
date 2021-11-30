package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.IPeopleViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.ShimmerPlaceholderUserListAdapter
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.UserAdapter
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.viewmodel.PeopleViewModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.viewmodel.PeopleViewModelFactory
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class PeopleFragment : Fragment(R.layout.fragment_people), IPeopleViewer {
    companion object {
        fun newInstance(): PeopleFragment {
            val bundle = Bundle()
            return PeopleFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var peopleViewModelFactory: PeopleViewModelFactory
    private val viewModel: PeopleViewModel by activityViewModels {
        peopleViewModelFactory
    }

    private val compositeDisposable = CompositeDisposable()

    private var userRecycler: RecyclerView? = null
    private var content: View? = null
    private var shimmer: ShimmerFrameLayout? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.peopleComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        content = view.findViewById(R.id.people_content)
        shimmer = view.findViewById(R.id.shimmer)

        val shimmerContent = view.findViewById<ListView>(R.id.shimmer_content).apply {
            adapter = ShimmerPlaceholderUserListAdapter(this.context)
        }

        userRecycler = view.findViewById(R.id.users_list)
        userRecycler?.apply {
            addItemDecoration(DividerItemDecoration(view.context,
                DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(view.context,
                    R.drawable.user_divider_empty_background)!!)
            })
            adapter = UserAdapter()
        }

        this.bind()
    }

    override fun bind() {
        startShimmer()
        viewModel.getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { stopShimmer() }
            .subscribeBy(
                onSuccess = ::setUsers
            ).addTo(compositeDisposable)
    }

    override fun startShimmer() {
        content!!.isVisible = false
        shimmer!!.isVisible = true
        shimmer!!.startShimmer()
    }

    override fun stopShimmer() {
        shimmer!!.stopShimmer()
        shimmer!!.isVisible = false
        content!!.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.unbind()
    }

    override fun unbind() {
        compositeDisposable.clear()
    }

    override fun setUsers(users: List<User>) {
        (userRecycler!!.adapter as UserAdapter).submitList(users)
    }
}