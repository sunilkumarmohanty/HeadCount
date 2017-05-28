# CS-E4002 - Special Course in Computer Science: The Internet of Things: selected themes

# SP5: ResinOS on Intel Edison

The aim of this software project is to develop an IoT use case, enabled by deploying ResinOS on the Intel Edison. ResinOS is an operating system optimized for running Docker containers on embedded devices.

# Project Title: HeadCount
## Team members:
  - Rajagopalan Ranganathan
  - Sunil Kumar Mohanty

## Project Description
### Overview

The aim of the project is to get the total number of people at given location at any point of time. The solution uses multiple IP enabled cameras to monitor the place(s) of interest. The cameras are connected to an IoT gateway (Intel Edison board). The gateway connects to the cameras and takes pictures at regular time intervals. The gateway performs the face detection and gets the count of faces. The gateway has a business logic to send the data to the cloud (Google Firebase) only if the number of faces has changed. Ex: last sent data has - 3 faces, and recent data is 3 faces, this data is not sent to the cloud. This minimizes the network traffic and enhances the performance of the Gateway. Once the data reaches the cloud, the back-end fires a trigger (firebase cloud function) to map the latest value to the specific camera. The Android application gives an interface to display the face count data and manage the cameras.

### High Level Architecture

The high level architecture of the solution is depicted in the Figure below.

![](readme-res/arch.png?raw=true)

There are multiple IP enabled cameras that connect to the IoT Gateway. The IP enabled cameras could be a commercial IP enabled cameras used for CCTV coverage, general IP cameras or smart phones with cameras. These cameras are identified by their IP address and are first provisioned in the cloud with the help of the "HeadCount" Android application. The IoT gateway connects to the cloud at preconfigured time intervals (30 seconds in this case) to get the list of IP cameras that the gateway should connect to. Once the gateway receives the list of cameras, it connects to each of these cameras on a round robin fashion. Then it gets the image from each of the cameras and does the face detection using OpenCV library. The gateway detects the number of faces and checks if the current values differs from the last count. If it does, the data is sent to the cloud and processed and stored there.

The Android application, authenticates the user with a user-name and password against the cloud. It fetches the data of the cameras if any. There is provision to add, remove and edit any camera information.

### Features

####IoT Gateway - Intel Edison (Resin IO)
- Downloads the list of cameras from cloud
- Connects to the cameras and gets the images at regular intervals
- Performs face detection on the received images
- Sends the results back to cloud

#### Cloud - Google Firebase
- Stores camera details
- Stores headcount
- Firebase cloud function to read the latest headcount for each camera and saves the same

#### Android Application
- User authentication
- Manage cameras
  - Add cameras
  - Edit Cameras
  - Delete cameras
- Displays the latest headcount per camera (real-time data)
- Graphs - graphical representation of headcount for the last hour


### Technical Specification

####IoT Gateway - Intel Edison (Resin IO)
- Hardware - Intel Edison
- Operating System - Resin IO (resin/intel-edison-python:latest)
- Langugages
  - Python 2.7.12
  - Open Source Python packages: opencv-python, python-Firebase


#### Cloud
- Google Firebase
- Firebase function written using Node.js

#### Android Application
- Native Android application
- firebase-auth:10.0.1
- github.PhilJay:MPAndroidChart:v3.0.2

### Project Structure

Root Folder: CS-E4002-IOT-Project-HeadCount

There are 3 primary sub folders:
- Android-Application - Contains the Android application code

   -src folder contains the android application code

- Firebase-back-end - Contains the Firebase cloud function source code
   - index.js - contains the Firebase cloud function

- IOT-Gateway - Contains the code to be run on the Intel Edison Board
   - cse4002 - this is the name of the application in Resin.IO
   - head-count.py - runs the main application logic for the gateway

The detailed folder structure is shown in the Figure below.

![](readme-res/folder.png?raw=true)

### Build/Deploy

#### Android Build

To build and generate android apk file

Run CS-E4002-IOT-Project-HeadCount/Android-App/buildapks.sh

#### IOT-Gateway Build

User accounts need to be created in Resin.IO and should be added as a collaborator for the project.

Code is pushed to ResinIO git repository. ResinIO's CI creates a Docker container image from the DockerFile and deploys it on the Edison board.

#### Firebase Build

Open a terminal in your local host and go the path "Firebase-backend/Firebase"

$ npm install -g firebase-tools

$ firebase login # to login via the browser and authenticate the Firebase tool.

$ firebase init functions

$ firebase deploy --only functions
