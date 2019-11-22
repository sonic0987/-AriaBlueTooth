package com.norma.abc.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringDef;

import com.norma.abc.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NotificationManager {

    private static final String GROUP = "Dev.oni_Notification_GROUP";

    public static void createChannel(Context context) {

        //Oreo SDKqnxjsms notification 채널 그룹이란게 필요. 안그러면 notification 작동 안함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup group = new NotificationChannelGroup(GROUP, GROUP);
            getManager(context).createNotificationChannelGroup(group);

            NotificationChannel channelMessage = new NotificationChannel(Channel.MESSAGE,
                    context.getString(R.string.notification_channel_message_title), android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription(context.getString(R.string.notification_channel_message_description));
            channelMessage.setGroup(GROUP);
            channelMessage.setLightColor(Color.GREEN);
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager(context).createNotificationChannel(channelMessage);

            NotificationChannel channelComment = new NotificationChannel(Channel.COMMENT,
                    context.getString(R.string.notification_channel_comment_title), android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelComment.setDescription(context.getString(R.string.notification_channel_comment_description));
            channelComment.setGroup(GROUP);
            channelComment.setLightColor(Color.BLUE);
            channelComment.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager(context).createNotificationChannel(channelComment);

            NotificationChannel channelNotice = new NotificationChannel(Channel.NOTICE,
                    context.getString(R.string.notification_channel_notice_title), android.app.NotificationManager.IMPORTANCE_HIGH);
            channelNotice.setDescription(context.getString(R.string.notification_channel_notice_description));
            channelNotice.setGroup(GROUP);
            channelNotice.setLightColor(Color.RED);
            channelNotice.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager(context).createNotificationChannel(channelNotice);

            NotificationChannel serviceUpNotice = new NotificationChannel(Channel.SERVE_UP,
                    context.getString(R.string.notification_channel_notice_title),
                    android.app.NotificationManager.IMPORTANCE_MIN);
            serviceUpNotice.setDescription(context.getString(R.string.notification_channel_notice_description));
            serviceUpNotice.setGroup(GROUP);
            serviceUpNotice.setLightColor(Color.WHITE);
            serviceUpNotice.setLockscreenVisibility(Notification.BADGE_ICON_NONE);
            getManager(context).createNotificationChannel(serviceUpNotice);
        }




    }

    private static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //특정 채널 삭제(앞으로 해당 채널에 관련된 notification은 알람이 안옴.)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void deleteChannel(Context context, @Channel String channel) {
        getManager(context).deleteNotificationChannel(channel);
    }

    //일반적인 Notification 실행
    public static void sendNotification(Context context, int id, @Channel String channel, String title, String body) {
        Notification.Builder builder;

        builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channel);
        }

        getManager(context).notify(id, builder.build());
    }

    //일반적인 Notification 실행
    // + PendingIntent 추가로 Noti 클릭시, Activity가 실행되거나 팝업이 실행되는 응용에 쓰임.
    public static void sendNotification(Context context, PendingIntent pendingIntent, int id, @Channel String channel, String title, String body) {
        Notification.Builder builder;

        builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channel);
        }

        getManager(context).notify(id, builder.build());
    }

    //일반적인 Notification 실행
    // + Expandable 형식
    public static void sendNotification_expandable(Context context, int id, @Channel String channel, String title, String body) {
        Notification.Builder builder;

        builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channel);
        }

        getManager(context).notify(id, builder.build());
    }

    //Oreo SDK부터는 startForeground를 호출해야 백엔드 프로세서가 실행되게 돼있음.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification_Server(Service context, int id, @Channel String channel, String title, String body) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(getSmallIcon())
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true);
        }

        context.startForeground(id, builder.build());
        getManager(context).cancel(id);
    }

    //Oreo SDK부터는 startForeground를 호출해야 백엔드 프로세서가 실행되게 돼있음.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification_Server(Service context, PendingIntent pendingIntent, int id, @Channel String channel, String title, String body) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(getSmallIcon())
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true);
        }

        context.startForeground(id, builder.build());
        getManager(context).cancel(id);
    }

    //notification에 보일 아이콘
    private static int getSmallIcon() {
        return R.mipmap.ic_launcher_round;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Channel.MESSAGE,
            Channel.COMMENT,
            Channel.NOTICE,
            Channel.SERVE_UP,
            Type.ACCOUNT,
            Type.COMMENT,
            Type.REGISTED,
            Type.REJECT
    })

    //각 notification 타입에 따른 channel 명칭
    public @interface Channel {
        String MESSAGE = "message";
        String COMMENT = "comment";
        String NOTICE = "notice";
        String SERVE_UP = "service";
    }


    //Type 명칭
    public @interface Type {
        String ACCOUNT = "acc";
        String REGISTED = "reged";
        String COMMENT = "cmt";
        String REJECT = "acc_rej";
    }

}
