import { NativeModules, Platform } from 'react-native'

const LINKING_ERROR =
  'The package \'react-native-mock-location-detector\' doesn\'t seem to be linked. Make sure: \n\n' +
  Platform.select({ ios: '- You have run pod install\'\n', default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n'

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null

const MockLocationDetectorModule = isTurboModuleEnabled
  ? require('./NativeMockLocationDetector').default
  : NativeModules.MockLocationDetector

const MockLocationDetector = MockLocationDetectorModule
  ? MockLocationDetectorModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR)
        },
      }
    )

type MockLocationDetectorResult = {
    isLocationMocked: boolean
}

export enum MockLocationDetectorErrorCode {
    GPSNotEnabled = '0',
    NoLocationPermissionEnabled = '1',
    CantDetermine = '2'
}

export type MockLocationDetectorError = {
    message: string,
    code: MockLocationDetectorErrorCode
}

export const isMockingLocation = (): Promise<MockLocationDetectorResult> => {
    return MockLocationDetector
        .isMockingLocation()
        .catch(({ code, message }: MockLocationDetectorError) => {
            throw {
                code: parseInt(code, 10),
                message,
            }
        })
}
