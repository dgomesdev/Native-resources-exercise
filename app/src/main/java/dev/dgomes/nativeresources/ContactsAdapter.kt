package dev.dgomes.nativeresources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.dgomes.nativeresources.databinding.ContactsViewBinding

class ContactsAdapter(private val contactsList: ArrayList<Contact>): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ContactsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = contactsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(contactsList[position])
    }

    class ViewHolder(private val itemBinding: ContactsViewBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bindItem(contact: Contact) {
            itemBinding.contactName.text = contact.name
            itemBinding.contactPhoneNumber.text = contact.phoneNumber
        }
    }
}