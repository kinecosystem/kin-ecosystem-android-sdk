package com.kin.ecosystem.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObservableData<T> {

    private AtomicReference<T> value;
    private List<Observer<T>> observers = new ArrayList<>(1);
    private final Handler mainThreadHandler;

    private final ReadWriteLock lock;
    private final Lock writeLock;

    ObservableData() {
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.value = new AtomicReference<>();
    }

    ObservableData(@NonNull final T defaultValue) {
        this();
        this.value.lazySet(defaultValue);
    }

    public static <T> ObservableData<T> create() {
        return new ObservableData<>();
    }

    public static <T> ObservableData<T> create(@NonNull final T defaultValue) {
        return new ObservableData<>(defaultValue);
    }

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public T getValue() {
        return value.get();
    }

    /**
     * Set value on the same thread
     * @param value
     */
    public void setValue(T value) {
        this.writeLock.lock();
        this.value.lazySet(value);
        this.onChanged();
        this.writeLock.unlock();
    }

    /**
     * Set value on the main thread
     * @param value
     */
    public void postValue(final T value) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    setValue(value);
                }
            });
        } else {
            setValue(value);
        }
    }

    private void onChanged() {
        for (Observer observer : observers) {
            observer.onChanged(value.get());
        }
    }
}