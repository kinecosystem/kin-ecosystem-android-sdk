package com.kin.ecosystem.base.transformation;

import static com.kin.ecosystem.base.transformation.CornerType.ALL;
import static com.kin.ecosystem.base.transformation.CornerType.BOTTOM;
import static com.kin.ecosystem.base.transformation.CornerType.BOTTOM_LEFT;
import static com.kin.ecosystem.base.transformation.CornerType.BOTTOM_RIGHT;
import static com.kin.ecosystem.base.transformation.CornerType.LEFT;
import static com.kin.ecosystem.base.transformation.CornerType.RIGHT;
import static com.kin.ecosystem.base.transformation.CornerType.TOP;
import static com.kin.ecosystem.base.transformation.CornerType.TOP_LEFT;
import static com.kin.ecosystem.base.transformation.CornerType.TOP_RIGHT;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT})
@Retention(RetentionPolicy.SOURCE)
@interface CornerType {
	int ALL = 0;
	int TOP_LEFT = 1;
	int TOP_RIGHT = 2;
	int BOTTOM_LEFT = 3;
	int BOTTOM_RIGHT = 4;
	int TOP = 5;
	int BOTTOM = 6;
	int LEFT = 7;
	int RIGHT = 8;
}