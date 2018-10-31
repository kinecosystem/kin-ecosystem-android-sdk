package com.kin.ecosystem.poll.view;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.poll.presenter.PollWebViewPresenter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IPollWebView extends IBaseView<PollWebViewPresenter> {

    int ORDER_SUBMISSION_FAILED = 0x00000001;
    int SOMETHING_WENT_WRONG = 0x00000002;

    @IntDef({ORDER_SUBMISSION_FAILED, SOMETHING_WENT_WRONG})
    @Retention(RetentionPolicy.SOURCE)
    @interface Message {

    }

    void showToast(@Message final int msg);

    void loadUrl();

    void renderJson(@NonNull final String pollJsonString);

    void close();

    void showToolbar();

    void hideToolbar();

    void setTitle(String title);
}
