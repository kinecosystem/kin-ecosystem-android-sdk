package com.kin.ecosystem.base;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

public class ObservableData<T> {

    private T value;
    private List<Observer<T>> observers = new ArrayList<>(1);
    private final Handler mainThreadHandler;

    public ObservableData() {
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public static <T> ObservableData<T> create() {
        ObservableData<T> observableData = new ObservableData<>();
        observableData.setValue(null);
        return observableData;
    }

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public T getValue() {
        return value;
    }

    /**
     * Set value on the same thread
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
        onChanged();
    }

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
            observer.onChanged(value);
        }
    }
}