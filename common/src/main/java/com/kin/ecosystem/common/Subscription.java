package com.kin.ecosystem.common;

public class Subscription<T> {

	private ObservableData<T> observableData;
	private Observer<T> observer;

	public Subscription(ObservableData<T> observableData, Observer<T> observer) {
		this.observableData = observableData;
		this.observer = observer;
	}

	public void remove() {
		observableData.removeObserver(observer);
	}
}
