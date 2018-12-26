package com.kin.ecosystem.transfer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public abstract class AccountInfoService extends IntentService {
    public interface AccountInfoListener {
        void onAccountInfoReady(String data);

        void onAccountInfoFailed(String reason);
    }

    private static String FILE_PROVIDER_DIR_NAME = "kin_account_info";
    private static String FILE_NAME = "kinAccountInfo.txt";
    private static String ACTION_KIN_TRANSFER_INFO = "kin.transfer.info";
    private static String EXTRA_PACKAGE_KEY = "EXTRA_PACKAGE_KEY";
    private static String EXTRA_ACTION_KEY = "EXTRA_ACTION_KEY";
    private static String EXTRA_SERVICE_FULL_PATH_KEY = "EXTRA_SERVICE_FULL_PATH_KEY";
    private static String FILE_PROVIDER_NAME = "AccountInfoFileProvider";
    private static String CHANNEL_NAME = "kin transfer";
    private static String CHANNEL_ID = "0";
    private static String NOTIFICATION_TRANSFER = "transfer kin";


    private String fileFullPath, fileDirPath;
    private File file;

    public AccountInfoService(String name) {
        super(name);
    }

    public abstract void getAccountInfo(AccountInfoListener listener);

    @Override
    protected void onHandleIntent(final @Nullable Intent intent) {
        startForeground();

        if (intent != null && intent.getAction().equals(ACTION_KIN_TRANSFER_INFO) && intent.hasExtra(EXTRA_ACTION_KEY)
                && intent.hasExtra(EXTRA_PACKAGE_KEY) && intent.hasExtra(EXTRA_SERVICE_FULL_PATH_KEY)) {
            getAccountInfo(new AccountInfoListener() {
                @Override
                public void onAccountInfoReady(String data) {
                    if (createFile(data)) {
                        startServiceKinAccountReceiver(intent.getStringExtra(EXTRA_ACTION_KEY),
                                intent.getStringExtra(EXTRA_PACKAGE_KEY),
                                intent.getStringExtra(EXTRA_SERVICE_FULL_PATH_KEY));
                    }
                }

                @Override
                public void onAccountInfoFailed(String reason) {
                }
            });

        }
        stopForeground();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileDirPath = getFilesDir().getAbsolutePath() + File.separator + FILE_PROVIDER_DIR_NAME;
        fileFullPath = fileDirPath + File.separator + FILE_NAME;
    }

    private void stopForeground() {
        stopForeground(true);
        stopSelf();
    }

    private void startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, createNotificationChannel());
            builder.setContentTitle(getString(R.string.app_name)).setContentText(NOTIFICATION_TRANSFER).setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(getString(R.string.app_name)).setContentText(NOTIFICATION_TRANSFER).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }
    }

    private boolean createFile(String data) {
        File dir = new File(fileDirPath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                return false;
            }
        }
        try {
            file = new File(fileFullPath);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            FileWriter writer = new FileWriter(file);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AccountInfoService", "AccountInfoService cant create and write to file " + e.getMessage());
            return false;
        }
        return true;
    }

    private void startServiceKinAccountReceiver(String action, String pkg, String serviceFullPath) {
        Intent intent = new Intent(action);
        intent.setPackage(pkg);
        intent.setComponent(new ComponentName(pkg, serviceFullPath));
        String authority = getPackageName() + "." + FILE_PROVIDER_NAME;
        final Uri uri = FileProvider.getUriForFile(this, authority, file);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(EXTRA_PACKAGE_KEY, getApplication().getPackageName());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, getContentResolver().getType(uri));
        final List<ResolveInfo> services = getPackageManager().queryIntentServices(intent, 0);
        if (!services.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = CHANNEL_ID;
        String channelName = CHANNEL_NAME;
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setImportance(NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = getSystemService(NotificationManager.class);
        if (service != null) {
            service.createNotificationChannel(chan);
        }
        return channelId;
    }
}

