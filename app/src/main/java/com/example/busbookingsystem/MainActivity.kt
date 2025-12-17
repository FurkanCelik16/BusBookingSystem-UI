package com.example.busbookingsystem

import android.widget.Toast
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.passenger.CreatePassengerDto
import com.example.busbookingsystem.data.model.ticket.ReserveTicketDto
import com.example.busbookingsystem.ui.screens.*
import com.example.busbookingsystem.ui.theme.BusBookingSystemTheme
import com.example.busbookingsystem.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(applicationContext)
        val savedId = sessionManager.getUserId()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusBookingSystemTheme {
                var searchOriginId by remember { mutableIntStateOf(0) }
                var searchDestinationId by remember { mutableIntStateOf(0) }
                var searchOriginDistrictId by remember { mutableStateOf<Int?>(null) }
                var searchDestinationDistrictId by remember { mutableStateOf<Int?>(null) }
                var searchDate by remember { mutableStateOf("") }

                var selectedTripId by remember { mutableIntStateOf(0) }
                var selectedSeatNumber by remember { mutableIntStateOf(0) }
                var currentTripPrice by remember { mutableDoubleStateOf(0.0) }

                var currentOriginName by remember { mutableStateOf("") }
                var currentDestName by remember { mutableStateOf("") }

                var currentOriginDistrict by remember { mutableStateOf<String?>(null) }
                var currentDestDistrict by remember { mutableStateOf<String?>(null) }

                var currentTripDateStr by remember { mutableStateOf("") }
                var currentTripTimeStr by remember { mutableStateOf("") }
                var currentTripPlateNumber by remember { mutableStateOf("") }
                var currentTripCompanyName by remember { mutableStateOf("") }
                var loggedInPassengerId by remember { mutableIntStateOf(savedId) }
                var currentScreen by remember { mutableStateOf("Login") }
                var currentReservationId by remember { mutableStateOf<Int?>(null) }

                var currentPassengerData by remember { mutableStateOf<CreatePassengerDto?>(null) }
                var selectedAdminTripId by remember { mutableIntStateOf(0) }

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                when (currentScreen) {
                    "Login" -> LoginScreen(
                        onNavigateToAdmin = { currentScreen = "AdminHome" },
                        onNavigateToUser = { currentScreen = "UserHome" },
                        onNavigateToRegister = { currentScreen = "Register" }
                    )

                    "Register" -> RegisterScreen(
                        onRegistrationSuccess = { currentScreen = "Login" },
                        onNavigateBack = { currentScreen = "Login" }
                    )

                    "UserHome" -> UserHomeScreen(
                        userId = loggedInPassengerId,
                        onSearchClick = { originId, originDistrictId, destId, destDistrictId, date ->
                            searchOriginId = originId
                            searchOriginDistrictId = originDistrictId
                            searchDestinationId = destId
                            searchDestinationDistrictId = destDistrictId
                            searchDate = date
                            currentScreen = "TripList"
                        },
                        onMyTicketsClick = { currentScreen = "MyTickets" },
                        onReservationClick = { ticket ->
                            selectedTripId = ticket.trip?.id ?: 0
                            selectedSeatNumber = ticket.seatNumber
                            currentTripPrice = ticket.trip?.price ?: 0.0
                            currentReservationId = ticket.id

                            currentOriginName = ticket.trip?.originCityName ?: ""
                            currentDestName = ticket.trip?.destinationCityName ?: ""

                            if (ticket.passenger != null) {
                                currentPassengerData = CreatePassengerDto(
                                    firstName = ticket.passenger.firstName,
                                    lastName = ticket.passenger.lastName,
                                    tcNo = ticket.passenger.tcNo,
                                    email = ticket.passenger.email,
                                    phoneNumber = ticket.passenger.phoneNumber,
                                    gender = ticket.passenger.gender,
                                    dateOfBirth = ticket.passenger.dateOfBirth
                                )
                            }
                            currentScreen = "Payment"
                        },
                        onLogoutClick = {
                            sessionManager.clearSession()
                            loggedInPassengerId = 0
                            currentScreen = "Login"
                        }
                    )

                    "MyTickets" -> MyTicketsScreen(
                        onNavigateBack = { currentScreen = "UserHome" }
                    )

                    "TripList" -> TripListScreen(
                        originId = searchOriginId,
                        originDistrictId = searchOriginDistrictId,
                        destinationId = searchDestinationId,
                        destinationDistrictId = searchDestinationDistrictId,
                        date = searchDate,
                        onNavigateBack = { currentScreen = "UserHome" },

                        onTripSelected = { tripId, price, originName, destName, tDate, tTime, plateNumber, compName, originDist, destDist ->
                            selectedTripId = tripId
                            currentTripPrice = price
                            currentOriginName = originName
                            currentDestName = destName
                            currentTripDateStr = tDate
                            currentTripTimeStr = tTime
                            currentTripPlateNumber = plateNumber
                            currentTripCompanyName = compName

                            currentOriginDistrict = originDist
                            currentDestDistrict = destDist

                            currentScreen = "SeatSelection"
                        }
                    )

                    "SeatSelection" -> SeatSelectionScreen(
                        tripId = selectedTripId,
                        onNavigateBack = { currentScreen = "TripList" },
                        onSeatSelected = { seatNumber ->
                            selectedSeatNumber = seatNumber
                            currentScreen = "PassengerDetails"
                        }
                    )

                    "PassengerDetails" -> PassengerDetailsScreen(
                        tripId = selectedTripId,
                        seatNumber = selectedSeatNumber,
                        price = currentTripPrice,
                        originName = currentOriginName,
                        destinationName = currentDestName,

                        originDistrict = currentOriginDistrict,
                        destinationDistrict = currentDestDistrict,

                        date = currentTripDateStr,
                        time = currentTripTimeStr,
                        busPlateNumber = currentTripPlateNumber,
                        companyName = currentTripCompanyName,
                        onNavigateBack = { currentScreen = "SeatSelection" },
                        onPassengerDataReady = { passengerDto, isReservation ->
                            if (isReservation) {
                                scope.launch {
                                    try {
                                        val passResponse = RetrofitClient.api.createPassenger(passengerDto)
                                        if (passResponse.success && passResponse.data != null) {
                                            loggedInPassengerId = passResponse.data.id
                                            sessionManager.saveUserId(loggedInPassengerId)
                                            val pId = passResponse.data.id
                                            val reserveDto = ReserveTicketDto(pId, selectedSeatNumber)
                                            val resResponse = RetrofitClient.api.reserveTicket(selectedTripId, reserveDto)
                                            if (resResponse.success) {
                                                Toast.makeText(context, "✅ Rezervasyon Başarılı!", Toast.LENGTH_LONG).show()
                                                currentScreen = "UserHome"
                                            } else {
                                                Toast.makeText(context, "Rezervasyon Hatası: ${resResponse.message}", Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Yolcu Kaydedilemedi: ${passResponse.message}", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                currentPassengerData = passengerDto
                                currentReservationId = null
                                currentScreen = "Payment"
                            }
                        }
                    )

                    "Payment" -> PaymentScreen(
                        tripId = selectedTripId,
                        passengerData = currentPassengerData!!,
                        seatNumber = selectedSeatNumber,
                        price = currentTripPrice,
                        reservationId = currentReservationId,
                        onNavigateBack = { currentScreen = "UserHome" },
                        onPaymentSuccess = {
                            currentScreen = "UserHome"
                        }
                    )

                    "AdminHome" -> AdminHomeScreen(
                        onNavigateToAddTrip = { currentScreen = "AddTrip" },
                        onNavigateToListTrips = { currentScreen = "AdminTripList" },
                        onNavigateToAddBus = { currentScreen = "AddBus" },
                        onLogout = { currentScreen = "Login" },
                        onNavigateToAddCompany = { currentScreen = "AddCompany" }
                    )
                    "AddTrip" -> AddTripScreen(
                        onNavigateBack = { currentScreen = "AdminHome" },
                        onTripAdded = { currentScreen = "AdminHome" }
                    )
                    "AdminTripList" -> AdminTripListScreen(
                        onNavigateBack = { currentScreen = "AdminHome" },
                        onNavigateToDetails = { tripId ->
                            selectedAdminTripId = tripId
                            currentScreen = "TripPassengerList"
                        }
                    )
                    "TripPassengerList" -> TripPassengerListScreen(
                        tripId = selectedAdminTripId,
                        onNavigateBack = { currentScreen = "AdminTripList" }
                    )
                    "AddBus" -> AddBusScreen(onNavigateBack = { currentScreen = "AdminHome" })
                    "AddCompany" -> AddCompanyScreen(onNavigateBack = { currentScreen = "AdminHome" })
                }
            }
        }
    }
}