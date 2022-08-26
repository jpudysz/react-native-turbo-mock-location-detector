# react-native-turbo-mock-location-detector

Library was written with TurboModules and Codegen. It also supports both old and new architectures.

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
import { isMockingLocation, MockLocationDetectorErrorCode, MockLocationDetectorError } from 'react-native-mock-location-detector'

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
                    // for android if coudln't fetch GPS position
                }
            }
        })
}, [])

```

## Support

| Lib version | RN version |
|-------------|------------|
| 1.0         | >= 0.68.0  |

## Testing
Library was tested:
- on iOS with the use of [Location Changer](https://www.ultfone.com/ios-location-changer.html)
- on Android with the use of [Mock Locations](https://play.google.com/store/apps/details?id=ru.gavrikov.mocklocations&hl=pl&gl=US)



*Library may work on lower versions of React Native (with paper architecture), but it wasn't tested!*

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---
