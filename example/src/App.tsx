import React, { useEffect, useState } from 'react'
import { StyleSheet, View, Platform, Text, ActivityIndicator, Button } from 'react-native'
import { isMockingLocation, MockLocationDetectorError } from 'react-native-mock-location-detector'
import { PERMISSIONS, Permission } from 'react-native-permissions'
import { usePermission } from './usePermission'

const LOCATION_PERMISSION = Platform.select({
    ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE,
    android: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
})

export const App = () => {
    const [isLocationMocked, setIsLocationMocked] = useState<boolean>()
    const { startPermissionFlow, isEnabled } = usePermission(LOCATION_PERMISSION as Permission)
    const checkIfLocationisMocked = () => {
        isMockingLocation()
            .then(result => setIsLocationMocked(result.isLocationMocked))
            .catch((error: MockLocationDetectorError) => console.log(JSON.stringify(error)))
    }

    useEffect(() => {
        if (isEnabled && isLocationMocked === undefined) {
            checkIfLocationisMocked()

            return
        }

        startPermissionFlow()
    }, [isEnabled, isLocationMocked, startPermissionFlow])

    const getMockState = () => {
        return isLocationMocked
            ? 'mocked'
            : 'not mocked'
    }

    return (
        <View style={styles.container}>
            {isLocationMocked === undefined
                ? (
                    <ActivityIndicator />
                )
                : (
                    <View>
                        <Text>
                            Location is {getMockState()}
                        </Text>
                        <Button
                            title="Try again"
                            onPress={() => {
                                setIsLocationMocked(undefined)
                                checkIfLocationisMocked()
                            }}
                        />
                    </View>
                )
            }
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    box: {
        width: 60,
        height: 60,
        marginVertical: 20,
    },
})
