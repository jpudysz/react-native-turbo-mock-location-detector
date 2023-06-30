import { Alert } from 'react-native'
import { useCallback, useEffect, useState } from 'react'
import { request, check, RESULTS, openSettings } from 'react-native-permissions'
import type { Permission, PermissionStatus } from 'react-native-permissions'

// use Platform.select to switch between iOS/Android permissions
export const usePermission = (permission: Permission) => {
    const [status, setStatus] = useState<PermissionStatus>()

    useEffect(() => {
        if (!permission) {
            return
        }

        check(permission)
            .then(setStatus)
    }, [permission])

    useEffect(() => {
        if (permission) {
            check(permission)
                .then(setStatus)
        }
    }, [permission])

    const showOpenSettingsModal = useCallback((onError?: () => void) => {
        Alert.alert('Oops!', 'You need to open settings manually', [
            {
                text: 'Cancel',
                style: 'destructive',
                onPress: () => {
                    if (onError) {
                        onError()
                    }
                },
            },
            {
                text: 'Open settings',
                onPress: openSettings,
            },
        ])
    }, [])

    return {
        isEnabled: status !== undefined
            ? !permission || status === RESULTS.GRANTED || status === RESULTS.LIMITED
            : null,
        startPermissionFlow: (onSuccess?: () => void, onError?: () => void) => {
            if (!permission) {
                return onSuccess
                    ? onSuccess()
                    : undefined
            }

            const shouldOpenSettings = status === RESULTS.BLOCKED
            const shouldRequestPermission = status === RESULTS.DENIED

            if (shouldOpenSettings) {
                return showOpenSettingsModal(onError)
            }

            if (shouldRequestPermission) {
                return request(permission)
                    .then(result => {
                        setStatus(result)

                        result === RESULTS.GRANTED || result === RESULTS.LIMITED
                            ? onSuccess ? onSuccess() : undefined
                            : onError ? onError() : undefined
                    })
                    .catch(() => {
                        if (onError) {
                            onError()
                        }
                    })
            }

            if (onSuccess) {
                onSuccess()
            }
        },
        showOpenSettingsModal,
        openSettingsSilently: openSettings,
        hasLimitedPermission: status === RESULTS.LIMITED,
    }

}
