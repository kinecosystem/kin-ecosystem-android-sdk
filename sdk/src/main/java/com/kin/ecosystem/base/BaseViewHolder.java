package com.kin.ecosystem.base;

import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kin.ecosystem.base.transformation.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /**
     * Views indexed with their IDs
     */
    private final SparseArray<View> views;

    BaseViewHolder(View view) {
        super(view);
        views = new SparseArray<>();
    }

	/**
	 * @param viewId
	 * @param <E>
	 * @return
	 */
	protected <E extends View> E getView(@IdRes int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = itemView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (E) view;
	}

	/**
	 *
	 *
	 * @param viewId
	 * @param width
	 * @param height
	 */
	protected void setViewSize(@IdRes int viewId, int width, int height) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = width;
			params.height = height;
		}
	}

	protected void setViewHeight(@IdRes int viewId, int height) {
		View view = getView(viewId);
		if (view != null) {
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.height = height;
		}
	}

	/**
	 * @param viewId The view id.
	 * @param value  The text to put in the text view.
	 */
	protected void setText(@IdRes int viewId, CharSequence value) {
		TextView view = getView(viewId);
		view.setText(value);
	}

	/**
	 * Set spannable test for text font manipulation.
	 *
	 * @param viewId
	 * @param spannable
	 */
	protected void setSpannableText(@IdRes int viewId, Spannable spannable) {
		TextView view = getView(viewId);
		view.setText(spannable, TextView.BufferType.SPANNABLE);
	}

	/**
	 * Set the color of the test
	 *
	 * @param viewId
	 * @param color
	 */
	protected void setTextColor(@IdRes int viewId, @ColorInt int color) {
		TextView view = getView(viewId);
		view.setTextColor(color);
	}

	/**
	 * Set a image resource.
	 *
	 * @param viewId
	 * @param imageResId
	 */
	protected void setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
		ImageView view = getView(viewId);
		view.setImageResource(imageResId);
	}

	/**
	 * Load image from url and set to src.
	 *
	 * @param viewId
	 * @param imageURL
	 * @param width
	 * @param height
	 */
    protected void setImageUrlResized(@IdRes int viewId, String imageURL, int width, int height) {
        ImageView view = getView(viewId);
        if (view != null) {
            Picasso.get()
                .load(Uri.parse(imageURL))
                .transform(new RoundedCornersTransformation(5, 0))
                .resize(width, height)
                .centerCrop()
                .into(view);
        }
    }
}