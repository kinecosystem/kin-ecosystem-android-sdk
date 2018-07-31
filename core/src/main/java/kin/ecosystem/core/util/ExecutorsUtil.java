package kin.ecosystem.core.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorsUtil {

    private final Executor diskIO;

    private final Executor mainThread;

    ExecutorsUtil(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public ExecutorsUtil() {
        this(new DiskIOThreadExecutor(), new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public static class MainThreadExecutor implements Executor {

        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                handler.post(command);
            } else {
                command.run();
            }
        }

        public void removeCallbacksAndMessages(Object token) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(token);
            }
        }
    }

    private static class DiskIOThreadExecutor implements Executor {

        private final Executor diskIO;

        DiskIOThreadExecutor() {
            diskIO = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            diskIO.execute(command);
        }
    }
}
