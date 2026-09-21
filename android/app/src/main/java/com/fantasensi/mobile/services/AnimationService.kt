package com.fantasensi.mobile.services

import android.content.Context
import android.provider.Settings

/**
 * Ajusta as escalas de animação do sistema (recurso legítimo, usado por
 * game boosters para reduzir a latência percebida). Exige a permissão
 * especial WRITE_SETTINGS, concedida pelo usuário nas configurações.
 */
class AnimationService(private val context: Context) {

    private val resolver = context.contentResolver

    fun canWrite(): Boolean = Settings.System.canWrite(context)

    data class AnimationState(
        val window: Float,
        val transition: Float,
        val animator: Float
    )

    fun current(): AnimationState = AnimationState(
        window = read(Settings.Global.WINDOW_ANIMATION_SCALE),
        transition = read(Settings.Global.TRANSITION_ANIMATION_SCALE),
        animator = read(Settings.Global.ANIMATOR_DURATION_SCALE)
    )

    fun applyGamingProfile(): AnimationState = applyProfile(0.5f, 0.5f, 0.5f)

    fun applyProfile(window: Float, transition: Float, animator: Float): AnimationState {
        val current = current()
        write(Settings.Global.WINDOW_ANIMATION_SCALE, window)
        write(Settings.Global.TRANSITION_ANIMATION_SCALE, transition)
        write(Settings.Global.ANIMATOR_DURATION_SCALE, animator)
        return current
    }

    fun restore(state: AnimationState) {
        write(Settings.Global.WINDOW_ANIMATION_SCALE, state.window)
        write(Settings.Global.TRANSITION_ANIMATION_SCALE, state.transition)
        write(Settings.Global.ANIMATOR_DURATION_SCALE, state.animator)
    }

    private fun read(key: String): Float =
        Settings.Global.getFloat(resolver, key, 1.0f)

    private fun write(key: String, value: Float) {
        try {
            Settings.Global.putFloat(resolver, key, value)
        } catch (_: Exception) {
            // A permissão WRITE_SETTINGS pode ter sido revogada entre a checagem e a escrita.
        }
    }
}
