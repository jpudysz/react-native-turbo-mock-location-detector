import * as React from 'react'
import { StyleSheet, View, Text } from 'react-native'
import { multiply } from 'react-native-mock-location-detector'

export const App = () => {
    const [result, setResult] = React.useState<number>()

    React.useEffect(() => {
        multiply(3, 7).then(setResult)
    }, [])

    return (
        <View style={styles.container}>
            <Text>
                Result: {result}
            </Text>
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
