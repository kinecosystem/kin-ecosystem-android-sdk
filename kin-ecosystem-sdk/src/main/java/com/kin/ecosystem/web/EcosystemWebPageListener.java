package com.kin.ecosystem.web;

public interface EcosystemWebPageListener {
    void onPageLoaded();

    void onPageCancel();

    void onPageResult(String result);
}
