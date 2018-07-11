package com.kin.ecosystem.base;

import android.net.Uri;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;
import com.kin.ecosystem.base.transformation.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

class BaseViewHolder<T> extends com.chad.library.adapter.base.BaseViewHolder<T> {

    BaseViewHolder(View view) {
        super(view);
    }

    /**
     * set image url with sizes.
     *
     * @return The BaseViewHolder for chaining.
     */
    protected BaseViewHolder setImageUrlResized(@IdRes int viewId, String imageURL, int width, int height) {
        ImageView view = getView(viewId);
        if (view != null) {
            Picasso.get()
                .load(Uri.parse(imageURL))
                .transform(new RoundedCornersTransformation(5, 0))
                .resize(width, height)
                .centerCrop()
                .into(view);
        }
        return this;
    }
}