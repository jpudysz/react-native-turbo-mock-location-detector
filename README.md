# react-native-turbo-mock-location-detector

Protect your business from location spoofing. Mock location detector is designed to detect and prevent any attempts to spoof GPS location, making it the perfect solution for any use case that requires accurate location tracking. Common examples include fleet management and logistics apps where truck drivers may use tools like [Location Changer](https://www.ultfone.com/ios-location-changer.html) and [Mock Locations](https://play.google.com/store/apps/details?id=ru.gavrikov.mocklocations&hl=pl&gl=US).

Library supports both new and old architecture of React Native.

## Installation

```sh
yarn add react-native-turbo-mock-location-detector
cd ios && pod install
```

## Prerequisites

1. Use eg. [react-native-permissions](https://github.com/zoontek/react-native-permissions) to ask for location permission
2. In your AndroidManifest.xml use at least one of:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
3. In your Info.plist use:
```text
<key>NSLocationWhenInUseUsageDescription</key>
<string>YOUR TEXT</string>
```

## Usage

```typescript
import { isMockingLocation, MockLocationDetectorErrorCode, MockLocationDetectorError } from 'react-native-turbo-mock-location-detector'

useEffect(() => {
    isMockingLocation()
        .then(({ isLocationMocked }) => {
            // isLocationMocked: boolean
            // boolean result for Android and iOS >= 15.0
        })
        .catch((error: MockLocationDetectorError) => {
            // error.message - descriptive message
            switch (error.code) {
                case MockLocationDetectorErrorCode.GPSNotEnabled: {
                    // user disabled GPS
                    return
                }
                case MockLocationDetectorErrorCode.NoLocationPermissionEnabled: {
                    // user has no permission to access location
                    return
                }
                case MockLocationDetectorErrorCode.CantDetermine: {
                    // always for iOS < 15.0
                    // for android and iOS if couldn't fetch GPS position
                }
            }
        })
}, [])

```

## Support

| Lib version | RN version       |
|-------------|------------------|
| 1.0 - 1.2   | 0.68, 0.69, 0.70 |
| 2.0         | 0.71             |


## Testing
Library was tested:
- on iOS with the use of [Location Changer](https://www.ultfone.com/ios-location-changer.html)
- on Android with the use of [Mock Locations](https://play.google.com/store/apps/details?id=ru.gavrikov.mocklocations&hl=pl&gl=US)


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---
