# OnboardBeacon

An Android application that uses **BLE (Bluetooth Low Energy) beacons** and **GPS location services** to help bus passengers identify nearby buses, board the correct one, and track the next upcoming stop in real time.

## Overview

OnboardBeacon solves the problem of bus identification and stop tracking by combining beacon-based bus detection with GPS-powered next-stop calculation. Passengers can scan for nearby buses broadcasting BLE signals, select their bus, view its route, and get real-time updates on the nearest stop.

## Features

- **Beacon-Based Bus Detection** -- Scans for nearby BLE beacons (UUID: `74278bda-b644-4520-8f0c-720eaf059935`) and displays detected buses within a 3-second scan window.
- **Manual Bus Entry** -- Fallback option to manually enter a bus number when beacon detection isn't available.
- **Auto Mode** -- Maintain a watchlist of specific buses stored in SharedPreferences. The app auto-detects only those buses and alerts the user.
- **Bus Route Display** -- Shows the full list of stops for a selected bus.
- **Next Stop Calculation** -- Uses Google Fused Location Provider to determine the user's GPS position and calculates the nearest upcoming stop.
- **Text-to-Speech Announcements** -- Voice feedback when a bus is selected.
- **BLE GATT Communication** -- Establishes a Bluetooth GATT connection to the beacon device and writes data to signal boarding/offboarding.

## Tech Stack

| Component          | Technology                                    |
|--------------------|-----------------------------------------------|
| Platform           | Android (Java)                                |
| Min SDK            | API 21 (Android 5.0 Lollipop)                |
| Target SDK         | API 26 (Android 8.0 Oreo)                    |
| Beacon Library     | [AltBeacon](https://altbeacon.github.io/android-beacon-library/) 2.x |
| Estimote SDK       | Estimote SDK 1.0.3                            |
| Location Services  | Google Play Services 9.0.2 (Fused Location)  |
| UI                 | AppCompat + ConstraintLayout                  |
| Build System       | Gradle 2.3.3                                  |

## Project Structure

```
app/src/main/
├── java/com/example/adityashekhar/onboardbeacon/
│   ├── MainActivity.java           # Beacon scanning & bus list
│   ├── MainActivity2.java          # Manual bus number entry
│   ├── RestrictedActivity.java     # Auto mode with bus watchlist
│   ├── DisplayMessageActivity.java # Displays stops for a selected bus
│   ├── NextStopActivity.java       # GPS-based next stop calculation
│   ├── Bus.java                    # Bus data model
│   ├── BusList.java                # Bus collection manager
│   ├── stop.java                   # Bus stop data model (name + lat/long)
│   ├── stoplist.java               # Stop collection manager
│   └── Constants.java              # App-wide configuration constants
├── res/
│   ├── layout/                     # Activity XML layouts
│   ├── values/                     # Colors, strings, styles
│   └── mipmap-*/                   # App launcher icons
└── AndroidManifest.xml             # Permissions & activity declarations
```

## App Flow

```
MainActivity (Beacon Scan)
├── Scan → Detect buses → Select → GATT connect → DisplayMessageActivity
├── OFF-BOARD → MainActivity2 (manual entry) → DisplayMessageActivity
└── AUTO MODE → RestrictedActivity (watchlist) → NextStopActivity

DisplayMessageActivity (Route View)
└── Next Stop → NextStopActivity (GPS-based nearest stop)
```

## Permissions

| Permission              | Purpose                                |
|-------------------------|----------------------------------------|
| `BLUETOOTH`             | BLE scanning and device connection     |
| `BLUETOOTH_ADMIN`       | BLE administrative operations          |
| `ACCESS_FINE_LOCATION`  | GPS-based location for next stop       |
| `ACCESS_COARSE_LOCATION`| Network-based location fallback        |

The app also declares `android.hardware.bluetooth_le` as a required hardware feature.

## Configuration

Key constants are defined in `Constants.java`:

| Constant           | Value    | Description                      |
|--------------------|----------|----------------------------------|
| `SCAN_PERIOD`      | 3000 ms  | BLE beacon scan duration         |
| `connectionTime`   | 1200 ms  | GATT connection timeout          |
| `sendData`         | `"6"`    | Data written to beacon device    |

## Getting Started

### Prerequisites

- Android Studio
- Android SDK (API 21+)
- A physical Android device with BLE support (beacons cannot be tested on emulators)
- BLE beacon hardware (e.g., Estimote beacons) configured with the expected UUID

### Build & Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Connect a physical Android device with Bluetooth enabled.
5. Build and run the app.

### Sample Data

The app includes hardcoded sample bus data (bus `"123"`) with four stops:

| Stop       | Latitude   | Longitude  |
|------------|------------|------------|
| Main Gate  | 28.545766  | 77.196457  |
| LHC        | 28.544623  | 77.192348  |
| Bharti     | 28.544623  | 77.190348  |
| Hospital   | 28.545499  | 77.188210  |

## License

This project is provided as-is for educational and demonstration purposes.
