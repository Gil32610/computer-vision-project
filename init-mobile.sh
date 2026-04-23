#!/bin/bash
cd mobile
chmod -R 777 .

# 1. Download the wrapper files if they don't exist
if [ ! -f "gradlew" ]; then
    mkdir -p gradle/wrapper
    wget https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar
    wget https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties -O gradle/wrapper/gradle-wrapper.properties
    curl -sL https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradlew -o gradlew
    chmod +x gradlew
fi

# 2. Setup SDK Environment
echo "sdk.dir=/opt/android-sdk" > local.properties

# ACCEPT LICENSES FIRST
yes | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --licenses

# INSTALL COMPONENTS
yes | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-34"

# 3. Build
./gradlew clean 
./gradlew assembleDebug