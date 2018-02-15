package com.kin.ecosystem.web;

/**
 * Created by nitzantomer on 14/02/2018.
 */

public interface EcosystemWebPageListener {
    void onPageLoaded();

    void onPageCancel();

    void onPageResult(String result);
}
