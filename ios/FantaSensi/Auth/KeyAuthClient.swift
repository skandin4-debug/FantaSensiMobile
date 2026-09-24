import Foundation

enum KaOutcome {
    case ok
    case invalid
    case unreachable
}

struct KaResult {
    let outcome: KaOutcome
    var message: String = ""
    var sessionId: String = ""
}

enum KeyAuthClient {
    private static let qType = Obf.d("qOOjrOj+UBMsMzPWJ+FLirVRSPuUn2DLK/A9OL+dGFU=")
    private static let qVer = Obf.d("fdaut7AYCBxtM5jgZNPaBaen2fSBmp9eB68rlHxava0=")
    private static let qName = Obf.d("z3pKWUIDgCROw7j4/a8ZYGP4UTMXIJsfuak0JxHnckU=")
    private static let qOwner = Obf.d("DdMnsKpE8Gfs8gfyEWh/aNXRk/k6lb8dxumu5xpQk0I=")
    private static let qKey = Obf.d("FyEsqFceit130AF4cPW8kYEQa5AyuvF4KxUHILKy96Q=")
    private static let qHwid = Obf.d("6NZ/SJK3m+RAfBSK1DBfwyg1YSNE0JtT5+HhXJ8NoKw=")
    private static let qSession = Obf.d("zg4XXb5fnbQJ+3ToRdZ87QC64/ScBy4Oy0OAiJ74A7I=")
    private static let tInit = Obf.d("zu7xjK53Rnf7VLwVEInTG8R8cMwmQmPd2Y9tQFBNHNM=")
    private static let tLicense = Obf.d("TaDXljeDk+VgumfxxkPX6CBX/j4/AHjCYvFpRiNG88Y=")
    private static let tCheck = Obf.d("+X7+zEEyXSTc2FIrFlhlS6E/WjeBXK7lcc7H5rdpNkc=")

    static func initSession() async -> KaResult {
        let urlString = AuthConfig.url + qType + tInit + qVer + AuthConfig.version +
            qName + enc(AuthConfig.name) + qOwner + enc(AuthConfig.ownerId)
        guard let body = await request(urlString) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        guard let json = parse(body) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        if json["success"] as? Bool == true {
            let sid = json["sessionid"] as? String ?? ""
            if sid.isEmpty {
                return KaResult(outcome: .invalid, message: "Sessão não retornada.")
            }
            return KaResult(outcome: .ok, message: "Sessão iniciada.", sessionId: sid)
        }
        return KaResult(outcome: .invalid, message: json["message"] as? String ?? "Falha na inicialização.")
    }

    static func license(_ key: String, hwid: String, sessionId: String) async -> KaResult {
        let urlString = AuthConfig.url + qType + tLicense + qKey + enc(key) +
            qHwid + enc(hwid) + qSession + enc(sessionId) +
            qName + enc(AuthConfig.name) + qOwner + enc(AuthConfig.ownerId)
        guard let body = await request(urlString) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        guard let json = parse(body) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        if json["success"] as? Bool == true {
            return KaResult(outcome: .ok, message: "Autenticado.")
        }
        return KaResult(outcome: .invalid, message: json["message"] as? String ?? "Chave inválida.")
    }

    static func check(_ sessionId: String) async -> KaResult {
        let urlString = AuthConfig.url + qType + tCheck + qSession + enc(sessionId) +
            qName + enc(AuthConfig.name) + qOwner + enc(AuthConfig.ownerId)
        guard let body = await request(urlString) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        guard let json = parse(body) else {
            return KaResult(outcome: .unreachable, message: "Falha de conexão.")
        }
        if json["success"] as? Bool == true {
            return KaResult(outcome: .ok, message: json["message"] as? String ?? "")
        }
        return KaResult(outcome: .invalid, message: json["message"] as? String ?? "Sessão inválida.")
    }

    private static func parse(_ body: String) -> [String: Any]? {
        guard let data = body.data(using: .utf8) else { return nil }
        return (try? JSONSerialization.jsonObject(with: data)) as? [String: Any]
    }

    private static func enc(_ s: String) -> String {
        return s.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? s
    }

    private static func request(_ urlString: String) async -> String? {
        guard let url = URL(string: urlString) else { return nil }
        var req = URLRequest(url: url)
        req.timeoutInterval = 12
        req.setValue("KeyAuth", forHTTPHeaderField: "User-Agent")
        do {
            let (data, _) = try await URLSession.shared.data(for: req)
            return String(data: data, encoding: .utf8)
        } catch {
            return nil
        }
    }
}
