import SwiftUI

enum AuthState {
    case loading
    case loggedOut
    case loggedIn
}

struct ContentView: View {
    @State private var state: AuthState = .loading

    var body: some View {
        ZStack {
            Color.fsBackground.ignoresSafeArea()
            switch state {
            case .loading:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .fsPrimary))
                    .scaleEffect(1.2)
            case .loggedOut:
                LoginView(onAuthorized: { _, _ in state = .loggedIn })
            case .loggedIn:
                DashboardView()
            }
        }
        .onAppear(perform: autoLogin)
    }

    private func autoLogin() {
        guard let savedKey = AuthSession.license else {
            state = .loggedOut
            return
        }
        Task {
            let initResult = await KeyAuthClient.initSession()
            if initResult.outcome == .ok {
                let lic = await KeyAuthClient.license(savedKey, hwid: Hwid.get(), sessionId: initResult.sessionId)
                if lic.outcome == .ok {
                    AuthSession.save(license: savedKey, sessionId: initResult.sessionId)
                    state = .loggedIn
                    return
                }
            }
            state = .loggedOut
        }
    }
}

#Preview {
    ContentView()
        .preferredColorScheme(.dark)
}
