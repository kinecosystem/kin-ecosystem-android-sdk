package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kin.ecosystem.recovery.R;

public class WellDoneBackupFragment extends Fragment {

	public static WellDoneBackupFragment newInstance() {
		return new WellDoneBackupFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_well_done_backup, container, false);
		return root;
	}
}
