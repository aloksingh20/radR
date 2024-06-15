# Product Scanner App

## Overview

This application allows users to scan products using their device camera, processes the scanned images with Google Gemini Pro, and stores the product details in Firebase. The app is built using MVVM architecture and Clean Architecture principles for maintainability and scalability.

## Features

- **Scanner Screen**: A screen with a button to scan products using the device camera.
- **Integration with Google Gemini Pro**: Processes scanned images to extract product details.
- **Firebase Integration**: Stores response JSON (Product Name, Description, Colour, Pattern) from Gemini Pro into Firebase.
- **Display Scanned Products**: A screen displaying all scanned products with their images.
- **Product Detail Screen**: Detailed view of individual product details.
- **Rescan Functionality**: Allows the user to rescan an image if needed.
- **Flashlight Feature**: Integrated flashlight for low-light conditions during scanning.
- **Custom Prompts**: Custom prompts for actions such as confirmation before deleting a product.
- **MVVM and Clean Architecture**: Ensures maintainability and testability of the code.
- **Coil Image Library**: Efficient image loading and display from Firebase.


## APK Download

You can download the APK for the application from the following link:
[APK Download](https://drive.google.com/drive/folders/1URLe-iufiZYR5PLT-xVkAgZx978-PpbU?usp=drive_link)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/product-scanner-app.git
