package com.kin.ecosystem.recovery.backup.view;

import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.SaveAndSharePresenter;
import com.kin.ecosystem.recovery.backup.presenter.SaveAndSharePresenterImpl;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGeneratorImpl;
import com.kin.ecosystem.recovery.qr.QRFileUriHandlerImpl;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SaveAndShareFragment extends Fragment implements SaveAndShareView {

	public static final String KEY_ACCOUNT_KEY = "key_account_key";

	public static SaveAndShareFragment newInstance(BackupNextStepListener listener, String key) {
		SaveAndShareFragment fragment = new SaveAndShareFragment();
		fragment.setNextStepListener(listener);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_ACCOUNT_KEY, key);
		fragment.setArguments(bundle);
		return fragment;
	}

	private BackupNextStepListener nextStepListener;
	private SaveAndSharePresenter saveAndSharePresenter;

	private CheckBox iHaveSavedCheckbox;
	private TextView iHaveSavedText;
	private ImageView qrImageView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_save_and_share_qr, container, false);
		initViews(root);
		String key = getArguments().getString(KEY_ACCOUNT_KEY, null);
		final QRBarcodeGenerator qrBarcodeGenerator = new QRBarcodeGeneratorImpl(
			new QRFileUriHandlerImpl(getContext()));
		saveAndSharePresenter = new SaveAndSharePresenterImpl(nextStepListener, qrBarcodeGenerator, key);
		saveAndSharePresenter.onAttach(this);
		return root;
	}

	private void initViews(View root) {
		iHaveSavedCheckbox = root.findViewById(R.id.i_saved_my_qr_checkbox);
		iHaveSavedText = root.findViewById(R.id.i_saved_my_qr_text);
		qrImageView = root.findViewById(R.id.qr_image);

		iHaveSavedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveAndSharePresenter.iHaveSavedChecked(isChecked);
			}
		});
		iHaveSavedText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iHaveSavedCheckbox.performClick();
			}
		});

		root.findViewById(R.id.send_email_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndSharePresenter.sendQREmailClicked();
			}
		});

	}

	@Override
	public void showIHaveSavedCheckBox() {
		iHaveSavedCheckbox.setVisibility(View.VISIBLE);
		iHaveSavedText.setVisibility(View.VISIBLE);
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

	private void setNextStepListener(@NonNull final BackupNextStepListener nextStepListener) {
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
			.setType("*/*")
			.putExtra(Intent.EXTRA_STREAM, qrURI)
			.putExtra(Intent.EXTRA_SUBJECT, myKinWallet)
			.putExtra(Intent.EXTRA_TEXT, subjectBuilder.toString());
		startActivity(Intent.createChooser(emailIntent , getString(R.string.kinrecovery_send_email)));
	}
}
