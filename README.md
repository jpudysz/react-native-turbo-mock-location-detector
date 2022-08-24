# react-native-mock-location-detector

### TODO
- ◻️ Detects mocked location on iOS
- ◻️ Detects mocked location on Android
- ◻️ Supports Fabric architecture
- ◻️ fallback for iOS < 15.0

## Installation

```sh
yarn add react-native-mock-location-detector
cd ios && pod install
```

## Usage

```typescript
import { isMockingLocation } from 'react-native-mock-location-detector'

useEffect(() => {
    isMockingLocation()
        .then(result => {
            // result.isLocationMocked: boolean
            // boolean result for Android and iOS >= 15.0
        })
        .catch(error => {
            // error.message
            // no GPS enabled
            // no permission to access location
            // iOS lower than 15.0
        })
}, [])

```

## Support

| Lib version | RN version |
|-------------|------------|
| 0.1         | >0.68.0    |


*Library may work on lower versions of React Native, but it wasn't tested!*

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---
