package com.kin.ecosystem.bi;

import com.kin.ecosystem.bi.events.User;
import com.kin.ecosystem.bi.events.Common;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
