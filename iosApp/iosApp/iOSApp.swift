import SwiftUI
import BookMemo

@main
struct iOSApp: App {
    init() {
        IosKoinInitKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}