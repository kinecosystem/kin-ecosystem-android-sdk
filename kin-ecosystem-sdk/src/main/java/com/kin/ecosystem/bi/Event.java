package com.kin.ecosystem.bi;

import com.kin.ecosystem.bi.events.Common;
import com.kin.ecosystem.bi.events.User;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
