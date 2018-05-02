package com.kin.ecosystem.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObservableData<T> {

    static final Observer[] EMPTY = new Observer[0];

    private AtomicReference<T> value;
    private final AtomicReference<Observer<T>[]> observers;
    private final Handler mainThreadHandler;

    private final ReadWriteLock lock;
    private final Lock writeLock;

    ObservableData() {
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.value = new AtomicReference<>();
        this.observers = new AtomicReference<Observer<T>[]>(EMPTY);
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

    public boolean addObserver(Observer<T> observer) {
        Observer<T>[] oldList = observers.get();

        int len = oldList.length;
        @SuppressWarnings("unchecked")
        Observer<T>[] newList = new Observer[len + 1];
        System.arraycopy(oldList, 0, newList, 0, len);
        newList[len] = observer;
        return observers.compareAndSet(oldList, newList);

    }

    public void removeObserver(Observer<T> observer) {
        Observer<T>[] oldList = observers.get();
        if (oldList == EMPTY) {
            return;
        }
        int len = oldList.length;
        int observerIndex = -1;
        for (int i = 0; i < len; i++) {
            if (oldList[i] == observer) {
                observerIndex = i;
                break;
            }
        }

        if (observerIndex < 0) {
            return;
        }
        Observer<T>[] newList;
        if (len == 1) {
            newList = EMPTY;
        } else {
            newList = new Observer[len - 1];
            System.arraycopy(oldList, 0, newList, 0, observerIndex);
            System.arraycopy(oldList, observerIndex + 1, newList, observerIndex, len - observerIndex - 1);
        }
        observers.compareAndSet(oldList, newList);
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
        for (Observer observer : observers.get()) {
            observer.onChanged(value.get());
        }
    }
}