package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PincodeDialog(
    currentPincode: String,
    onDismiss: () -> Unit,
    onApply: (pincode: String, city: String) -> Unit
) {
    var pincodeInput by remember { mutableStateOf(currentPincode) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val majorCities = listOf(
        "110001" to "New Delhi",
        "400001" to "Mumbai",
        "560001" to "Bengaluru",
        "500001" to "Hyderabad",
        "600001" to "Chennai",
        "700001" to "Kolkata",
        "122002" to "Gurugram",
        "411001" to "Pune"
    )

    fun resolveCity(pin: String): String {
        return majorCities.find { it.first == pin }?.second ?: when {
            pin.startsWith("11") -> "New Delhi"
            pin.startsWith("12") -> "Haryana / NCR"
            pin.startsWith("40") -> "Mumbai"
            pin.startsWith("56") -> "Bengaluru"
            pin.startsWith("50") -> "Hyderabad"
            pin.startsWith("60") -> "Chennai"
            pin.startsWith("70") -> "Kolkata"
            pin.startsWith("41") -> "Pune"
            pin.startsWith("38") -> "Ahmedabad"
            pin.startsWith("30") -> "Jaipur"
            else -> "India (Standard Delivery)"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("pincode_selection_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BizzyAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Delivery Location",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = BizzyTextMuted
                        )
                    }
                }

                Text(
                    text = "Enter a 6-digit Indian PIN code to view product availability and accurate delivery timelines.",
                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = pincodeInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            pincodeInput = it.filter { char -> char.isDigit() }
                            errorMessage = null
                        }
                    },
                    label = { Text("Enter 6-digit PIN code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(errorMessage!!, color = Color.Red, fontSize = 11.sp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pincode_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Popular Delivery Hubs:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    majorCities.forEach { (pin, city) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (pincodeInput == pin) BizzyPrimary.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
                            border = if (pincodeInput == pin) androidx.compose.foundation.BorderStroke(1.dp, BizzyPrimary) else null,
                            modifier = Modifier
                                .clickable {
                                    pincodeInput = pin
                                    errorMessage = null
                                }
                        ) {
                            Text(
                                text = "$city ($pin)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = if (pincodeInput == pin) BizzyPrimary else BizzyTextPrimary,
                                    fontWeight = if (pincodeInput == pin) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (pincodeInput.length == 6) {
                            val city = resolveCity(pincodeInput)
                            onApply(pincodeInput, city)
                            onDismiss()
                        } else {
                            errorMessage = "Please enter a valid 6-digit Indian PIN code"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_pincode_btn")
                ) {
                    Text(
                        text = "Check & Apply Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
