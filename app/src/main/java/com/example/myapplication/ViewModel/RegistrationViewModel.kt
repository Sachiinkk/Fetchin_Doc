package com.example.myapplication.ViewModel

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.media.session.MediaSession.Token
import kotlinx.coroutines.delay
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Remote.UserInfoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.myapplication.Repository.UserRepository
import java.util.*
import  com.example.myapplication.Screens.DocumentsScreen
import com.example.myapplication.Remote.LicenceDocument
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope


import kotlinx.coroutines.launch

import androidx.compose.runtime.mutableStateListOf



import java.util.*

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    val userToken = mutableStateOf("")
    val name = mutableStateOf("")
    val fatherName = mutableStateOf("")
    val motherName = mutableStateOf("")
    val dateOfBirth = mutableStateOf("")
    val gender = mutableStateOf("")
    val genderExpanded = mutableStateOf(false)
    val aadhaarNumber = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val message = mutableStateOf<String?>(null)
    val isAadhaarAuthenticated = MutableStateFlow(false)
    val showOtpPopup = mutableStateOf(false)
    val showSuccessDialog = mutableStateOf(false)
    val showDocumentsScreen = mutableStateOf(false)

    val vaultKey = mutableStateOf("")
    val issuedDocs = mutableStateListOf<LicenceDocument>()
    val expiredDocs = mutableStateListOf<LicenceDocument>()
    fun onNameChange(newName: String) { name.value = newName }
    fun onFatherNameChange(newName: String) { fatherName.value = newName }
    fun onMotherNameChange(newName: String) { motherName.value = newName }
    fun onDateOfBirthChange(newDob: String) { dateOfBirth.value = newDob }
    fun onGenderSelect(selected: String) {
        gender.value = selected
        genderExpanded.value = false
    }
    fun onGenderExpandToggle() { genderExpanded.value = !genderExpanded.value }
    fun onAadhaarChange(newAadhaar: String) { aadhaarNumber.value = newAadhaar }
    fun clearMessage() { message.value = null }

    fun fetchDocumentsAfterForm(navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // 1. Format DOB
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val parsedDate = inputFormat.parse(dateOfBirth.value)
                val formattedDob = outputFormat.format(parsedDate ?: Date())

                // 2. Prepare request
                val request = UserInfoRequest(
                    username = name.value,
                    fathername = fatherName.value,
                    mothername = motherName.value,
                    gender = gender.value,
                    aadhar_verification_id = vaultKey.value,
                    dob = formattedDob
                )

                // 3. Log request JSON
                val jsonLog = """
                {
                    "username": "${request.username}",
                    "fathername": "${request.fathername}",
                    "mothername": "${request.mothername}",
                    "gender": "${request.gender}",
                    "aadhar_verification_id": "${request.aadhar_verification_id}",
                    "dob": "${request.dob}"
                }
            """.trimIndent()
                Log.d("DOC_FETCH_REQUEST", jsonLog)





                // 5. API call
//                val response = repository.getUserDetails(userToken.value, request)
                val userToken =
                    """eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiI0MDQwNDcxIiwicHNwY2xfYWdlbnQiOiIyMTMxOCIsInVzZXJ0eXBlIjoiY2l0aXplbiIsIm5hbWUiOiJIYXJpbmRlciBDaGVlbWEiLCJ1c2VybmFtZSI6IjkzNjgzMDA3NjUiLCJleHAiOjE3NjExMjY2ODIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6NjM4ODQiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjYzODg0In0.ib25aczA5Mu2nYR7zKj3yhNpHqdb2Qzz6BYAQL72J-0"""


                Log.d("AUTH_TOKEN", "Bearer ${userToken}")
                val token = "${userToken}"
                val response = repository.getUserDetails(token, request)
                Log.d("DOC_FETCH", "HTTP Code: ${response.code()}")
                Log.d("DOC_FETCH", "Is Successful: ${response.isSuccessful}")
                Log.d("DOC_FETCH", "Headers: ${response.headers()}")
                Log.d("DOC_FETCH", "Raw: ${response.raw()}")
                Log.d("DOC_FETCH", "Message: ${response.message()}")
                Log.d("DOC_FETCH", "Body: ${response.body()?.toString()}")
                Log.d("DOC_FETCH", "Error Body: ${response.errorBody()?.string()}")

// 4. Validate token
                if (userToken.isBlank()) {
                    message.value = "Auth token missing. Please authenticate again."
                    isLoading.value = false
                    return@launch
                }

                if (response.isSuccessful) {
                    val userDocs = response.body()
                    val responseText = "Documents fetched ✅\n\n$userDocs"

                    // ✅ Set the response string BEFORE navigating
                    navController.currentBackStackEntry?.savedStateHandle?.set("api_response", responseText)

                    // ✅ Navigate to screen
                    navController.navigate("documents_screen")

                    Log.d("API_Count", "$userDocs")


                    Log.d("API_Count" ,"${userDocs}")

                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("DOC_FETCH", "Failed: $error")
                    message.value = "Failed to fetch documents ❌"
                }
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
                Log.e("DOC_FETCH", "Exception: ${e.stackTraceToString()}")
            } finally {
                isLoading.value = false
            }
        }
    }








}