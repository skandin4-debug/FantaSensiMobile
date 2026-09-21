import SwiftUI

// Paleta FantaSensi (copiada do PCOptimizer — Themes/Glassmorphism.xaml)
extension Color {
    static let fsBackground = Color(red: 0.031, green: 0.031, blue: 0.043)
    static let fsSurface = Color(red: 0.071, green: 0.071, blue: 0.094)
    static let fsSurfaceLight = Color(red: 0.090, green: 0.090, blue: 0.118)
    static let fsPrimary = Color(red: 0.788, green: 0.353, blue: 0.035)
    static let fsPrimaryLight = Color(red: 0.886, green: 0.455, blue: 0.122)
    static let fsPrimaryDark = Color(red: 0.659, green: 0.290, blue: 0.024)
    static let fsOrange = Color(red: 1.0, green: 0.541, blue: 0.118)
    static let fsOrangeLight = Color(red: 1.0, green: 0.757, blue: 0.478)
    static let fsOrangeDeep = Color(red: 0.851, green: 0.400, blue: 0.051)
    static let fsTextPrimary = Color(red: 0.925, green: 0.925, blue: 0.933)
    static let fsTextSecondary = Color(red: 0.651, green: 0.678, blue: 0.714)
    static let fsSuccess = Color(red: 0.290, green: 0.871, blue: 0.502)
}

struct GlassCard<Content: View>: View {
    let content: Content
    var padding: CGFloat = 18
    var cornerRadius: CGFloat = 16

    init(padding: CGFloat = 18, cornerRadius: CGFloat = 16, @ViewBuilder content: () -> Content) {
        self.padding = padding
        self.cornerRadius = cornerRadius
        self.content = content()
    }

    var body: some View {
        content
            .padding(padding)
            .background(
                RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                    .fill(Color.white.opacity(0.055))
            )
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                    .stroke(Color.white.opacity(0.141), lineWidth: 1)
            )
    }
}

struct GradientText: View {
    let text: String
    var fontSize: CGFloat = 30

    var body: some View {
        TimelineView(.animation) { context in
            let phase = CGFloat(
                context.date.timeIntervalSinceReferenceDate
                    .truncatingRemainder(dividingBy: 3.0) / 3.0
            )
            Text(text)
                .font(.system(size: fontSize, weight: .bold))
                .foregroundStyle(
                    LinearGradient(
                        colors: [
                            .fsOrangeLight, .fsOrange, .fsOrangeDeep, .fsOrange, .fsOrangeLight,
                            .fsOrange, .fsOrangeDeep, .fsOrange, .fsOrangeLight
                        ],
                        startPoint: UnitPoint(x: phase - 1, y: 0.5),
                        endPoint: UnitPoint(x: phase + 1, y: 0.5)
                    )
                )
                .shadow(color: .fsOrange.opacity(0.7), radius: 9)
        }
    }
}

struct ProgressBar: View {
    let progress: Double
    var height: CGFloat = 9

    var body: some View {
        GeometryReader { geo in
            ZStack(alignment: .leading) {
                Capsule()
                    .fill(Color.white.opacity(0.13))
                Capsule()
                    .fill(
                        LinearGradient(
                            colors: [
                                Color(red: 1.0, green: 0.620, blue: 0.259),
                                .fsPrimaryLight,
                                .fsPrimary
                            ],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .frame(width: geo.size.width * min(max(progress, 0), 1))
            }
        }
        .frame(height: height)
    }
}

struct StatCard: View {
    let icon: String
    let label: String
    let value: String
    let detail: String
    let progress: Double

    var body: some View {
        GlassCard(padding: 10) {
            VStack(alignment: .leading, spacing: 5) {
                HStack(spacing: 4) {
                    Image(systemName: icon)
                        .font(.system(size: 12))
                        .foregroundColor(.fsPrimary)
                    Text(label)
                        .font(.caption2.weight(.semibold))
                        .foregroundColor(.fsTextSecondary)
                }
                Text(value)
                    .font(.title2.weight(.bold))
                    .foregroundColor(.fsOrangeLight)
                ProgressBar(progress: progress, height: 6)
                Text(detail)
                    .font(.system(size: 9))
                    .foregroundColor(.fsTextSecondary)
                    .lineLimit(1)
            }
        }
    }
}

struct PrimaryButton: View {
    let title: String
    var enabled: Bool = true
    var loading: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if loading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(0.8)
                }
                Text(title)
                    .font(.headline.weight(.semibold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 13)
            .background(Color.fsPrimary.opacity(enabled ? 1 : 0.45))
            .foregroundColor(.white.opacity(enabled ? 1 : 0.6))
            .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
        }
        .disabled(!enabled)
    }
}

struct MiraButton: View {
    let title: String
    var enabled: Bool = true
    var loading: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if loading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .fsTextPrimary))
                        .scaleEffect(0.8)
                }
                Text(title)
                    .font(.headline.weight(.bold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 13)
            .foregroundColor(.fsTextPrimary)
            .background(
                LinearGradient(
                    colors: [
                        Color(red: 1.0, green: 0.478, blue: 0.0).opacity(0.118),
                        Color.black.opacity(0.063)
                    ],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(Color.fsOrange.opacity(0.349), lineWidth: 1.5)
            )
        }
        .disabled(!enabled)
        .opacity(loading ? 0.85 : (enabled ? 1 : 0.55))
    }
}

struct PowerButton: View {
    let title: String
    var enabled: Bool = true
    var loading: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if loading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(0.8)
                }
                Text(title)
                    .font(.headline.weight(.bold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 13)
            .foregroundColor(.white)
            .background(
                LinearGradient(
                    colors: [.fsOrange, .fsOrangeDeep, Color(red: 0.604, green: 0.267, blue: 0.008)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(Color(red: 1.0, green: 0.643, blue: 0.227), lineWidth: 1.5)
            )
        }
        .disabled(!enabled)
        .opacity(loading ? 0.85 : (enabled ? 1 : 0.55))
    }
}
