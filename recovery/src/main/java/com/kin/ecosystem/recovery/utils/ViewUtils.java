package com.kin.ecosystem.recovery.utils;


import android.support.constraint.Group;
import android.view.View;

public class ViewUtils {

	public static void registerToGroupOnClickListener(Group group, View root, View.OnClickListener listener) {
		int refIds[] = group.getReferencedIds();
		for (int id : refIds) {
			root.findViewById(id).setOnClickListener(listener);
		}
	}
}
