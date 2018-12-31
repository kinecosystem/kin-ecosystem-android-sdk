package com.kin.ecosystem.transfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class AccountInfoManager {
    private static final String EXTRA_HAS_ERROR = "EXTRA_HAS_ERROR";
    private static String FILE_NAME = "accountInfo.txt";
    private static String FILE_PROVIDER_NAME = "KinTransferAccountInfoFileProvider";
    private static String FILE_PROVIDER_DIR_NAME = "kintransfer_account_info";

    private File file;

    //need to be called first
    public boolean init(@NonNull final File filesDir, @NonNull final String publicAddress) {
        if (publicAddress.isEmpty()) {
            return false;
        }
        String dirPath = filesDir.getAbsolutePath() + File.separator + FILE_PROVIDER_DIR_NAME;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                return false;
            }
        }
        String fileFullPath = dirPath + File.separator + FILE_NAME;
        try {
            file = new File(fileFullPath);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    file = null;
                    return false;
                }
            }
            FileWriter writer = new FileWriter(file);
            writer.append(publicAddress);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            file = null;
            return false;
        }
        return true;
    }

    //Can be called anytime (before init returns), when user decline transfer
    void respondCancel(@NonNull Activity activity) {
        respondCancel(activity, false);
    }

    void respondCancel(@NonNull Activity activity, boolean hasError) {
        Intent intent = new Intent();
        if (hasError) {
            intent.putExtra(EXTRA_HAS_ERROR, hasError);
        }
        activity.setResult(Activity.RESULT_CANCELED, intent);
    }

    //Can be called only after init returns true, hence file is not null
    void respondOk(@NonNull Activity activity) {
        if (file != null) {
            Intent intent = new Intent();
            String authority = activity.getPackageName() + "." + FILE_PROVIDER_NAME;
            final Uri uri = FileProvider.getUriForFile(activity, authority, file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                activity.revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, activity.getContentResolver().getType(uri));
            activity.setResult(Activity.RESULT_OK, intent);
        }
    }
}
