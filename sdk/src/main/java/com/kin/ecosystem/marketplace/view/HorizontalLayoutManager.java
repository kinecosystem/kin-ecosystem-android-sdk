package com.kin.ecosystem.marketplace.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;

public class HorizontalLayoutManager extends LinearLayoutManager {

	private static final String TAG = HorizontalLayoutManager.class.getSimpleName();
	private static final String INDEX_OUT_OF_BOUNDS_INCONSISTENCY = "IndexOutOfBounds inconsistency";

	public HorizontalLayoutManager(Context context) {
		super(context, LinearLayoutManager.HORIZONTAL, false);
	}

	@Override
	public void onLayoutChildren(Recycler recycler, State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (IndexOutOfBoundsException e) {
			Logger.log(new Log().withTag(TAG).text(INDEX_OUT_OF_BOUNDS_INCONSISTENCY).priority(Log.WARN));
		}
	}
}
