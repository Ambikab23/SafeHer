
package com.ambika.safeher

import com.ambika.safeher.database.SafeHerDatabase

class EmergencyContactsRepository(
    private val database: SafeHerDatabase
) {
    private val queries = database.emergencyContactQueries

    fun getAllContacts(): List<EmergencyContact> {
        return queries.getAllContacts().executeAsList().map { contact ->
            EmergencyContact(
                id = contact.id.toInt(),
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                relationship = contact.relationship
            )
        }
    }

    fun addContact(contact: EmergencyContact) {
        queries.insertContact(
            name = contact.name,
            phoneNumber = contact.phoneNumber,
            relationship = contact.relationship
        )
    }

    fun deleteContact(contactId: Int) {
        queries.deleteContact(contactId.toLong())
    }
}
