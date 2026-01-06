package com.example.petbuddy.ui.theme.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.example.petbuddy.ui.theme.splash.SplashScreen
import com.example.petbuddy.ui.theme.onboarding.OnboardingScreen1
import com.example.petbuddy.ui.theme.onboarding.OnboardingScreen2
import com.example.petbuddy.ui.theme.onboarding.OnboardingScreen3
import com.example.petbuddy.ui.theme.onboarding.OnboardingScreen4
import com.example.petbuddy.ui.theme.access.UserAccessScreen
import com.example.petbuddy.ui.theme.login.PetOwnerLoginScreen
import com.example.petbuddy.ui.theme.login.ClinicOwnerLoginScreen
import com.example.petbuddy.ui.theme.login.ForgotPasswordScreen
import com.example.petbuddy.ui.theme.login.ClinicOwnerForgotPasswordScreen
import com.example.petbuddy.ui.theme.home.HomeScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerHomeScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerAppointmentsScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerPatientsScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerNotificationsScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerProfileScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerEditProfileScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerServicesManagementScreen
import com.example.petbuddy.ui.theme.home.ClinicOwnerLogoutScreen
import com.example.petbuddy.ui.theme.home.QuickSearchScreen
import com.example.petbuddy.ui.theme.home.AIPhotoMatchingScreen
import com.example.petbuddy.ui.theme.home.AIAnalysisScreen
import com.example.petbuddy.ui.theme.home.AIMatchingResultsScreen
import com.example.petbuddy.data.model.ImageComparisonResponse
import com.google.gson.Gson
import android.net.Uri
import com.example.petbuddy.ui.theme.home.MatchDetailsScreen
import com.example.petbuddy.ui.theme.home.ViewSideBySideScreen
import com.example.petbuddy.ui.theme.home.ConfirmMatchScreen
import com.example.petbuddy.ui.theme.home.ReportLostPetScreen
import com.example.petbuddy.ui.theme.home.UploadPhotosScreen
import com.example.petbuddy.ui.theme.home.PetDetailsScreen
import com.example.petbuddy.ui.theme.home.IdentificationScreen
import com.example.petbuddy.ui.theme.home.LastSeenLocationScreen
import com.example.petbuddy.ui.theme.home.NotificationsScreen
import com.example.petbuddy.ui.theme.home.PetDetailsViewScreen
import com.example.petbuddy.ui.theme.home.ContactOwnerScreen
import com.example.petbuddy.ui.theme.home.ShareAlertScreen
import com.example.petbuddy.ui.theme.home.MyPetsScreen
import com.example.petbuddy.ui.theme.home.AddPetProfileScreen
import com.example.petbuddy.ui.theme.home.PetProfileScreen
import com.example.petbuddy.ui.theme.home.MedicalRecordsScreen
import com.example.petbuddy.ui.theme.home.VaccinationScheduleScreen
import com.example.petbuddy.ui.theme.home.AddVaccinationScreen
import com.example.petbuddy.ui.theme.home.MedicationRemindersScreen
import com.example.petbuddy.ui.theme.home.AddMedicationScreen
import com.example.petbuddy.ui.theme.home.AddMedicalRecordScreen
import com.example.petbuddy.ui.theme.home.NutritionMealPlanScreen
import com.example.petbuddy.ui.theme.home.AddNutritionPlanScreen
import com.example.petbuddy.ui.theme.home.GroomingScheduleScreen
import com.example.petbuddy.ui.theme.home.BookGroomingServiceScreen
import com.example.petbuddy.ui.theme.home.ServiceDetailsScreen
import com.example.petbuddy.ui.theme.home.PaymentScreen
import com.example.petbuddy.ui.theme.home.CommunityScreen
import com.example.petbuddy.ui.theme.home.ShareYourStoryScreen
import com.example.petbuddy.ui.theme.home.ProfileScreen
import com.example.petbuddy.ui.theme.home.EditProfileScreen
import com.example.petbuddy.ui.theme.home.SettingsScreen
import com.example.petbuddy.ui.theme.home.LogoutScreen
import com.example.petbuddy.ui.theme.home.FAQsScreen
import com.example.petbuddy.ui.theme.home.AboutScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.VerificationScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.ClinicOwnerVerificationScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.CreateNewPasswordScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.ClinicOwnerCreateNewPasswordScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.CreateAccountScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.ClinicOwnerCreateAccountScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.CompleteProfileScreen
import com.example.petbuddy.ui.theme.OnboardingScreens.ClinicOwnerCompleteProfileScreen

