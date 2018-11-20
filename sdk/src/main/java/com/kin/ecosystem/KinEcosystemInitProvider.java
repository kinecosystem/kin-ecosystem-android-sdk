package com.kin.ecosystem;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;

public class KinEcosystemInitProvider extends ContentProvider {

	private static final String TAG = "KinEcosystemAutoInit";

	@Override
	public boolean onCreate() {
		try {
			Kin.initialize(getContext());
		} catch (ClientException e) {
			Logger.log(new Log().withTag(TAG).text("KinEcosystem sdk auto initialize failed"));
		} catch (BlockchainException e) {
			Logger.log(new Log().withTag(TAG).text("KinEcosystem sdk auto initialize failed"));
		}
		return false;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
		@Nullable String[] selectionArgs, @Nullable String sortOrder) {
		return null;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		return null;
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
		@Nullable String[] selectionArgs) {
		return 0;
	}
}
