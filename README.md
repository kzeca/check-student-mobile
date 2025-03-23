# check-student-mobile

## Overview
Check-Student-Mobile is a native Android application developed in Java designed to register students for Fundação Matias Machline school remote classes during the COVID-19 pandemic. The app connects to Firebase to store and retrieve student attendance data.

## Purpose
This application was created as a response to the challenges posed by the COVID-19 pandemic, providing a contactless solution for tracking student attendance at Fundação Matias Machline. It helps school administrators maintain accurate records of which students attended remote classes during this critical period.

## Features
- Student registration and authentication
- Class attendance tracking
- Real-time data synchronization with Firebase
- Attendance history and reporting
- User-friendly interface optimized for quick check-ins
- Admin dashboard for monitoring attendance statistics

## Technical Implementation
- **Frontend**: Native Android application written in Java
- **Backend**: Firebase Realtime Database and Authentication
- **Data Storage**: Cloud-based storage for student records and attendance data
- **Security**: Role-based access control and data encryption

## Getting Started

### Prerequisites
- Android Studio
- Firebase account
- Android device running Android 5.0 (Lollipop) or higher

### Installation
1. Clone this repository
2. Open the project in Android Studio
3. Connect to your Firebase project by adding your `google-services.json` file
4. Build and run the application

## Configuration
The application requires proper Firebase configuration for both authentication and data retrieval/storage. Make sure to set up the appropriate Firebase services in your project console.

## Usage
1. Students register their information in the app
2. Teachers or administrators create class sessions
3. Students check in to classes using the app
4. Attendance data is synchronized with Firebase in real-time
5. Administrators can access reports and analytics through the dashboard

## Contributors
- José Carlos Peixoto Leão (kzeca) -> Android Developer
- Arley Novais do Nascimento (novaisarley) -> Android Developer
- Pedro Araújo (pedroaraujo1952) -> Web developer

## Acknowledgments
- Developed for Fundação Matias Machline
- Created as a solution for maintaining educational continuity during the COVID-19 pandemic
