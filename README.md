# 📱 Measuring Distances in 3D with AR Core

This guide outlines the steps to establish a wireless ADB bridge between a physical Android device and a containerized Python environment for Computer Vision / AR development.

## 1. Prerequisites

- **Container:** Docker + VS Code Dev Containers
- **Device:** Android Device with Google Play Services AR support
- **Network:** Devices should be connected on the same network.

## 2. Environment Configuration

Create a `.env` file in the project root:

```bash
API_SECRET_KEY=your_generated_hex_here
ANDROID_DEVICE_IP=your_android_device_ip
ADB_WIFI_PORT=5555
PROJECT_MODE=development
PORT=8000
```

## 3. The Wireless Handshake (Step-by-Step)

### Step A: Clean the Host

Run the `init-mobile.sh` to get the necessary dependencies for gradle build

```bash
chmod +x init-mobile.sh
./init-mobile.sh
```

Kill any existing ADB processes on the Linux Mint host to prevent port conflicts:
Bash

```bash
adb kill-server
sudo pkill -9 adb
```

### Step B: Establish Network Bridge

Enable developer options on the android device and toggle wireless debugging On. Select the option to Pair with code to get the IP address and Port. On the container terminal pair with the device:

```bash
adb pair <IP>:<PORT>
# Provide the pairing code when requested
```

Next, in the Dev Container Terminal, connect via the device IP and Port specified on the wireless debugging page:

```bash
    adb connect <IP>:<PORT>
```

To verify the currently connected devices run the following:

```bash
    adb devices
    # It should list the device identification followed by the 'device' indicator
```

## 4. Hardware Verification

Run this to ensure the container "owns" the device and can read its properties:

```bash
adb shell getprop ro.product.model
```

## 5. Install the application on the device

```bash
cd ./mobile
./gradlew installdebug
# await for the build successful message
```

## 6. Run the application on the device

Look for an application with the Android icon on the device. Open it and await 5 seconds and then do slight smooth horizontal moves around the environment. Tap on the screen to anchor a 3D sphere. It should display a message Toast. Tap on a different point. It should display a second Toast message and the approximate distance between the two spheres on the top right corner.
