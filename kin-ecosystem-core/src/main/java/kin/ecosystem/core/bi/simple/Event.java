package kin.ecosystem.core.bi.simple;

import kin.ecosystem.core.bi.simple.events.Common;
import kin.ecosystem.core.bi.simple.events.User;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
