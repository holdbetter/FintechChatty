package com.holdbetter.fintechchatproject.navigation.people

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.StupidUser
import com.holdbetter.fintechchatproject.navigation.people.presenter.IPeoplePresenter
import com.holdbetter.fintechchatproject.navigation.people.presenter.PeoplePresenter
import com.holdbetter.fintechchatproject.navigation.people.view.IPeopleViewer
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository

class PeopleFragment : Fragment(R.layout.fragment_people), IPeopleViewer {
    companion object {
        fun newInstance() : PeopleFragment {
            val bundle = Bundle()
            return PeopleFragment().apply {
                arguments = bundle
            }
        }
    }

    private var presenter: IPeoplePresenter? = null
    private var userRecycler: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = PeoplePresenter(chatRepository, this)

        userRecycler = view.findViewById(R.id.users_list)
        userRecycler?.apply {
            addItemDecoration(DividerItemDecoration(view.context,
                DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(view.context,
                    R.drawable.user_divider_empty_background)!!)
            })
        }

        presenter!!.bind()
    }

    override fun onDestroyView() {
        presenter!!.unbind()
        super.onDestroyView()
    }

    override fun setUsers(users: List<StupidUser>) {
        userRecycler?.adapter = UserAdapter(users)
    }
}