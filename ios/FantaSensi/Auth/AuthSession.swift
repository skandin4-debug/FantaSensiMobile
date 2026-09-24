import Foundation

enum AuthSession {
    private static let licenseKey = "auth_license"
    private static let sessionKey = "auth_sessionid"

    static func save(license: String, sessionId: String) {
        UserDefaults.standard.set(license, forKey: licenseKey)
        UserDefaults.standard.set(sessionId, forKey: sessionKey)
    }

    static func clear() {
        UserDefaults.standard.removeObject(forKey: licenseKey)
        UserDefaults.standard.removeObject(forKey: sessionKey)
    }

    static var license: String? {
        UserDefaults.standard.string(forKey: licenseKey)
    }

    static var sessionId: String? {
        UserDefaults.standard.string(forKey: sessionKey)
    }
}
