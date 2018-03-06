package com.kin.ecosystem.poll.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.poll.presenter.PollWebViewPresenter;

public interface IPollWebView extends IBaseView<PollWebViewPresenter> {

    void showToast(String msg);

    void loadUrl();

    void renderJson(@NonNull final String pollJsonString);

    void close();
}
