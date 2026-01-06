package com.example.petbuddy.data.api

import com.example.petbuddy.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("signup.php")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("get_my_pets.php")
    suspend fun getMyPets(
        @Field("user_id") userId: Int
    ): Response<PetResponse>

    @Multipart
    @POST("add_pet.php")
    suspend fun addPet(
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part("pet_name") petName: okhttp3.RequestBody,
        @Part("pet_type") petType: okhttp3.RequestBody,
        @Part("breed") breed: okhttp3.RequestBody?,
        @Part("color") color: okhttp3.RequestBody?,
        @Part("age") age: okhttp3.RequestBody?,
        @Part("microchip_id") microchipId: okhttp3.RequestBody?,
        @Part petImage: okhttp3.MultipartBody.Part?
    ): Response<PetResponse>

    @FormUrlEncoded
    @POST("get_vaccinations.php")
    suspend fun getVaccinations(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String
    ): Response<VaccinationResponse>

    @FormUrlEncoded
    @POST("add_vaccination.php")
    suspend fun addVaccination(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String,
        @Field("vaccine_name") vaccineName: String,
        @Field("last_date") lastDate: String,
        @Field("next_date") nextDate: String
    ): Response<VaccinationResponse>

    @FormUrlEncoded
    @POST("get_medications.php")
    suspend fun getMedications(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String
    ): Response<MedicationResponse>

    @FormUrlEncoded
    @POST("add_medication.php")
    suspend fun addMedication(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String,
        @Field("medication_name") medicationName: String,
        @Field("dosage_time") dosageTime: String,
        @Field("frequency") frequency: String
    ): Response<MedicationResponse>

    @FormUrlEncoded
    @POST("get_medical_records.php")
    suspend fun getMedicalRecords(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int
    ): Response<MedicalRecordResponse>

    @FormUrlEncoded
    @POST("upload_medical_record.php")
    suspend fun uploadMedicalRecord(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("title") title: String,
        @Field("file_url") fileUrl: String
    ): Response<MedicalRecordResponse>

    @FormUrlEncoded
    @POST("get_edit_profile.php")
    suspend fun getProfile(
        @Field("user_id") userId: Int
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("update_edit_profile.php")
    suspend fun updateProfile(
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String?
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("get_user_profile.php")
    suspend fun getUserProfile(
        @Field("user_id") userId: Int
    ): Response<UserProfileResponse>

    @FormUrlEncoded
    @POST("get_user_stats.php")
    suspend fun getUserStats(
        @Field("user_id") userId: Int
    ): Response<ProfileStatsResponse>

    @GET("quick_search.php")
    suspend fun quickSearch(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null
    ): Response<QuickSearchResponse>

    @FormUrlEncoded
    @POST("report_pet_details.php")
    suspend fun reportPetDetails(
        @Field("user_id") userId: Int,
        @Field("pet_type") petType: String,
        @Field("pet_name") petName: String,
        @Field("breed") breed: String,
        @Field("age") age: String? = null,
        @Field("weight") weight: String? = null,
        @Field("primary_color") primaryColor: String? = null,
        @Field("description") description: String? = null
    ): Response<ReportPetDetailsResponse>

    @FormUrlEncoded
    @POST("update_lost_pet_identification.php")
    suspend fun updateIdentification(
        @Field("lost_id") lostId: Int,
        @Field("has_microchip") hasMicrochip: Int,
        @Field("microchip_number") microchipNumber: String? = null,
        @Field("has_collar") hasCollar: Int,
        @Field("collar_description") collarDescription: String? = null,
        @Field("has_id_tag") hasIdTag: Int,
        @Field("id_tag_text") idTagText: String? = null
    ): Response<UpdateIdentificationResponse>

    // Community Posts APIs
    @GET("get_community_posts.php")
    suspend fun getCommunityPosts(): Response<CommunityPostResponse>

    @FormUrlEncoded
    @POST("add_community_post.php")
    suspend fun addCommunityPost(
        @Field("user_id") userId: Int,
        @Field("content") content: String
    ): Response<AddPostResponse>

    @FormUrlEncoded
    @POST("toggle_post_like.php")
    suspend fun togglePostLike(
        @Field("post_id") postId: Int,
        @Field("user_id") userId: Int
    ): Response<ToggleLikeResponse>

    @FormUrlEncoded
    @POST("get_post_comments.php")
    suspend fun getPostComments(
        @Field("post_id") postId: Int
    ): Response<PostCommentsResponse>

    @FormUrlEncoded
    @POST("add_post_comment.php")
    suspend fun addPostComment(
        @Field("post_id") postId: Int,
        @Field("user_id") userId: Int,
        @Field("comment") comment: String
    ): Response<AddCommentResponse>

    // Clinic Owner APIs
    @FormUrlEncoded
    @POST("pet_clinic_register.php")
    suspend fun clinicOwnerSignup(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): Response<ClinicOwnerSignupResponse>

    @FormUrlEncoded
    @POST("pet_clinic_login.php")
    suspend fun clinicOwnerLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ClinicOwnerLoginResponse>

    // Clinic Owner Forgot Password APIs
    @FormUrlEncoded
    @POST("pet_clinic_send_verification_code.php")
    suspend fun clinicOwnerSendVerificationCode(
        @Field("email") email: String
    ): Response<ClinicOwnerSendCodeResponse>

    @FormUrlEncoded
    @POST("pet_clinic_verify_code.php")
    suspend fun clinicOwnerVerifyCode(
        @Field("email") email: String,
        @Field("code") code: String
    ): Response<ClinicOwnerVerifyCodeResponse>

    @FormUrlEncoded
    @POST("pet_clinic_forgot_password.php")
    suspend fun clinicOwnerResetPassword(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ClinicOwnerResetPasswordResponse>

    // Pet Owner Forgot Password APIs
    @FormUrlEncoded
    @POST("send_verification_code.php")
    suspend fun sendVerificationCode(
        @Field("email") email: String
    ): Response<SendCodeResponse>

    @FormUrlEncoded
    @POST("verify_code.php")
    suspend fun verifyCode(
        @Field("email") email: String,
        @Field("code") code: String
    ): Response<VerifyCodeResponse>

    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ResetPasswordResponse>

    // Clinic Owner Appointments API
    @FormUrlEncoded
    @POST("get_clinic_appointments.php")
    suspend fun getClinicAppointments(
        @Field("clinic_user_id") clinicUserId: Int,
        @Field("type") type: String // today | upcoming | past | all
    ): Response<ClinicAppointmentResponse>

    // Clinic Owner Patients API
    @FormUrlEncoded
    @POST("get_patients.php")
    suspend fun getClinicPatients(
        @Field("search") search: String = ""
    ): Response<ClinicPatientResponse>

    // Clinic Owner Profile API
    @FormUrlEncoded
    @POST("get_clinic_profile.php")
    suspend fun getClinicProfile(
        @Field("clinic_user_id") clinicUserId: Int
    ): Response<ClinicProfileResponse>

    @FormUrlEncoded
    @POST("update_clinic_profile.php")
    suspend fun updateClinicProfile(
        @Field("clinic_user_id") clinicUserId: Int,
        @Field("full_name") fullName: String,
        @Field("clinic_name") clinicName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    ): Response<UpdateClinicProfileResponse>

    // Pet Nutrition API
    @FormUrlEncoded
    @POST("get_pet_nutrition.php")
    suspend fun getPetNutrition(
        @Field("pet_id") petId: Int
    ): Response<NutritionResponse>

    @FormUrlEncoded
    @POST("update_nutrition_plan.php")
    suspend fun updateNutritionPlan(
        @Field("pet_id") petId: Int,
        @Field("daily_cups") dailyCups: String,
        @Field("breakfast") breakfast: String,
        @Field("dinner") dinner: String,
        @Field("current_food") currentFood: String
    ): Response<UpdateNutritionResponse>

    // Grooming Services API
    @GET("get_grooming_services.php")
    suspend fun getGroomingServices(): Response<GroomingServiceResponse>

    // Payments
    @FormUrlEncoded
    @POST("process_payment.php")
    suspend fun processPayment(
        @Field("user_id") userId: Int,
        @Field("service_name") serviceName: String,
        @Field("appointment_date") appointmentDate: String,
        @Field("appointment_time") appointmentTime: String,
        @Field("amount") amount: String,
        @Field("payment_method") paymentMethod: String,
        @Field("card_last4") cardLast4: String
    ): Response<PaymentResponse>

    // Notifications API
    @FormUrlEncoded
    @POST("get_lost_pet_notifications.php")
    suspend fun getLostPetNotifications(
        @Field("user_id") userId: Int
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_notification_on_lost_pet_report.php")
    suspend fun createLostPetNotifications(
        @Field("lost_id") lostId: Int,
        @Field("user_id") userId: Int,
        @Field("pet_name") petName: String,
        @Field("pet_type") petType: String,
        @Field("breed") breed: String,
        @Field("location") location: String? = null,
        @Field("lost_date") lostDate: String? = null,
        @Field("owner_name") ownerName: String? = null
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_pet_profile_notification.php")
    suspend fun createPetProfileNotification(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int? = null,
        @Field("pet_name") petName: String,
        @Field("pet_type") petType: String,
        @Field("breed") breed: String? = null
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_vaccination_notification.php")
    suspend fun createVaccinationNotification(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String,
        @Field("vaccine_name") vaccineName: String,
        @Field("next_date") nextDate: String? = null
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_medication_notification.php")
    suspend fun createMedicationNotification(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String,
        @Field("medication_name") medicationName: String,
        @Field("dosage_time") dosageTime: String? = null,
        @Field("frequency") frequency: String? = null
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_nutrition_notification.php")
    suspend fun createNutritionNotification(
        @Field("user_id") userId: Int,
        @Field("pet_id") petId: Int,
        @Field("pet_name") petName: String,
        @Field("daily_cups") dailyCups: String? = null
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("create_community_post_notification.php")
    suspend fun createCommunityPostNotification(
        @Field("user_id") userId: Int,
        @Field("post_id") postId: Int? = null,
        @Field("content") content: String? = null
    ): Response<okhttp3.ResponseBody>

    // AI Image Comparison API
    @FormUrlEncoded
    @POST("compare_pet_image.php")
    suspend fun comparePetImage(
        @Field("image_base64") imageBase64: String,
        @Field("user_id") userId: Int? = null
    ): Response<okhttp3.ResponseBody>
}
