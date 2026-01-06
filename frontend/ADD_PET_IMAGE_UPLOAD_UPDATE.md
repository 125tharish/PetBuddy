# Add Pet Profile - Image Upload Update

## Overview
Updated the Add Pet Profile screen to work with the PHP backend that expects multipart file uploads instead of base64 encoded images.

## Changes Made

### 1. ApiService.kt
- Changed from `@FormUrlEncoded` to `@Multipart`
- Updated `addPet()` method to accept:
  - `RequestBody` for text fields (user_id, pet_name, pet_type, breed, color, age, microchip_id)
  - `MultipartBody.Part` for the image file (pet_image)

### 2. PetRepository.kt
- Updated `addPet()` method to:
  - Accept `Uri?` for photo instead of base64 string
  - Accept `Context` to handle file operations
  - Convert URI to File for both camera and gallery images
  - Create `MultipartBody.Part` from the file
  - Handle both `file://` URIs (from camera) and `content://` URIs (from gallery)

### 3. AddPetProfileScreen.kt
- Removed base64 conversion logic
- Updated to pass `photoUri` and `context` directly to repository
- Simplified the upload process

## PHP Backend Requirements

The PHP backend (`add_pet.php`) expects:
- **POST fields**: `user_id`, `pet_name`, `pet_type`, `breed`, `color`, `age`, `microchip_id`
- **File upload**: `pet_image` (multipart/form-data)
- **Response**: JSON with `status`, `message`, and `pet_image_url`

## How It Works

1. User selects/captures a photo in AddPetProfileScreen
2. Photo URI is stored in state
3. When user clicks "Create Profile":
   - Repository receives the URI and context
   - URI is converted to a File:
     - Camera photos: Direct file path from URI
     - Gallery photos: Copied to cache directory
   - File is converted to `MultipartBody.Part`
   - All fields are sent as multipart form data to PHP backend
4. PHP backend saves the file and returns the image URL

## Testing

1. **Camera Upload**:
   - Open Add Pet Profile
   - Click camera icon
   - Take a photo
   - Fill in pet details
   - Click "Create Profile"
   - Verify image is uploaded and displayed

2. **Gallery Upload**:
   - Open Add Pet Profile
   - Click camera icon
   - Select "Gallery"
   - Choose an image
   - Fill in pet details
   - Click "Create Profile"
   - Verify image is uploaded and displayed

## File Structure

```
app/src/main/java/com/example/petbuddy/
├── data/
│   ├── api/
│   │   └── ApiService.kt (updated)
│   └── repository/
│       └── PetRepository.kt (updated)
└── ui/theme/home/
    └── AddPetProfileScreen.kt (updated)
```

## Notes

- The image file is temporarily stored in the app's cache directory for gallery images
- Camera images use the direct file path from FileProvider
- File is sent as `image/jpeg` MIME type
- The PHP backend handles image validation (JPG, JPEG, PNG only)
- PHP backend creates the `pets_images/` directory if it doesn't exist

