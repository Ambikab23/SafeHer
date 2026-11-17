
package com.ambika.safeher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

private val ContactsPink = Color(0xFFB81D52)
private val ContactsBackground = Color(0xFFFDF7F9)
private val ContactsText = Color(0xFF30252A)
private val ContactsMuted = Color(0xFF88777E)
private val ContactsBorder = Color(0xFFEADCE2)

private fun normalizePhoneDigits(value: String): String {
    return value.map { character ->
        val digit = character.digitToIntOrNull()
        if (digit != null) digit.digitToChar() else character
    }.joinToString("")
}

private fun getPhoneDigits(value: String): String {
    return normalizePhoneDigits(value).filter { it.isDigit() }
}

@Composable
fun EmergencyContactsScreen(
    repository: EmergencyContactsRepository,
    onBack: () -> Unit
) {
    val contactViewModelFactory = remember(repository) {
        viewModelFactory {
            initializer {
                EmergencyContactsViewModel(repository)
            }
        }
    }

    val viewModel: EmergencyContactsViewModel = viewModel(
        factory = contactViewModelFactory
    )

    val contacts by viewModel.contacts.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    fun addEmergencyContact() {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanRelationship = relationship.trim()
        val phoneDigits = getPhoneDigits(cleanPhone)

        nameError = cleanName.isBlank()
        phoneError = false
        validationMessage = ""

        if (nameError) {
            validationMessage = "Please enter the contact name."
            return
        }

        val validPhoneFormat =
            cleanPhone.matches(Regex("^\\+?[0-9\\s().-]+$"))

        if (!validPhoneFormat || phoneDigits.length !in 7..15) {
            phoneError = true
            validationMessage =
                "Enter a valid phone number containing 7 to 15 digits."
            return
        }

        val duplicateContact = contacts.any { contact ->
            getPhoneDigits(contact.phoneNumber) == phoneDigits
        }

        if (duplicateContact) {
            phoneError = true
            validationMessage = "This phone number is already saved."
            return
        }

        viewModel.addContact(
            cleanName,
            normalizePhoneDigits(cleanPhone),
            cleanRelationship
        )

        name = ""
        phone = ""
        relationship = ""
        validationMessage = ""
        nameError = false
        phoneError = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ContactsBackground),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 28.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Emergency Contacts",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ContactsPink
                )

                Text(
                    text = "Add trusted people who can help you in an emergency.",
                    fontSize = 14.sp,
                    color = ContactsMuted,
                    lineHeight = 21.sp
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, ContactsBorder),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Add New Contact",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ContactsText
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                            validationMessage = ""
                        },
                        label = { Text("Contact Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ContactsPink,
                            unfocusedBorderColor = ContactsBorder,
                            focusedLabelColor = ContactsPink,
                            cursorColor = ContactsPink,
                            focusedTextColor = ContactsText,
                            unfocusedTextColor = ContactsText
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = normalizePhoneDigits(it)
                            phoneError = false
                            validationMessage = ""
                        },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = phoneError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ContactsPink,
                            unfocusedBorderColor = ContactsBorder,
                            focusedLabelColor = ContactsPink,
                            cursorColor = ContactsPink,
                            focusedTextColor = ContactsText,
                            unfocusedTextColor = ContactsText
                        )
                    )

                    OutlinedTextField(
                        value = relationship,
                        onValueChange = {
                            relationship = it
                        },
                        label = {
                            Text("Relationship (Friend, Family, etc.)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ContactsPink,
                            unfocusedBorderColor = ContactsBorder,
                            focusedLabelColor = ContactsPink,
                            cursorColor = ContactsPink,
                            focusedTextColor = ContactsText,
                            unfocusedTextColor = ContactsText
                        )
                    )

                    if (validationMessage.isNotBlank()) {
                        Text(
                            text = validationMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { addEmergencyContact() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ContactsPink,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE8C5D1),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text(
                            "Add Emergency Contact",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Saved Contacts (${contacts.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ContactsText
            )
        }

        if (contacts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, ContactsBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No contacts yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ContactsText
                        )

                        Text(
                            "Your trusted emergency contacts will appear here.",
                            fontSize = 13.sp,
                            color = ContactsMuted
                        )
                    }
                }
            }
        } else {
            items(
                items = contacts,
                key = { it.id }
            ) { contact ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, ContactsBorder),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = contact.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ContactsPink
                        )

                        Text(
                            "Phone: ${normalizePhoneDigits(contact.phoneNumber)}",
                            fontSize = 14.sp,
                            color = ContactsText
                        )

                        Text(
                            "Relationship: ${
                                contact.relationship.ifBlank {
                                    "Not specified"
                                }
                            }",
                            fontSize = 14.sp,
                            color = ContactsMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.deleteContact(contact)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = ContactsPink
                                )
                            ) {
                                Text(
                                    "Delete",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ContactsPink,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Return to Home",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
