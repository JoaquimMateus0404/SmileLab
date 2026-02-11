package com.cleansoft.smilelab.notifications
}
    }
        notificationManager.cancelAll()
        val notificationManager = NotificationManagerCompat.from(context)
    fun cancelAllNotifications(context: Context) {
     */
     * Cancela todas as notificações
    /**

    }
        notificationManager.cancel(notificationId)
        val notificationManager = NotificationManagerCompat.from(context)
    fun cancelNotification(context: Context, notificationId: Int) {
     */
     * Cancela uma notificação específica
    /**

    }
        }
            e.printStackTrace()
            // Permissão de notificação não concedida
        } catch (e: SecurityException) {
            }
                notify(notificationId, builder.build())
            with(NotificationManagerCompat.from(context)) {
        try {
        // Mostrar notificação

        }
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        if (enableVibration) {
        // Adicionar vibração se habilitada

        }
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        if (enableSound) {
        // Adicionar som se habilitado

            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        // Construir notificação

        )
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            intent,
            notificationId,
            context,
        val pendingIntent = PendingIntent.getActivity(
        }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val intent = Intent(context, MainActivity::class.java).apply {
        // Intent para abrir o app quando clicar na notificação
    ) {
        enableVibration: Boolean = true
        enableSound: Boolean = true,
        message: String,
        title: String,
        notificationId: Int,
        context: Context,
    fun showBrushingReminder(
     */
     * Mostra uma notificação de lembrete de escovação
    /**

    }
        }
            notificationManager.createNotificationChannel(channel)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            }
                )
                    null
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                setSound(
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                enableVibration(true)
                description = CHANNEL_DESCRIPTION
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    fun createNotificationChannel(context: Context) {
     */
     * Cria o canal de notificação (necessário para Android 8.0+)
    /**

    private const val CHANNEL_DESCRIPTION = "Notificações para lembrar de escovar os dentes"
    private const val CHANNEL_NAME = "Lembretes de Escovação"
    private const val CHANNEL_ID = "brushing_reminders"

object NotificationHelper {
 */
 * Helper para criar e exibir notificações
/**

import com.cleansoft.smilelab.R
import com.cleansoft.smilelab.MainActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import android.os.Build
import android.media.RingtoneManager
import android.content.Intent
import android.content.Context
import android.app.PendingIntent
import android.app.NotificationManager
import android.app.NotificationChannel


