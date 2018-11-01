package com.kin.ecosystem.recovery.backup.view;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import com.kin.ecosystem.recovery.Validator;
import java.util.Timer;
import java.util.TimerTask;

public class TextWatcherAdapter implements TextWatcher {

	private final Handler mainThreadHandler;
	private final TextChangeListener listener;

	private Timer timer = new Timer();
	private final static long DELAY = 500; // milliseconds

	public TextWatcherAdapter(final TextChangeListener textChangeListener) {
		Validator.checkNotNull(textChangeListener, "listener");
		this.mainThreadHandler = new Handler(Looper.getMainLooper());
		this.listener = textChangeListener;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(final Editable editable) {
		timer.cancel();
		timer = new Timer();
		timer.schedule(
			new TimerTask() {
				@Override
				public void run() {
					mainThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							listener.afterTextChanged(editable);
						}
					});
				}
			},
			DELAY
		);
	}

	public interface TextChangeListener {

		void afterTextChanged(Editable editable);
	}
}
