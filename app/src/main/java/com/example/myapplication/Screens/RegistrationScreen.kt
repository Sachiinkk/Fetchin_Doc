package com.example.myapplication.Screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

import com.example.myapplication.ViewModel.RegistrationViewModel
import java.util.*
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }

    val showSuccessDialog = viewModel.showSuccessDialog.value

    val isLoading = viewModel.isLoading.value
    val message = viewModel.message.value
    val isAuthenticated by viewModel.isAadhaarAuthenticated.collectAsStateWithLifecycle()
    val nameError = remember { mutableStateOf<String?>(null) }
    val fatherNameError = remember { mutableStateOf<String?>(null) }
    val motherNameError = remember { mutableStateOf<String?>(null) }
    val aadhaarError = remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }
    var showOtpCard by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()

    var otpValue by remember { mutableStateOf("") }
    val otpErrorMessage = viewModel.message.value
    val inputColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Gray,
        unfocusedBorderColor = Color.Gray,
        errorBorderColor = Color.Red
    )

    LaunchedEffect(viewModel.showOtpPopup.value) {
        showOtpCard = viewModel.showOtpPopup.value
    }

    val allValid by remember {
        derivedStateOf {
            nameError.value == null &&
                    fatherNameError.value == null &&
                    motherNameError.value == null &&
                    aadhaarError.value == null &&
                    dobError == null &&
                    viewModel.name.value.isNotBlank() &&
                    viewModel.fatherName.value.isNotBlank() &&
                    viewModel.motherName.value.isNotBlank() &&
                    viewModel.aadhaarNumber.value.isNotBlank() &&
                    viewModel.dateOfBirth.value.isNotBlank() &&
                    viewModel.gender.value.isNotBlank()
        }
    }

    val buttonEnabled by remember {
        derivedStateOf { true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Punjab e-Locker") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
            LaunchedEffect(otpErrorMessage) {
                otpErrorMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearMessage()
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.name.value,
                    onValueChange = {
                        viewModel.onNameChange(it)
                        nameError.value = when {
                            it.isBlank() -> "This field is required"
                            !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                            else -> null
                        }
                    },
                    label = { Text("Full Name") },
                    isError = nameError.value != null,
                    supportingText = {
                        if (nameError.value != null) Text(nameError.value!!, color = MaterialTheme.colorScheme.error)
                    },
                    colors = inputColors,
                    placeholder = { Text("Enter Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Other fields would go here as needed
                OutlinedTextField(
                    value = viewModel.fatherName.value,
                    onValueChange = {
                        viewModel.onFatherNameChange(it)
                        fatherNameError.value = when {
                            it.isBlank() -> "This field is required"
                            !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                            else -> null
                        }
                    },
                    label = { Text("Father Name") },
                    isError = fatherNameError.value != null,
                    supportingText = {
                        if (fatherNameError.value != null) Text(fatherNameError.value!!, color = MaterialTheme.colorScheme.error)
                    },
                    colors = inputColors,
                    placeholder = { Text("Enter Father Name") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = viewModel.motherName.value,
                    onValueChange = {
                        viewModel.onMotherNameChange(it)
                        motherNameError.value = when {
                            it.isBlank() -> "This field is required"
                            !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                            else -> null
                        }
                    },
                    label = { Text("Mother Name") },
                    isError = motherNameError.value != null,
                    supportingText = {
                        if (motherNameError.value != null) Text(motherNameError.value!!, color = MaterialTheme.colorScheme.error)
                    },
                    colors = inputColors,
                    placeholder = { Text("Enter Mother Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                var showPicker by remember { mutableStateOf(false) }

                if (showPicker) {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, day: Int ->
                            val selectedDate = Calendar.getInstance().apply {
                                set(year, month, day)
                            }.time

                            val today = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time

                            if (selectedDate.after(today)) {
                                dobError = "DOB must be before today"
                            } else {
                                val dobFormatted = String.format("%02d/%02d/%04d", day, month + 1, year)
                                viewModel.onDateOfBirthChange(dobFormatted)
                                dobError = null
                            }

                            showPicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                OutlinedTextField(
                    value = viewModel.dateOfBirth.value,
                    onValueChange = {}, // disable manual typing
                    readOnly = true,
                    label = { Text("Date of Birth") },
                    placeholder = { Text("Select DOB (DD/MM/YYYY)") },
                    trailingIcon = {
                        IconButton(onClick = { showPicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPicker = true }, // open on field click
                    isError = dobError != null,
                    supportingText = {
                        dobError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    colors = inputColors
                )








                ExposedDropdownMenuBox(
                    expanded = viewModel.genderExpanded.value,
                    onExpandedChange = { viewModel.onGenderExpandToggle() }
                ) {
                    OutlinedTextField(
                        value = viewModel.gender.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select Gender") },
                        label = { Text("Gender") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(viewModel.genderExpanded.value)
                        },
                        colors = inputColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.genderExpanded.value,
                        onDismissRequest = { viewModel.genderExpanded.value = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = { viewModel.onGenderSelect(it) }
                            )
                        }
                    }
                }


                OutlinedTextField(
                    value = viewModel.vaultKey.value,
                    onValueChange = {
                        viewModel.vaultKey.value = it
                        aadhaarError.value = if (it.isBlank()) "This field is required" else null
                    },
                    label = { Text("Aadhaar Vault Key Number") },
                    placeholder = { Text("Enter Aadhaar Vault Key Number") },
                    isError = aadhaarError.value != null,
                    supportingText = { aadhaarError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors,
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth()
                )


                Button(
                    onClick = {
                        viewModel.fetchDocumentsAfterForm(navController)
                        navController.navigate("documents_screen")
                    },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (isLoading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    else
                        Text("Submit")
                }
            }
        }
    }
}
