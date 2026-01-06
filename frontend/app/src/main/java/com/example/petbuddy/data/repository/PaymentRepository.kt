package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.PaymentRequest
import com.example.petbuddy.data.model.PaymentResponse

class PaymentRepository {
    private val apiService = RetrofitClient.api

    suspend fun processPayment(request: PaymentRequest): Result<PaymentResponse> {
        return try {
            val response = apiService.processPayment(
                userId = request.userId,
                serviceName = request.serviceName,
                appointmentDate = request.appointmentDate,
                appointmentTime = request.appointmentTime,
                amount = request.amount,
                paymentMethod = request.paymentMethod,
                cardLast4 = request.cardLast4
            )

            if (response.isSuccessful && response.body() != null) {
                val paymentResponse = response.body()!!
                if (paymentResponse.status) {
                    Result.success(paymentResponse)
                } else {
                    Result.failure(Exception(paymentResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Payment failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot reach XAMPP. Make sure Apache is running and the BASE_URL points to your machine (10.0.2.2 for emulator or your LAN IP)."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure Apache is running and port 80/443 is open."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Payment request timed out. Check your network or server load."))
        } catch (e: com.google.gson.JsonSyntaxException) {
            Result.failure(Exception("Unexpected server response. Verify that process_payment.php returns valid JSON."))
        } catch (e: Exception) {
            Result.failure(Exception("Payment error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
}

