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
    const [isLoading, setLoading] = useState(false)
    const [isLocationMocked, setIsLocationMocked] = useState<boolean>()
    const { startPermissionFlow, isEnabled } = usePermission(LOCATION_PERMISSION as Permission)
    const checkIfLocationIsMocked = () => {
        setLoading(true)

        isMockingLocation()
            .then(result => setIsLocationMocked(result.isLocationMocked))
            .catch((error: MockLocationDetectorError) => console.log(JSON.stringify(error)))
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        if (!isEnabled) {
            startPermissionFlow()

            return
        }
    }, [isEnabled, startPermissionFlow])

    const getMockState = () => {
        if (isLocationMocked === undefined) {
            return '-'
        }

        return isLocationMocked
            ? 'mocked'
            : 'not mocked'
    }

    return (
        <View style={styles.container}>
            {isLoading
                ? (
                    <ActivityIndicator />
                )
                : (
                    <View>
                        <Text style={styles.result}>
                            Location is {getMockState()}
                        </Text>
                        <Button
                            title="Try again"
                            onPress={() => {
                                setIsLocationMocked(undefined)
                                checkIfLocationIsMocked()
                            }}
                        />
                        <Button
                            title="Try again 10 times"
                            onPress={() => {
                                Array.from(new Array(10))
                                    .forEach(() => {
                                        setIsLocationMocked(undefined)
                                        checkIfLocationIsMocked()
                                    })
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
        backgroundColor: 'white',
    },
    box: {
        width: 60,
        height: 60,
        marginVertical: 20,
    },
    result: {
        textAlign: 'center',
    },
})
