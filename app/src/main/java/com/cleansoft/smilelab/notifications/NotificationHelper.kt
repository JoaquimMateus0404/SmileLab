package com.cleansoft.smilelab.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cleansoft.smilelab.MainActivity
import com.cleansoft.smilelab.R

/**
 * Helper para criar e exibir notificações
 */
object NotificationHelper {

    private const val CHANNEL_ID = "brushing_reminders"
    private const val CHANNEL_NAME = "Lembretes de Escovação"
    private const val CHANNEL_DESCRIPTION = "Notificações para lembrar de escovar os dentes"

    /**
     * Cria o canal de notificação (necessário para Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Mostra uma notificação de lembrete de escovação
     */
    fun showBrushingReminder(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        enableSound: Boolean = true,
        enableVibration: Boolean = true
    ) {
        // Intent para abrir o app quando clicar na notificação
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Adicionar som se habilitado
        if (enableSound) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        // Adicionar vibração se habilitada
        if (enableVibration) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        }

        // Mostrar notificação
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Permissão de notificação não concedida
            e.printStackTrace()
        }
    }

    /**
     * Cancela uma notificação específica
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancela todas as notificações
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }
}



