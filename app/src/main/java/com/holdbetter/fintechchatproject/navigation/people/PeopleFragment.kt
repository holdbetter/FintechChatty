package com.holdbetter.fintechchatproject.navigation.people

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.navigation.people.presenter.IPeoplePresenter
import com.holdbetter.fintechchatproject.navigation.people.view.IPeopleViewer
import com.holdbetter.fintechchatproject.navigation.people.view.ShimmerPlaceholderUserListAdapter
import com.holdbetter.fintechchatproject.navigation.people.view.UserAdapter

class PeopleFragment : Fragment(R.layout.fragment_people), IPeopleViewer {
    companion object {
        fun newInstance(): PeopleFragment {
            val bundle = Bundle()
            return PeopleFragment().apply {
                arguments = bundle
            }
        }
    }

    private var presenter: IPeoplePresenter? = null
    private var userRecycler: RecyclerView? = null
    private var content: View? = null
    private var shimmer: ShimmerFrameLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        presenter = PeoplePresenter(chatRepository, this)

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
        }

        stopShimmer()
//        presenter!!.bind()
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
//        presenter!!.unbind()
        super.onDestroyView()
    }

    override fun setUsers(users: List<User>) {
        userRecycler?.adapter = UserAdapter(users)
    }
}