# Automated Video Surveillance System

Navigation-Drawer - Holds the Android side of the project
VideoSurveillance - Raspberry Pi side of the project


The project consists of two parts:
   - Raspberry Pi
   - Smartphone
   
The Raspberry Pi has attached to it:
   - One dedicated camera
   - One servomotor
   
   
<h2>Key software technologies:</h2>

   * Python  
   * OpenCV
   * Flask 
   * Android / Java
   * Firebase Authentication
   * Firebase Storage
   * Firebase Realtime Database
   * ZeroTier One

<h2>SUMMARY</h2>

<h4>Raspberry Pi</h4>

 The Raspberry Pi system is responsible with the video frame aquisition and motion detection. For motion detection the OpenCV library is used.
 The captured frames are analyzed one by one and when significant changes among the the pixels are detected the system concludes motion had been detected.
 The program continues by recording the flow of frames which continues untill no motion is detected in a predefined time interval.
 Simultaneously the user is notified by having a SMS sent to the configured phone number.
 This entire process continues indefinitely unless the program is manually terminated.

 Firebase Storage is used to store the generated videos, which are categorized by the period in which have been uploaded for later convenient access by the user.
 However, storing records is only one part of the equation. Each location of the videos is stored on Firebase Realtime Database.
 
 <h4>Android</h4>
 
 The other part of the project consists of an application dedicated for Android. This application aims to monitor the real time activity recorded by the Raspberry Pi camera module and allowing the user 
 to access all recordings uploaded to Firebase Storage. Along with these features it is also possible to change the camera angle since the camera is attached to a servomotor.
 
 Key functionalities:
 * Authentication
 * Receiving and accessing the live stream from the Raspberry Pi
 * Filtering information based on date and name
 * Deleting files
 * Downloading files locally
 * The possibility to access recordings
 * Extended video list display
 






