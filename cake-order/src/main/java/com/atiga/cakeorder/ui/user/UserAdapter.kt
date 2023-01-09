package com.atiga.cakeorder.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.databinding.ItemUserBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var onItemClick: ((User) -> Unit)? = null
    var userData = mutableListOf<User>()

    fun setData(data: MutableList<User>) {
        userData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)

        fun bind(data: User) {
            with(binding) {
                tvUser.text = data.username
                tvUserRole.text = data.userRole.roleName
            }
        }

        init {
            binding.cardUser.setOnClickListener {
                onItemClick?.invoke(userData[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val data = userData[position]
        holder.bind(data)
    }

    override fun getItemCount() = userData.size
}