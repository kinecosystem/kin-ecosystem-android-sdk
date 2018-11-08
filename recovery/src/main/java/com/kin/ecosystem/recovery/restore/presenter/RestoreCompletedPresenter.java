package com.kin.ecosystem.recovery.restore.presenter;


import android.os.Bundle;
import com.kin.ecosystem.recovery.restore.view.RestoreCompletedView;

public interface RestoreCompletedPresenter extends BaseChildPresenter<RestoreCompletedView> {

	void close();

	void onSaveInstanceState(Bundle outState);
}
