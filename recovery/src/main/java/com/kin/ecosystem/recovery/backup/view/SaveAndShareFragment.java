package com.kin.ecosystem.recovery.backup.view;

import static com.kin.ecosystem.recovery.backup.presenter.BackupPresenterImpl.KEY_ACCOUNT_KEY;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.SaveAndSharePresenter;
import com.kin.ecosystem.recovery.backup.presenter.SaveAndSharePresenterImpl;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGeneratorImpl;
import com.kin.ecosystem.recovery.qr.QRFileUriHandlerImpl;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SaveAndShareFragment extends Fragment implements SaveAndShareView {

	public static SaveAndShareFragment newInstance(BackupNavigator listener, String key) {
		SaveAndShareFragment fragment = new SaveAndShareFragment();
		fragment.setNextStepListener(listener);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_ACCOUNT_KEY, key);
		fragment.setArguments(bundle);
		return fragment;
	}

	private BackupNavigator nextStepListener;
	private SaveAndSharePresenter saveAndSharePresenter;

	private CheckBox iHaveSavedCheckbox;
	private ImageView qrImageView;
	private Button actionButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_save_and_share_qr, container, false);
		initViews(root);
		String key = getArguments().getString(KEY_ACCOUNT_KEY, null);
		final QRBarcodeGenerator qrBarcodeGenerator = new QRBarcodeGeneratorImpl(
			new QRFileUriHandlerImpl(getContext()));
		saveAndSharePresenter = new SaveAndSharePresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), nextStepListener,
			qrBarcodeGenerator, key, savedInstanceState);
		saveAndSharePresenter.onAttach(this);
		return root;
	}

	private void initViews(View root) {
		iHaveSavedCheckbox = root.findViewById(R.id.i_saved_my_qr_checkbox);
		qrImageView = root.findViewById(R.id.qr_image);
		actionButton = root.findViewById(R.id.action_button);

		iHaveSavedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveAndSharePresenter.iHaveSavedChecked(isChecked);
			}
		});

		root.findViewById(R.id.action_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndSharePresenter.actionButtonClicked();
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		saveAndSharePresenter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void showIHaveSavedQRState() {
		iHaveSavedCheckbox.setVisibility(View.VISIBLE);
		actionButton.setText(R.string.kinrecovery_done);
		setActionButtonEnabled(iHaveSavedCheckbox.isChecked());
	}

	@Override
	public void setActionButtonEnabled(boolean isEnabled) {
		actionButton.setEnabled(isEnabled);
		actionButton.setClickable(true);
	}

	@Override
	public void showErrorTryAgainLater() {
		new Builder(getActivity(), R.style.KinrecoveryAlertDialogTheme)
			.setTitle(R.string.kinrecovery_something_went_wrong_title)
			.setMessage(R.string.kinrecovery_could_not_load_the_qr_please_try_again_later)
			.setNegativeButton(R.string.kinrecovery_cancel, null)
			.create()
			.show();
	}

	public void setNextStepListener(@NonNull final BackupNavigator nextStepListener) {
		this.nextStepListener = nextStepListener;
	}

	@Override
	public void setQRImage(Uri qrURI) {
		Bitmap qrBitmap;
		try {
			qrBitmap = Media.getBitmap(getContext().getContentResolver(), qrURI);
			qrImageView.setImageBitmap(qrBitmap);
		} catch (IOException e) {
			saveAndSharePresenter.couldNotLoadQRImage();
		}
	}

	@Override
	public void showSendIntent(Uri qrURI) {
		String myKinWallet = getString(R.string.kinrecovery_my_kin_wallet_qr_code);
		String backupCreated = getString(R.string.kinrecovery_backup_created_on);
		Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
		String dateString = SimpleDateFormat.getDateInstance().format(date);
		SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
		String time = timeFormat.format(date);

		StringBuilder subjectBuilder = new StringBuilder(myKinWallet).append("\n")
			.append(backupCreated).append(" ").append(dateString).append(" | ").append(time);

		Intent emailIntent = new Intent(Intent.ACTION_SEND)
			.setType("Image/*")
			.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			.putExtra(Intent.EXTRA_STREAM, qrURI)
			.putExtra(Intent.EXTRA_SUBJECT, myKinWallet)
			.putExtra(Intent.EXTRA_TEXT, subjectBuilder.toString());
		startActivity(Intent.createChooser(emailIntent, getString(R.string.kinrecovery_send_email)));
	}
}
