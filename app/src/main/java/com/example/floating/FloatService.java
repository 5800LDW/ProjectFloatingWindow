package com.example.floating;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.example.projectfloatingwindow.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;


@TargetApi(23)
public class FloatService extends Service {

    StandardGSYVideoPlayer detail_player;
    WindowManager wm = null;

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }

    View view;

    private static final String TAG = FloatService.class.getSimpleName();

    @Override
    public void onCreate() {

        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.app_item_floating_view, null);

        createView();

        detail_player = view.findViewById(R.id.detail_player);

        ZoomView zoomView = view.findViewById(R.id.ZoomView);
        zoomView.setTranslationListener(new ZoomView.TranslationListener() {
            @Override
            public void translation(float actionX, float actionY) {
                wmParams.x = (int) actionX;
                wmParams.y = (int) actionY;
                wm.updateViewLayout(view, wmParams);
            }

            @Override
            public void scale(float scale) {

                wmParams.width = (int)(zoomView.getOriginalWidth() * scale);
                wmParams.height = (int)(zoomView.getOriginalHeight() * scale);

                wm.updateViewLayout(view, wmParams);

            }
        });

        setPlayer();

    }

    private static float dp2px(Float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void createView() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);//

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        wmParams.format = 1;

        wm.addView(view, wmParams);


    }


    @Override
    public void onDestroy() {
        wm.removeView(view);
        GSYVideoManager.releaseAllVideos();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//    private void createNotificationForNormal() {
//        Log.e("TAG" , ">>>>>>>>>>>>>>>> createNotificationForNormal ");
//        // 适配8.0及以上 创建渠道
//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = null;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        } else {
//            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        }
//
//        String channelId = createNotificationChannel("my_channel_ID", "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
//                .setContentTitle("通知")
//                .setContentText("floating window service")
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true)
//                .setWhen(System.currentTimeMillis());
//
//        notificationManager.notify(16657, notification.build());
//    }






    private String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

    private void setPlayer() {
        detail_player.setUpLazy("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", true, null, null, "这是title");
        //增加title
        detail_player.getTitleTextView().setVisibility(View.GONE);
        //设置返回键
        detail_player.getBackButton().setVisibility(View.GONE);


        //设置全屏按键功能
        detail_player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                detail_player.startWindowFullscreen(FloatService.this, false, true);
            }
        });
        //防止错位设置
        detail_player.setPlayTag(TAG);
//        detail_player.setPlayPosition(position);
        //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
        detail_player.setAutoFullWithSize(true);
        //音频焦点冲突时是否释放
        detail_player.setReleaseWhenLossAudio(false);
        //全屏动画
        detail_player.setShowFullAnimation(true);
        //小屏时不触摸滑动
        detail_player.setIsTouchWiget(false);

    }
}
