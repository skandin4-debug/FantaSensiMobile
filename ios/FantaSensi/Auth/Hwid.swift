import Foundation
import UIKit
import CryptoKit

enum Hwid {
    static func get() -> String {
        let id = UIDevice.current.identifierForVendor?.uuidString ?? "unknown"
        let digest = SHA256.hash(data: Data(id.utf8))
        return digest.map { String(format: "%02x", $0) }.joined()
    }
}
