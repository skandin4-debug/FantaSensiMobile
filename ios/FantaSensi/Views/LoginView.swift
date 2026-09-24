import SwiftUI
import UIKit

struct LoginView: View {
    var onAuthorized: (String, String) -> Void

    @State private var key = ""
    @State private var busy = false
    @State private var status = ""
    @State private var error = false

    var body: some View {
        ZStack {
            Color.fsBackground.ignoresSafeArea()
            ScrollView {
                VStack(spacing: 0) {
                    logo

                    GradientText(text: "FantaSensi", fontSize: 30)
                        .padding(.top, 14)

                    Text("Entre com a chave de ativação para liberar o aplicativo")
                        .font(.footnote)
                        .foregroundColor(.fsTextSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.top, 7)

                    Text("CHAVE DE ATIVAÇÃO")
                        .font(.caption.weight(.semibold))
                        .foregroundColor(Color.white.opacity(0.78))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.top, 26)
                        .padding(.leading, 2)

                    keyField
                        .padding(.top, 7)

                    PrimaryButton(
                        title: busy ? "VALIDANDO..." : "ACESSAR",
                        enabled: !busy,
                        loading: busy
                    ) {
                        doLogin()
                    }
                    .padding(.top, 14)

                    Text(status)
                        .font(.footnote)
                        .foregroundColor(error ? Color(red: 0.973, green: 0.443, blue: 0.443) : .fsTextSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.top, 14)

                    Text("v1.0 · FantaSensi")
                        .font(.caption)
                        .foregroundColor(.fsTextSecondary.opacity(0.6))
                        .padding(.top, 10)
                }
                .frame(maxWidth: 600)
                .frame(maxWidth: .infinity)
                .padding(.horizontal, 24)
                .padding(.vertical, 24)
            }
        }
    }

    private var logo: some View {
        logoImage
            .resizable()
            .scaledToFit()
            .padding(7)
            .frame(width: 96, height: 96)
            .background(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.white.opacity(0.078))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .stroke(Color.white.opacity(0.2), lineWidth: 1)
            )
    }

    private var logoImage: Image {
        if let img = UIImage(named: "favicon") {
            return Image(uiImage: img)
        }
        return Image(systemName: "gamecontroller.fill")
    }

    private var keyField: some View {
        HStack(spacing: 8) {
            Image(systemName: "key.fill")
                .font(.system(size: 18))
                .foregroundColor(.fsPrimary)
            SecureField("Cole aqui sua chave de ativação", text: $key)
                .foregroundColor(.white)
                .accentColor(.fsOrange)
                .disabled(busy)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .onSubmit { doLogin() }
        }
        .padding(.horizontal, 14)
        .frame(height: 58)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color.black.opacity(0.08))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color.white.opacity(0.22), lineWidth: 1)
        )
    }

    private func doLogin() {
        let k = key.trimmingCharacters(in: .whitespacesAndNewlines)
        if k.isEmpty {
            status = "Coloque sua chave de ativação para continuar."
            error = true
            return
        }
        busy = true
        status = "Validando chave... aguarde."
        error = false
        Task {
            let initResult = await KeyAuthClient.initSession()
            if initResult.outcome != .ok {
                status = initResult.message
                error = true
                busy = false
                return
            }
            let lic = await KeyAuthClient.license(k, hwid: Hwid.get(), sessionId: initResult.sessionId)
            if lic.outcome == .ok {
                AuthSession.save(license: k, sessionId: initResult.sessionId)
                onAuthorized(k, initResult.sessionId)
            } else {
                status = lic.message
                error = true
                busy = false
            }
        }
    }
}
