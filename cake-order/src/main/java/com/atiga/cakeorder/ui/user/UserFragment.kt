package com.atiga.cakeorder.ui.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.UserFragmentBinding
import com.atiga.cakeorder.ui.user.add.AddUserActivity
import com.atiga.cakeorder.ui.user.detail.DetailUserActivity
import com.atiga.cakeorder.util.SessionManager
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class UserFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModel()
    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val userAdapter = UserAdapter()
    private var users = mutableListOf<User>()

    companion object {
        const val ADD_USER = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserFragmentBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        context?.let {  sessionManager = SessionManager(it) }

        fetchData()

        binding.fabAddUser.setOnClickListener {
            startActivityForResult(Intent(activity, AddUserActivity::class.java), ADD_USER)
        }

        userViewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                showMessage(it)
            }
        })

        // refresh token when session has ended
        userViewModel.refreshToken.observe(viewLifecycleOwner, {
            it?.let {
                sessionManager.saveToPreference("token", it.token)
                fetchData()
            }
        })

        userAdapter.onItemClick = {
            startActivity(Intent(activity, DetailUserActivity::class.java).putExtra("data", it))
        }
    }

    private fun fetchData(){
        val token = sessionManager.getFromPreference(SessionManager.TOKEN)

        lifecycleScope.launch {
            token?.let { userViewModel.getAllUser(it) }
        }

        userViewModel.userData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS -> {

                        users.clear()

                        it.data?.map { result ->
                            users.add(result)
                        }

                        userAdapter.setData(users)

                        with(binding.rvUser){
                            layoutManager = GridLayoutManager(context, 2)
                            setHasFixedSize(true)
                            adapter = userAdapter
                        }

                        userAdapter.notifyItemInserted(users.size-1)
                        isLoading(false)

                        if(users.size < 1){
                            binding.imgOrderEmpty.visibility = View.VISIBLE
                            binding.tvOrderEmpty.visibility = View.VISIBLE
                        } else {
                            binding.imgOrderEmpty.visibility = View.GONE
                            binding.tvOrderEmpty.visibility = View.GONE
                        }
                    }
                    Status.ERROR -> {
                        isLoading(false)
//                        showMessage(getString(R.string.session_end))
//                        sessionManager.logout()
//                        startActivity(Intent(activity, LoginActivity::class.java))
//                        activity?.finish()
                        val token = sessionManager.getFromPreference(SessionManager.TOKEN)
                        token?.let {
                            lifecycleScope.launch {
                                userViewModel.refreshToken(it)
                            }
                        }
                    }
                    Status.LOADING -> {
                        isLoading(true)
                    }
                }
            }
        })
    }

    private fun showMessage(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(bool: Boolean){
        if(bool){
            binding.rvUser.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvUser.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_USER && resultCode == Activity.RESULT_OK){
            fetchData()
        }
    }

}