import SwiftUI
import UIKit

// VISUAL APENAS: esta tela reproduz o dashboard do PCOptimizer.
// Nenhuma mudança real é feita no sistema.
struct DashboardView: View {
    @State private var cpu = 34
    @State private var ram = 61
    @State private var ramUsed = "8.0 GB"
    @State private var ramTotal = "16.0 GB"
    @State private var disk = 47
    @State private var diskFree = "12.4 GB"

    @State private var optimizeBusy = false
    @State private var optimizeProgress: Double = 0
    @State private var optimizeStatus = "Pronto para otimizar."
    @State private var optimizePhase = 0
    @State private var optimizeTimer: Timer? = nil

    @State private var miraBusy = false
    @State private var miraStatus = "Pronto Para Melhorar Usando FantaSensi."
    @State private var activeMira: String? = nil
    @State private var miraTimer: Timer? = nil

    private let optPhrases = [
        "Aplicando otimização máxima",
        "Melhorando sensibilidade",
        "Limpando processos pesados",
        "Otimizando disco e registro",
        "Finalizando ajustes..."
    ]

    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                hero
                statsRow
                miraCard
                optimizeCard
            }
            .frame(maxWidth: 600)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 20)
            .padding(.vertical, 24)
        }
        .background(Color.fsBackground)
        .onReceive(timer) { _ in
            guard !optimizeBusy else { return }
            cpu = Int.random(in: (cpu - 4)...(cpu + 4)).clamped(to: 5...95)
            ram = Int.random(in: (ram - 3)...(ram + 3)).clamped(to: 20...95)
            disk = Int.random(in: (disk - 1)...(disk + 1)).clamped(to: 10...95)
        }
        .onDisappear {
            optimizeTimer?.invalidate()
            optimizeTimer = nil
            miraTimer?.invalidate()
            miraTimer = nil
        }
    }

    private var logoImage: Image {
        if let img = UIImage(named: "favicon") {
            return Image(uiImage: img)
        }
        return Image(systemName: "gamecontroller.fill")
    }

    private var hero: some View {
        VStack(spacing: 0) {
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

            GradientText(text: "FantaSensi", fontSize: 24)
                .padding(.top, 10)

            Text("Otimização completa e ajustes de mira com um clique.")
                .font(.footnote)
                .foregroundColor(.fsTextSecondary)
                .padding(.top, 5)
        }
    }

    private var statsRow: some View {
        HStack(spacing: 8) {
            StatCard(icon: "cpu", label: "CPU", value: "\(cpu)%",
                     detail: "uso do processador", progress: Double(cpu) / 100)
            StatCard(icon: "memorychip", label: "Memória (RAM)", value: "\(ram)%",
                     detail: "\(ramUsed) de \(ramTotal) em uso", progress: Double(ram) / 100)
            StatCard(icon: "internaldrive", label: "Disco", value: "\(disk)%",
                     detail: "\(diskFree) livres", progress: Double(disk) / 100)
        }
    }

    private var miraCard: some View {
        GlassCard {
            VStack(alignment: .leading, spacing: 10) {
                HStack(spacing: 6) {
                    Text("\u{25C6}").foregroundColor(.fsOrange)
                    Text("Configurações de Mira")
                        .font(.headline.weight(.semibold))
                        .foregroundColor(.fsTextPrimary)
                }
                Text("Aplica scripts de sensibilidade, latência e sistema.")
                    .font(.footnote)
                    .foregroundColor(.fsTextSecondary)

                HStack(spacing: 8) {
                    MiraButton(title: "MIRA LEVE", enabled: !miraBusy, loading: miraBusy && activeMira == "MIRA LEVE") { runMira("MIRA LEVE") }
                    MiraButton(title: "MIRA ALTA", enabled: !miraBusy, loading: miraBusy && activeMira == "MIRA ALTA") { runMira("MIRA ALTA") }
                }
                HStack(spacing: 8) {
                    MiraButton(title: "MIRA PESADA", enabled: !miraBusy, loading: miraBusy && activeMira == "MIRA PESADA") { runMira("MIRA PESADA") }
                    PowerButton(title: "FULL CAPA", enabled: !miraBusy, loading: miraBusy && activeMira == "FULL CAPA") { runMira("FULL CAPA") }
                }

                Text(miraStatus)
                    .font(.footnote)
                    .foregroundColor(.fsTextSecondary)

                Rectangle()
                    .fill(Color.white.opacity(0.141))
                    .frame(height: 1)

                socialFooter
            }
        }
    }

    private var optimizeCard: some View {
        GlassCard {
            VStack(alignment: .leading, spacing: 10) {
                HStack(spacing: 6) {
                    Image(systemName: "bolt.fill")
                        .font(.system(size: 15))
                        .foregroundColor(.fsOrange)
                    Text("Otimização única")
                        .font(.headline.weight(.semibold))
                        .foregroundColor(.fsTextPrimary)
                }
                Text("Limpa temporários, RAM, registro, disco e esvazia a lixeira.")
                    .font(.footnote)
                    .foregroundColor(.fsTextSecondary)

                PrimaryButton(title: optimizeBusy ? "OTIMIZANDO..." : "OTIMIZAR TUDO", enabled: !optimizeBusy, loading: optimizeBusy) {
                    runOptimization()
                }

                ProgressBar(progress: optimizeProgress)

                Text(optimizeStatus)
                    .font(.footnote)
                    .foregroundColor(.fsTextSecondary)
            }
        }
    }

    private var socialFooter: some View {
        VStack(spacing: 6) {
            HStack(spacing: 12) {
                socialChip("discord", "Discord", "https://discord.gg/fantasensi")
                socialChip("tiktok", "TikTok", "https://www.tiktok.com/@fanta.sens")
            }
            Text("v1.0 · FantaSensi")
                .font(.caption)
                .foregroundColor(.fsTextSecondary)
        }
        .frame(maxWidth: .infinity)
    }

    private func socialChip(_ imageName: String, _ label: String, _ url: String) -> some View {
        Button(action: { openURL(url) }) {
            HStack(spacing: 8) {
                if let img = UIImage(named: imageName) {
                    Image(uiImage: img)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                } else {
                    Image(systemName: "link")
                        .font(.system(size: 14))
                        .foregroundColor(.fsTextPrimary)
                }
                Text(label)
                    .font(.headline.weight(.semibold))
                    .foregroundColor(.fsTextPrimary)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 9)
            .background(Color.white.opacity(0.078))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(Color.white.opacity(0.2), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }

    private func openURL(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }
        UIApplication.shared.open(url)
    }

    private func runMira(_ label: String) {
        miraBusy = true
        activeMira = label
        let upper = label.uppercased()
        let phases = [
            "Aplicando \(upper)...",
            "Melhorando sensibilidade...",
            "Reduzindo latência de entrada...",
            "Calibrando mira..."
        ]
        let finalMsg = (label == "FULL CAPA") ? "FULL CAPA APLICADO" : "\(upper) APLICADA"

        var index = 0
        miraStatus = phases[0]
        miraTimer?.invalidate()
        miraTimer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
            index += 1
            if index < phases.count {
                miraStatus = phases[index]
            } else {
                timer.invalidate()
                miraTimer = nil
                activeMira = nil
                miraBusy = false
                miraStatus = finalMsg
            }
        }
    }

    private func runOptimization() {
        optimizeTimer?.invalidate()
        optimizeBusy = true
        optimizeProgress = 0
        optimizePhase = 0
        optimizeStatus = optPhrases[0]

        optimizeTimer = Timer.scheduledTimer(withTimeInterval: 0.7, repeats: true) { timer in
            optimizePhase += 1
            if optimizePhase < optPhrases.count {
                optimizeStatus = optPhrases[optimizePhase]
                optimizeProgress = Double(optimizePhase) / Double(optPhrases.count)
            } else {
                timer.invalidate()
                optimizeTimer = nil
                optimizeProgress = 1
                optimizeStatus = "Otimização concluída!"
                optimizeBusy = false
            }
        }
    }
}

extension Int {
    func clamped(to range: ClosedRange<Int>) -> Int {
        Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}
