package com.kin.ecosystem.base;

import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kin.ecosystem.base.transformation.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /**
     * Views indexed with their IDs
     */
    private final SparseArray<View> views;

    private BaseRecyclerAdapter adapter;

    /**
     * Associated user object and detect a change
     */
    private T associatedObject;


    public BaseViewHolder(final View view) {
        super(view);
        this.views = new SparseArray<>();
    }

    private int getClickPosition() {
        return getLayoutPosition();
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setGone(@IdRes int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for INVISIBLE.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setVisible(@IdRes int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    /**
     * Will set the text of a TextView.
     *
     * @param viewId The view id.
     * @param value  The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setText(@IdRes int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    /**
     * Sets the on click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BaseViewHolder for chaining.
     */
    @Deprecated
    public BaseViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * add childView id
     *
     * @param viewId add the child view id   can support childview click
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildClickListener(listener))}
     * <p>
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    public BaseViewHolder addOnClickListener(@IdRes final int viewId) {
        final View view = getView(viewId);
        if (view != null) {
            if (!view.isClickable()) {
                view.setClickable(true);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.getOnItemChildClickListener() != null) {
                        adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, getClickPosition());
                    }
                }
            });
        }

        return this;
    }

    /**
     * add long click view id
     *
     * @param viewId
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildLongClickListener(listener))}
     * <p>
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    public BaseViewHolder addOnLongClickListener(@IdRes final int viewId) {
        final View view = getView(viewId);
        if (view != null) {
            if (!view.isLongClickable()) {
                view.setLongClickable(true);
            }
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return adapter.getOnItemChildLongClickListener() != null &&
                            adapter.getOnItemChildLongClickListener().onItemChildLongClick(adapter, v, getClickPosition());
                }
            });
        }
        return this;
    }

    /**
     * set image url with sizes.
     *
     * @param viewId
     * @param imageURL
     * @param width
     * @param height
     * @return The BaseViewHolder for chaining.
     */
    protected BaseViewHolder setImageUrlResized(@IdRes int viewId, String imageURL, int width, int height) {
        ImageView view = getView(viewId);
        if (view != null) {
            Picasso.with(view.getContext())
                    .load(Uri.parse(imageURL))
                    .transform(new RoundedCornersTransformation(10, 0))
                    .resize(width, height)
                    .centerCrop()
                    .into(view);
        }
        return this;
    }

    /**
     * set view size.
     *
     * @param viewId
     * @param width
     * @param height
     * @return The BaseViewHolder for chaining.
     */
    protected BaseViewHolder setViewSize(@IdRes int viewId, int width, int height) {
        View view = getView(viewId);
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            params.height = height;
        }
        return this;
    }

    /**
     * Sets the adapter of a adapter view.
     *
     * @param adapter The adapter;
     * @return The BaseViewHolder for chaining.
     */
    protected BaseViewHolder setAdapter(BaseRecyclerAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public <E extends View> E getView(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (E) view;
    }

    /**
     * Retrieves the last converted object on this view.
     */
    public T getAssociatedObject() {
        return associatedObject;
    }

    /**
     * Should be called during convert
     */
    public void setAssociatedObject(T associatedObject) {
        this.associatedObject = associatedObject;
    }
}