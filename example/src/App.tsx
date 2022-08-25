import React, { useEffect, useState } from 'react'
import { StyleSheet, View, Platform, Text, ActivityIndicator } from 'react-native'
import { isMockingLocation } from 'react-native-mock-location-detector'
import { PERMISSIONS, Permission } from 'react-native-permissions'
import { usePermission } from './usePermission'

const LOCATION_PERMISSION = Platform.select({
    ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE,
    android: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
})

export const App = () => {
    const [isLocationMocked, setIsLocationMocked] = useState<boolean>()
    const { startPermissionFlow, isEnabled } = usePermission(LOCATION_PERMISSION as Permission)

    useEffect(() => {
        if (isEnabled) {
            isMockingLocation()
                .then(result => setIsLocationMocked(result.isLocationMocked))
                .catch(error => console.log(error.message))

            return
        }

        startPermissionFlow()
    }, [isEnabled, startPermissionFlow])

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
                    <Text>
                        Location is {getMockState()}
                    </Text>
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
