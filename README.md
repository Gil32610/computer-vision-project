# 📱 ARCore to Docker-FastAPI Infrastructure Setup

This guide outlines the steps to establish a wireless ADB bridge between a physical Android device and a containerized Python environment for Computer Vision / AR development.

## 1. Prerequisites
- **Host OS:** Linux Mint (or any Debian-based distro)
- **Container:** Docker + VS Code Dev Containers
- **Device:** Samsung Galaxy A34 / Tab S9 FE
- **Network:** Mobile Hotspot (Point-to-Point)

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

Kill any existing ADB processes on the Linux Mint host to prevent port conflicts:
Bash

```bash
adb kill-server
sudo pkill -9 adb
```

### Step B: Trigger TCP Mode (USB)

    - Plug the phone into the laptop via USB.

    - In the Dev Container Terminal (VS Code), run:
    

```bash
    adb tcpip 5555
```
   - Disconnect the USB cable once you see the message: restarting in TCP mode port: 5555.

### Step C: Establish Network Bridge

    Ensure the phone's Hotspot is active and the laptop is connected to it.

    Inside the Dev Container Terminal, connect via the device IP:
    Bash
```bash
    adb connect 172.20.145.195:5555
```
    Verification:

```
    adb devices
    # Should list: 172.20.145.195:5555 device
```

## 4. Hardware Verification

Run this to ensure the container "owns" the device and can read its properties:
Bash
```bash
adb shell getprop ro.product.model
```