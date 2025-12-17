package com.example.busbookingsystem.data.api

import com.example.busbookingsystem.data.model.ApiResponse
import com.example.busbookingsystem.data.model.bus.BusDto
import com.example.busbookingsystem.data.model.location.CityDto
import com.example.busbookingsystem.data.model.company.CompanyDto
import com.example.busbookingsystem.data.model.ticket.CompleteReservationDto
import com.example.busbookingsystem.data.model.bus.CreateBusDto
import com.example.busbookingsystem.data.model.company.CreateCompanyDto
import com.example.busbookingsystem.data.model.passenger.CreatePassengerDto
import com.example.busbookingsystem.data.model.ticket.CreateTicketDto
import com.example.busbookingsystem.data.model.trip.CreateTripDto
import com.example.busbookingsystem.data.model.auth.LoginRequest
import com.example.busbookingsystem.data.model.auth.LoginResponse
import com.example.busbookingsystem.data.model.passenger.PassengerDto
import com.example.busbookingsystem.data.model.auth.RegisterRequest
import com.example.busbookingsystem.data.model.ticket.ReserveTicketDto
import com.example.busbookingsystem.data.model.ticket.TicketDto
import com.example.busbookingsystem.data.model.trip.TripAvailabilityDto
import com.example.busbookingsystem.data.model.trip.TripDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @GET("api/Cities")
    suspend fun getCities(): ApiResponse<List<CityDto>>

    @GET("api/Trips/search")
    suspend fun searchTrips(
        @Query("originId") originId: Int,
        @Query("originDistrictId") originDistrictId: Int?,
        @Query("destinationId") destinationId: Int,
        @Query("destinationDistrictId") destinationDistrictId: Int?,
        @Query("date") date: String
    ): ApiResponse<List<TripDto>>

    @GET("api/Tickets/trips/{tripId}/availability")
    suspend fun getTripSeats(@Path("tripId") tripId: Int): ApiResponse<TripAvailabilityDto>

    @POST("api/Passengers")
    suspend fun createPassenger(@Body passenger: CreatePassengerDto): ApiResponse<PassengerDto>

    @GET("api/Passengers/tc/{tcNo}")
    suspend fun getPassengerByTc(@Path("tcNo") tcNo: String): ApiResponse<PassengerDto>

    // 3. Bileti Satın Al
    @POST("api/Tickets/trips/{tripId}/purchase")
    suspend fun purchaseTicket(@Path("tripId") tripId: Int, @Body ticket: CreateTicketDto): ApiResponse<TicketDto>

    @GET("api/Tickets/passengers/{passengerId}")
    suspend fun getPassengerTickets(@Path("passengerId") passengerId: Int): ApiResponse<List<TicketDto>>

    @GET("api/Buses")
    suspend fun getAllBuses(): ApiResponse<List<BusDto>>

    // Yeni Sefer Ekle
    @POST("api/Trips")
    suspend fun createTrip(@Body trip: CreateTripDto): ApiResponse<TripDto>

    @GET("api/Trips")
    suspend fun getAllTrips(): ApiResponse<List<TripDto>>

    // Seferi Sil
    @DELETE("api/Trips/{id}")
    suspend fun deleteTrip(@Path("id") id: Int): ApiResponse<Boolean>

    @POST("api/Buses")
    suspend fun createBus(@Body bus: CreateBusDto): ApiResponse<BusDto>

    @GET("api/Tickets/trips/{tripId}")
    suspend fun getTicketsByTrip(@Path("tripId") tripId: Int): ApiResponse<List<TicketDto>>

    @DELETE("api/Tickets/{ticketId}")
    suspend fun cancelTicket(@Path("ticketId") ticketId: Int): ApiResponse<Boolean>

    @GET("api/Companies")
    suspend fun getAllCompanies(): ApiResponse<List<CompanyDto>>

    @POST("api/Companies")
    suspend fun createCompany(@Body company: CreateCompanyDto): ApiResponse<CompanyDto>

    @POST("api/Tickets/trips/{tripId}/reserve")
    suspend fun reserveTicket(
        @Path("tripId") tripId: Int,
        @Body request: ReserveTicketDto
    ): ApiResponse<TicketDto>

    @GET("api/Tickets/trips/{tripId}/seats/{seatNumber}/validate-gender")
    suspend fun validateSeatGender(
        @Path("tripId") tripId: Int,
        @Path("seatNumber") seatNumber: Int,
        @Query("gender") gender: Int
    ): ApiResponse<Boolean>

    @POST("api/Tickets/{ticketId}/complete-reservation")
    suspend fun completeReservation(
        @Path("ticketId") id: Int,
        @Body body: CompleteReservationDto
    ): ApiResponse<TicketDto>
}
