package kin.ecosystem.core.bi.threaded;

import kin.ecosystem.core.bi.threaded.events.User;
import kin.ecosystem.core.bi.threaded.events.Common;

public interface Event {
    Common getCommon();
    void setCommon(Common common);

    User getUser();
    void setUser(User user);
}
