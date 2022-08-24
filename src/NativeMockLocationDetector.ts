import type { TurboModule } from 'react-native'
import { TurboModuleRegistry } from 'react-native'

type MockLocationDetectorResult = {
    isLocationMocked: boolean
}

export interface Spec extends TurboModule {
    isMockingLocation(): Promise<MockLocationDetectorResult>
}

export default TurboModuleRegistry.getEnforcing<Spec>('MockLocationDetector')
