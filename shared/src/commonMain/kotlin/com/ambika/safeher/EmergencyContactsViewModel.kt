
package com.ambika.safeher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyContactsViewModel(
    private val repository: EmergencyContactsRepository
) : ViewModel() {

    private val _contacts =
        MutableStateFlow<List<EmergencyContact>>(emptyList())

    val contacts: StateFlow<List<EmergencyContact>> =
        _contacts.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            val savedContacts = withContext(Dispatchers.Default) {
                repository.getAllContacts()
            }
            _contacts.value = savedContacts
        }
    }

    fun addContact(
        name: String,
        phoneNumber: String,
        relationship: String
    ) {
        if (name.isBlank() || phoneNumber.isBlank()) return

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.addContact(
                    EmergencyContact(
                        id = 0,
                        name = name,
                        phoneNumber = phoneNumber,
                        relationship = relationship
                    )
                )
            }
            loadContacts()
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.deleteContact(contact.id)
            }
            loadContacts()
        }
    }
}
