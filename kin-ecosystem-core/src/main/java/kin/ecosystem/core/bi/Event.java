package kin.ecosystem.core.bi;

import kin.ecosystem.core.bi.events.User;
import kin.ecosystem.core.bi.events.Common;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
