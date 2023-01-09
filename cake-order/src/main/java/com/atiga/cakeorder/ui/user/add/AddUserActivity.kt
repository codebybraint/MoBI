package com.atiga.cakeorder.ui.user.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.SpinnerCategory
import com.atiga.cakeorder.databinding.ActivityAddUserBinding
import com.atiga.cakeorder.util.SessionManager
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class AddUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUserBinding
    private val addUserViewModel: AddUserViewModel by viewModel()
    private lateinit var sessionManager: SessionManager
    private lateinit var userRoleAdapter: ArrayAdapter<SpinnerCategory>
    private var selectedRoleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.input_user)

        sessionManager = SessionManager(this)

        loadUserRoles()

        addUserViewModel.addedUser.observe(this, {
            it?.let {
                Toast.makeText(this, "User berhasil ditambahkan", Toast.LENGTH_LONG).show()
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })

        addUserViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })

        addUserViewModel.userRoles.observe(this,{
            it?.let {
                val arrayUserRoles = arrayListOf<SpinnerCategory>()
                it.forEach { category ->
                    arrayUserRoles.add(SpinnerCategory(category.roleId, category.roleName))
                }

                userRoleAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayUserRoles)
                userRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerRoleid.adapter = userRoleAdapter
                selectedRoleId = arrayUserRoles[0].id
            }
        })

        binding.spinnerRoleid.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedId = binding.spinnerRoleid.selectedItem as SpinnerCategory
                    selectedRoleId = selectedId.id
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun validateInputData(): Boolean{
        return if(binding.etUsername.text.toString().isNotBlank()
            && binding.etPassword.text.toString().isNotBlank()
            && binding.etPasswordConfirm.text.toString().isNotBlank()){
            if(binding.etPassword.text.toString().equals(binding.etPasswordConfirm.text.toString())){
                true
            } else {
                showMessage("Password tidak sama")
                false
            }
        } else {
            showMessage("Data belum lengkap")
            false
        }
    }

    private fun addUser(){
        val token = sessionManager.getFromPreference(SessionManager.TOKEN)
        lifecycleScope.launch {
            token?.let { addUserViewModel.addUser(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                selectedRoleId,
                it) }
        }
    }

    private fun loadUserRoles(){
        lifecycleScope.launch {
            addUserViewModel.getUserRoles()
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
        } else {
            binding.progressBarUpdate.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_submit) {
            if (validateInputData()) {
                item.isEnabled = false
                isLoading(true)
                addUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}