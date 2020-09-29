package com.example.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

lateinit var contactList: List<ContactAdapter.Contact>

class ContactAdapter(
    private val contacts: List<Contact>,
    private val onClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) = holder.bind(contacts[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
        holder.root.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }
        return holder
    }

    class ContactViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(contact: Contact) {
            with(root) {
                first_name.text = contact.name
                last_name.text = contact.phoneNumber
            }
        }
    }

    data class Contact(val name: String, val phoneNumber: String)
}
