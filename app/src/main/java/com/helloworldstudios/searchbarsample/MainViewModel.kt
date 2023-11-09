package com.helloworldstudios.searchbarsample

import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
class MainViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _contacts = MutableStateFlow(allContacts)

    val contacts = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_contacts) { text, contacts ->
            if (text.isBlank()){
                contacts
            }
            else{
                delay(500L)
                contacts.filter {
                    doesMatchSearchQuery(it, text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .map {contacts ->
            if (contacts.isEmpty())
                listOf(Contact("Contacts list is empty", "", "", ""))
            else
                contacts
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _contacts.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun doesMatchSearchQuery(contact: Contact, query: String): Boolean{
        val matchingCombinations = listOf(
            "${contact.firstName}",
            "${contact.firstName} ${contact.middleName}",
            "${contact.firstName}${contact.middleName}",
            "${contact.firstName} ${contact.middleName} ${contact.lastName}",
            "${contact.firstName}${contact.middleName}${contact.lastName}",
            "${contact.firstName.first()}${contact.lastName.first()}",
            "${contact.firstName.first()}${contact.middleName?.first()}${contact.lastName.first()}",
            "${contact.phoneNumber}"
        )

        return matchingCombinations.any{it.contains(query, ignoreCase = true)}
    }
}