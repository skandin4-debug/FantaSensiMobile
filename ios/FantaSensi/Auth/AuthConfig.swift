import Foundation

enum AuthConfig {
    private static let nameEnc = "bUD0hgToHEL27IpUAhIC6SYdjqdADSTY5xcvs9W0Hc4="
    private static let ownerEnc = "oquS3Y7Lv4qX29+kPK3xGHKwJZ3qmtTYvCH2maFk/V8="
    private static let versionEnc = "Wc3s0/HaVH/qDozGkA2XW49s4JELsw9+/wM51UjYJyI="
    private static let urlEnc = "CNf4jBG7dxFIyaGhHOi4Ritk10tt2xziNxU2QCoyiLdyJdvo2MHCWXrAv1JBHUeH"

    static var name: String { Obf.d(nameEnc) }
    static var ownerId: String { Obf.d(ownerEnc) }
    static var version: String { Obf.d(versionEnc) }
    static var url: String { Obf.d(urlEnc) }
}
