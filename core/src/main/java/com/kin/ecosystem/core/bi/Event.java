package com.kin.ecosystem.core.bi;

import com.kin.ecosystem.core.bi.events.Common;
import com.kin.ecosystem.core.bi.events.User;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
