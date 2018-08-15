package ubell.and.com.cardstalker.receiver;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.R;

public class PushMessage {

    //싱글턴 패턴. 객체를 단 하나만 만들어서 그 객체만 돌려쓴다.
    private static PushMessage instance;
    private NotificationManager manager;
    private NotificationCompat.Builder builder = null;

    //생성자를 private로 막아서 객체 생성을 막는다.
    private PushMessage() {
    }

    public static PushMessage getInstance(){
        if(instance==null)
            instance = new PushMessage();
        return instance;
    }

    public void goPushMessage(Context context, String message, String phoneNum) {

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //API 26 Level부터 NotificationChannel이라는게 생겼다. 그래서 분기를 나눠줘야함.

        //Oreo버전 이상이면 채널이라는 걸 만들어줘야함.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "one-channel";
            String channelName = "My Channel One";
            String channelDescription = "My Channel One Description";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, channelId);
        } else {

            //NotificationCompat.Builder(context)는 deprecated*사용중지* 되었다고함 API 26 level 이상에서..
            //그래서 인자값이 2개 들어가는 Builder 메소드를 이용했어야 함..!@!

            builder = new NotificationCompat.Builder(context, "channelIDX");
        }

        /*푸시 메세지를 눌렀을 때 인텐트를 통해 해당 액티비티로 이동*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,
                new Intent(context.getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        //NotificationCompat.Builder 를 통해 pushMessage, 즉 notification을 생성하고 그에대한 셋팅을 해줌.
        builder.setSmallIcon(R.drawable.foot)
                .setContentTitle(phoneNum)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());


    }
}

