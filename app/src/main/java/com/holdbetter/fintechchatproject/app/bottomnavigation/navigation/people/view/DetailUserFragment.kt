package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.DaggerDetailUserComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.DetailUserComponent
import com.holdbetter.fintechchatproject.databinding.FragmentUserDetailBinding
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app

class DetailUserFragment : Fragment(R.layout.fragment_user_detail) {
    companion object {
        const val USER_ID = "user"

        fun newInstance(userId: Long): DetailUserFragment {
            return DetailUserFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private val binding by viewBinding(FragmentUserDetailBinding::bind)

    lateinit var detailUserComponent: DetailUserComponent

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(app.appComponent) {
            detailUserComponent = DaggerDetailUserComponent.factory()
                .create(
                    androidDependencies = this,
                    domainDependencies = this,
                    repositoryDependencies = this,
                )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = requireArguments().getLong(USER_ID)

        binding.profileToolbar.apply {
            setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.container, DetailUserContent.newInstance(userId))
            .commitAllowingStateLoss()
    }
}