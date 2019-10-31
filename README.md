# Hushed
This repository contains code for a peer to peer encrypted communication app developed in Kotlin for Android users.

The goal of this app is to provide effective but encrypted communication between two app users.

The app attempts to target most Android users by attempting to keep the minimum API level as low as possible.

## SDK Versions
-  Compiled SDK Version
  - 29
- Minimum SDK Version
  - 19
- Target SDK Version
  - 29

## Tools Used
- Android Studio
- Firebase Firestore

## Project Setup
- Clone the repository
- Place the google-service.json file into the <git_repo>\app directory
  - Ex. C:\git_repo\Hushed\app
  - The google-service.json file allows the app to successfully connect to the proper Firebase Firestore
- In Android Studio, click the 'File' dropdown and select the 'Open' option
- In the 'Open File or Project' window, navigate to the directory created when cloning the project
- Open the 'Hushed' project to open the entire project directory structure
- Allow project to sync
  - Syncing may fail with a missing or faulty google-service.json file

## Project Development (Android Studio)
### App Deployment
#### To compile and build the app
- On top of Android Studio is a 'Make project' option, click on this to compile and build the project
  - Project can also be compiled and built by navigating to the 'Build' dropdown on top of Android Studio and selecting the 'Make project' option

#### To compile, build, and run the app
- First choose the desired device to run the app on
  - App requires a Android device to run on
  - App can be ran on a physical or virtual Android device
- Desired device can be chosen by navigating to the dropdown next to the 'Run app' option
  - Virtual Android device can be created by navigating to the 'Open AVD manager' option, selecting the 'Create virtual device' option and following the prompts to create the desired virtual device
  - Virtual Android device can also be created by navigating to the 'Tools' dropdown on top of Android studio, selecting the 'AVD Manager' option, selecting the 'Create virtual device' option and following the prompts to create the desired virtual device
- On top of Android Studio is a 'Run app' option, click on this to compile, build and run the project
  - Project can also be compiled, built, and ran by navigating to the 'Run' dropdown on top of Android Studio and selecting the 'Run app' option
- After the app has been succesfully compiled and built, it will automatically deploy onto the desired Android device
- The app can now be used and navigated through in the desired Android device

### Debugging
- Android Studio allows for the attachment of a debugger
- To start the debugger, first ensure the desired Android device is chosen
- Once the desired device has been chosen, start the debugger by selecting the 'Debug app' option
  - Debugger can also be started by navigating to the 'Run' dropdown on top of Android Studio and selecting the 'Debug app' option
- This option will first compile and build the app.  Once the project has been successfully compiled and built, a debugger will be attached to device.  Lastly the app will start on the desired device
- With the debugger attached to the device, when development you can set breakpoints, view dump logs, and view the app processess