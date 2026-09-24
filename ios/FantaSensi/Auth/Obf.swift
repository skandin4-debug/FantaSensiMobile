import Foundation
import CommonCrypto

enum Obf {
    private static let key: [UInt8] = [
        130, 196, 66, 117, 81, 185, 215, 22, 157, 228, 77, 101, 85, 95, 254, 255,
        78, 251, 132, 1, 17, 15, 41, 112, 233, 80, 2, 14, 192, 81, 163, 24
    ]

    static func d(_ encoded: String) -> String {
        guard let raw = Data(base64Encoded: encoded), raw.count > 16 else { return "" }
        let iv = raw.subdata(in: 0..<16)
        let cipher = raw.subdata(in: 16..<raw.count)

        var outLength = 0
        var out = Data(count: cipher.count + kCCBlockSizeAES128)
        let outCount = out.count
        let cipherCount = cipher.count

        let status: CCCryptorStatus = out.withUnsafeMutableBytes { outPtr in
            cipher.withUnsafeBytes { cipherPtr in
                iv.withUnsafeBytes { ivPtr in
                    key.withUnsafeBytes { keyPtr in
                        CCCrypt(
                            CCOperation(kCCDecrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyPtr.baseAddress, kCCKeySizeAES256,
                            ivPtr.baseAddress,
                            cipherPtr.baseAddress, cipherCount,
                            outPtr.baseAddress, outCount,
                            &outLength
                        )
                    }
                }
            }
        }

        guard status == kCCSuccess else { return "" }
        out.removeSubrange(outLength..<out.count)
        return String(data: out, encoding: .utf8) ?? ""
    }
}