@Composable
fun AppNavHost() {
    // Navigation host for PetBuddy app

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen {
                navController.navigate("onboarding1") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("onboarding1") {
            OnboardingScreen1 {
                navController.navigate("onboarding2")
            }
        }

        composable("onboarding2") {
            OnboardingScreen2 {
                navController.navigate("onboarding3")
            }
        }

        composable("onboarding3") {
            OnboardingScreen3 {
                navController.navigate("onboarding4")
            }
        }

        composable("onboarding4") {
            OnboardingScreen4 {
                navController.navigate("user_access")
            }
        }

        composable("user_access") {
            UserAccessScreen(
                onPetOwnerLogin = {
                    navController.navigate("pet_owner_login")
                },
                onClinicOwnerLogin = {
                    navController.navigate("clinic_owner_login")
                }
            )
        }

        composable("pet_owner_login") {
            PetOwnerLoginScreen(
                onSignIn = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onSignUp = {
                    navController.navigate("create_account")
                },
                onForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("clinic_owner_login") {
            ClinicOwnerLoginScreen(
                onSignIn = {
                    navController.navigate("clinic_owner_home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onSignUp = {
                    // Navigate to create account for clinic owners
                    navController.navigate("clinic_owner_create_account")
                },
                onForgotPassword = {
                    navController.navigate("clinic_owner_forgot_password")
                }
            )
        }

        composable("create_account") {
            CreateAccountScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreateAccount = {
                    // After successful account creation, navigate to complete profile
                    navController.navigate("complete_profile") {
                        // Clear back stack up to and including create_account
                        popUpTo("create_account") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignIn = {
                    // Navigate back to login screen
                    navController.popBackStack()
                }
            )
        }

        composable("clinic_owner_create_account") {
            ClinicOwnerCreateAccountScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreateAccount = {
                    // After successful account creation, navigate to clinic owner complete profile
                    navController.navigate("clinic_owner_complete_profile")
                },
                onSignIn = {
                    // Navigate back to clinic owner login screen
                    navController.popBackStack()
                }
            )
        }

        composable("complete_profile") {
            CompleteProfileScreen(
                onComplete = {
                    // After completing profile, navigate to home
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onSkip = {
                    // Skip profile completion and go to home
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("clinic_owner_complete_profile") {
            ClinicOwnerCompleteProfileScreen(
                onComplete = {
                    navController.navigate("clinic_owner_home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("clinic_owner_home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSendResetCode = { email ->
                    navController.navigate("verification/$email")
                }
            )
        }

        composable("clinic_owner_forgot_password") {
            ClinicOwnerForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSendResetCode = { email ->
                    navController.navigate("clinic_owner_verification/$email")
                }
            )
        }

        composable(
            route = "verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationScreen(
                email = email,
                onBack = {
                    navController.popBackStack()
                },
                onVerify = { verifiedEmail ->
                    navController.navigate("create_new_password/$verifiedEmail")
                },
                onResend = { resendEmail ->
                    // Resend handled in the screen
                }
            )
        }

        composable(
            route = "clinic_owner_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ClinicOwnerVerificationScreen(
                email = email,
                onBack = {
                    navController.popBackStack()
                },
                onVerify = { verifiedEmail ->
                    navController.navigate("clinic_owner_create_new_password/$verifiedEmail")
                },
                onResend = { resendEmail ->
                    // Resend handled in the screen
                }
            )
        }

        composable(
            route = "create_new_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CreateNewPasswordScreen(
                email = email,
                onBack = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    // Navigate back to login screen after successful password reset
                    // Clear the entire password reset flow (forgot_password, verification, create_new_password)
                    navController.navigate("pet_owner_login") {
                        popUpTo("forgot_password") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "clinic_owner_create_new_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ClinicOwnerCreateNewPasswordScreen(
                email = email,
                onBack = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    // Navigate back to clinic owner login screen after successful password reset
                    // Clear the entire password reset flow (clinic_owner_forgot_password, clinic_owner_verification, clinic_owner_create_new_password)
                    navController.navigate("clinic_owner_login") {
                        popUpTo("clinic_owner_forgot_password") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onSearchClick = {
                    navController.navigate("quick_search")
                },
                onAIScanClick = {
                    navController.navigate("ai_photo_matching") {
                        launchSingleTop = true
                    }
                },
                onReportClick = {
                    navController.navigate("report_lost_pet")
                },
                onNotificationsClick = {
                    navController.navigate("notifications")
                },
                onPetCareClick = {
                    navController.navigate("my_pets") {
                        launchSingleTop = true
                    }
                },
                onCommunityClick = {
                    navController.navigate("community") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("clinic_owner_home") {
            ClinicOwnerHomeScreen(
                onAppointmentsClick = {
                    navController.navigate("clinic_owner_appointments") {
                        launchSingleTop = true
                    }
                },
                onPatientsClick = {
                    navController.navigate("clinic_owner_patients") {
                        launchSingleTop = true
                    }
                },
                onNotificationsClick = {
                    navController.navigate("clinic_owner_notifications") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate("clinic_owner_profile") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("clinic_owner_appointments") {
            ClinicOwnerAppointmentsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAppointmentClick = { appointmentId ->
                    // TODO: Navigate to appointment details screen when created
                }
            )
        }
        
        composable("clinic_owner_patients") {
            ClinicOwnerPatientsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPatientClick = { patientId ->
                    // TODO: Navigate to patient details screen when created
                }
            )
        }
        
        composable("clinic_owner_notifications") {
            ClinicOwnerNotificationsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onMarkAllRead = {
                    // TODO: Handle mark all as read
                },
                onNotificationClick = { notificationId ->
                    // TODO: Navigate to notification details screen when created
                }
            )
        }
        
        composable("clinic_owner_profile") {
            ClinicOwnerProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditProfile = {
                    navController.navigate("clinic_owner_edit_profile") {
                        launchSingleTop = true
                    }
                },
                onServicesManagement = {
                    navController.navigate("clinic_owner_services_management") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("clinic_owner_logout") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("clinic_owner_edit_profile") {
            ClinicOwnerEditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProfilePictureClick = {
                    // TODO: Handle profile picture click - open camera or gallery
                }
            )
        }
        
        composable("clinic_owner_services_management") {
            ClinicOwnerServicesManagementScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddService = {
                    // TODO: Navigate to add service screen when created
                },
                onEditService = { serviceId ->
                    // TODO: Navigate to edit service screen when created
                },
                onDeleteService = { serviceId ->
                    // TODO: Handle delete service
                }
            )
        }
        
        composable("clinic_owner_logout") {
            ClinicOwnerLogoutScreen(
                onBack = {
                    navController.popBackStack()
                },
                onYesLogout = {
                    // Navigate to login screen and clear back stack
                    navController.navigate("clinic_owner_login") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("quick_search") {
            QuickSearchScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("ai_photo_matching") { backStackEntry ->
            AIPhotoMatchingScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCapture = {
                    // Handled internally by the screen
                },
                onUpload = {
                    // Handled internally by the screen
                },
                onEnhance = {
                    // Handle photo enhancement
                },
                onMatchFound = { imageUri, comparisonResult ->
                    // Save data to navigate to results screen using JSON serialization
                    val gson = Gson()
                    backStackEntry.savedStateHandle["image_uri"] = imageUri.toString()
                    backStackEntry.savedStateHandle["comparison_result_json"] = gson.toJson(comparisonResult)
                    navController.navigate("ai_matching_results") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("ai_analysis") {
            AIAnalysisScreen(
                onBack = {
                    navController.popBackStack()
                },
                onMatchClick = { petName ->
                    navController.navigate("match_details")
                }
            )
        }
        
        composable("ai_matching_results") { backStackEntry ->
            val imageUriString = backStackEntry.savedStateHandle.get<String>("image_uri")
            val comparisonResultJson = backStackEntry.savedStateHandle.get<String>("comparison_result_json")
            
            if (imageUriString != null && comparisonResultJson != null) {
                val imageUri = Uri.parse(imageUriString)
                val gson = Gson()
                val comparisonResult = try {
                    gson.fromJson(comparisonResultJson, ImageComparisonResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                
                if (comparisonResult != null) {
                    AIMatchingResultsScreen(
                        uploadedImageUri = imageUri,
                        comparisonResult = comparisonResult,
                        onBack = {
                            navController.popBackStack()
                        },
                        onMatchClick = { match ->
                            // Navigate to match details
                            navController.navigate("match_details") {
                                launchSingleTop = true
                            }
                        },
                        onRetakePhoto = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    // Fallback if parsing failed
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading results")
                    }
                }
            } else {
                // Fallback if data is missing
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error loading results")
                }
            }
        }
        
        composable("match_details") {
            MatchDetailsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onConfirmMatch = {
                    navController.navigate("confirm_match")
                },
                onContactOwner = {
                    navController.navigate("pet_details_view")
                },
                onViewSideBySide = {
                    navController.navigate("view_side_by_side")
                }
            )
        }
        
        composable("pet_details_view") {
            PetDetailsViewScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCall = {
                    // Handle call
                },
                onMessage = {
                    // Handle message
                },
                onEmail = {
                    // Handle email
                },
                onContactOwner = {
                    navController.navigate("contact_owner") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("contact_owner") {
            ContactOwnerScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCall = {
                    // Handle call
                },
                onTextMessage = {
                    // Handle text message
                },
                onEmail = {
                    // Handle email
                },
                onSendMessage = {
                    // Navigate to share alert screen
                    navController.navigate("share_alert") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("share_alert") {
            ShareAlertScreen(
                onBack = {
                    navController.popBackStack()
                },
                onDoneSharing = {
                    // Navigate to home screen and clear back stack
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onShareOptionClick = { option ->
                    // Handle share option click
                },
                onCopyLink = {
                    // Handle copy link
                }
            )
        }
        
        composable("my_pets") {
            MyPetsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPetClick = { petName ->
                    // Navigate to pet profile with pet name as argument
                    navController.navigate("pet_profile/$petName") {
                        launchSingleTop = true
                    }
                },
                onAddNewPet = {
                    navController.navigate("add_pet_profile") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("pet_profile/{petName}") { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: "Max"
            PetProfileScreen(
                petName = petName,
                breed = if (petName == "Max") "Golden Retriever" else "Siamese Cat",
                age = if (petName == "Max") "3 years" else "2 years",
                onBack = {
                    navController.popBackStack()
                },
                onVaccinationsClick = {
                    navController.navigate("vaccination_schedule/$petName") {
                        launchSingleTop = true
                    }
                },
                onMedicationsClick = {
                    navController.navigate("medication_reminders/$petName") {
                        launchSingleTop = true
                    }
                },
                onNutritionClick = {
                    navController.navigate("nutrition_meal_plan/$petName") {
                        launchSingleTop = true
                    }
                },
                onGroomingClick = {
                    navController.navigate("grooming_schedule") {
                        launchSingleTop = true
                    }
                },
                onViewMedicalRecords = {
                    navController.navigate("medical_records/$petName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("add_pet_profile") {
            AddPetProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onImageClick = {
                    // Handle image click - could open camera or gallery
                },
                onCreateProfile = { petName ->
                    // Navigate to pet profile screen with the pet name
                    navController.navigate("pet_profile/$petName") {
                        // Pop back to my_pets screen, removing add_pet_profile from stack
                        popUpTo("my_pets") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = "medical_records/{petName}",
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            val shouldRefresh = backStackEntry.savedStateHandle.get<Boolean>("should_refresh_medical_records") ?: false
            MedicalRecordsScreen(
                petName = petName,
                shouldRefresh = shouldRefresh,
                onBack = {
                    navController.popBackStack()
                },
                onDocumentClick = { documentTitle ->
                    // Handle document click
                },
                onDownloadClick = { downloadUrl ->
                    // Handle download click - downloadUrl is the file URL
                },
                onUploadDocument = {
                    navController.navigate("add_medical_record/$petName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = "add_medical_record/{petName}",
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            AddMedicalRecordScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onRecordAdded = {
                    // Set refresh flag
                    navController.previousBackStackEntry?.savedStateHandle?.set("should_refresh_medical_records", true)
                    navController.popBackStack()
                }
            )
        }
        
        composable("vaccination_schedule/{petName}") { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            VaccinationScheduleScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onVaccinationClick = { vaccinationName ->
                    // Handle vaccination click
                },
                onAddVaccination = {
                    navController.navigate("add_vaccination/$petName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("add_vaccination/{petName}") { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            AddVaccinationScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onVaccinationAdded = {
                    // Navigate back - the screen will be refreshed when it regains focus
                    navController.popBackStack()
                }
            )
        }
        
        composable("medication_reminders/{petName}") { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            MedicationRemindersScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onMedicationClick = { medicationName ->
                    // Handle medication click
                },
                onAddMedication = {
                    navController.navigate("add_medication/$petName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("add_medication/{petName}") { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            AddMedicationScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onMedicationAdded = {
                    // Navigate back to medication reminders
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "nutrition_meal_plan/{petName}",
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            NutritionMealPlanScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onMealClick = { mealName ->
                    // Handle meal click
                },
                onFoodClick = {
                    // Handle food click
                },
                onDailyPlanClick = {
                    navController.navigate("add_nutrition_plan/$petName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = "add_nutrition_plan/{petName}",
            arguments = listOf(navArgument("petName") { type = NavType.StringType })
        ) { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            AddNutritionPlanScreen(
                petName = petName,
                onBack = {
                    navController.popBackStack()
                },
                onPlanAdded = {
                    // Navigate back and refresh nutrition data
                    navController.popBackStack()
                }
            )
        }
        
        composable("grooming_schedule") {
            GroomingScheduleScreen(
                onBack = {
                    navController.popBackStack()
                },
                onTaskClick = { taskName ->
                    // Handle task click
                },
                onBookAppointment = {
                    navController.navigate("book_grooming_service") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("book_grooming_service") {
            BookGroomingServiceScreen(
                onBack = {
                    navController.popBackStack()
                },
                onServiceClick = { serviceName ->
                    // Navigate to service details with service name
                    navController.navigate("service_details/$serviceName") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("service_details/{serviceName}") { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: "Central Vet Clinic"
            ServiceDetailsScreen(
                serviceName = serviceName,
                rating = 4.8f,
                reviews = 247,
                location = "123 Main St, New York",
                hours = "Mon-Fri: 8AM-6PM",
                contact = "(555) 123-4567",
                onBack = {
                    navController.popBackStack()
                },
                onBookAppointment = {
                    navController.navigate("payment") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("payment") {
            PaymentScreen(
                serviceName = "Vet Checkup",
                date = "Jan 25, 2025",
                time = "10:00 AM",
                total = "$75.00",
                cardNumber = ".... 4242",
                expiryDate = "12/25",
                onBack = {
                    navController.popBackStack()
                },
                onConfirmAndPay = {
                    // Navigate to home screen and clear back stack
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("community") {
            CommunityScreen(
                onBack = {
                    navController.popBackStack()
                },
                onShareStory = {
                    navController.navigate("share_your_story") {
                        launchSingleTop = true
                    }
                },
                onPostClick = { author ->
                    // Handle post click
                },
                onLikeClick = { author ->
                    // Handle like click
                },
                onCommentClick = { author ->
                    // Handle comment click
                }
            )
        }
        
        composable("share_your_story") {
            ShareYourStoryScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddPhoto = {
                    // Handle add photo click
                },
                onPostToCommunity = {
                    // Navigate to home screen and clear back stack
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("profile") {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditProfile = {
                    navController.navigate("edit_profile") {
                        launchSingleTop = true
                    }
                },
                onMyPets = {
                    navController.navigate("my_pets") {
                        launchSingleTop = true
                    }
                },
                onSettings = {
                    navController.navigate("settings") {
                        launchSingleTop = true
                    }
                },
                onFAQs = {
                    navController.navigate("faqs") {
                        launchSingleTop = true
                    }
                },
                onAbout = {
                    navController.navigate("about") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("logout") {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("edit_profile") {
            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProfilePictureClick = {
                    // Handle profile picture click - could open camera or gallery
                },
                onSaveChanges = {
                    // Navigate to profile screen and clear back stack
                    navController.navigate("profile") {
                        popUpTo("profile") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLostPetAlertsToggle = { enabled ->
                    // Handle lost pet alerts toggle
                },
                onLocationServicesToggle = { enabled ->
                    // Handle location services toggle
                },
                onMessagesToggle = { enabled ->
                    // Handle messages toggle
                }
            )
        }
        
        composable("faqs") {
            FAQsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("about") {
            AboutScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("logout") {
            LogoutScreen(
                onBack = {
                    navController.popBackStack()
                },
                onYesLogout = {
                    // Navigate to login screen and clear back stack
                    navController.navigate("pet_owner_login") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("view_side_by_side") {
            ViewSideBySideScreen(
                onBack = {
                    navController.popBackStack()
                },
                onConfirmMatch = {
                    navController.navigate("confirm_match")
                },
                onZoomOut = {
                    // Handle zoom out
                },
                onZoomIn = {
                    // Handle zoom in
                }
            )
        }
        
        composable("confirm_match") {
            ConfirmMatchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onConfirm = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onReviewAgain = {
                    navController.navigate("match_details") {
                        popUpTo("confirm_match") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable("report_lost_pet") {
            ReportLostPetScreen(
                onBack = {
                    navController.popBackStack()
                },
                onStartReport = {
                    navController.navigate("upload_photos")
                }
            )
        }
        
        composable("upload_photos") {
            UploadPhotosScreen(
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate("pet_details")
                },
                onCameraClick = {
                    // Handle camera click
                },
                onGalleryClick = {
                    // Handle gallery click
                },
                onRemovePhoto = { index ->
                    // Handle remove photo
                }
            )
        }
        
        composable("pet_details") {
            PetDetailsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onContinue = { lostId ->
                    // Store lost_id in savedStateHandle for identification screen
                    navController.currentBackStackEntry?.savedStateHandle?.set("lost_id", lostId)
                    navController.navigate("identification")
                }
            )
        }
        
        composable("identification") { backStackEntry ->
            val lostId = backStackEntry.savedStateHandle.get<Int>("lost_id") ?: -1
            IdentificationScreen(
                lostId = lostId,
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate("last_seen_location")
                },
                onSkip = {
                    navController.navigate("last_seen_location")
                }
            )
        }
        
        composable("last_seen_location") {
            LastSeenLocationScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSubmitReport = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onUseCurrentLocation = {
                    // Handle use current location
                }
            )
        }
        
        composable("notifications") {
            NotificationsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onMarkAllRead = {
                    // Handle mark all as read
                },
                onNotificationClick = { notificationId ->
                    // Handle notification click - navigate based on type
                }
            )
        }
    }
}
