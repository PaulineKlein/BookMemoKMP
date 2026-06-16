import SwiftUI
import BookMemo
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        IosKoinInitKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
