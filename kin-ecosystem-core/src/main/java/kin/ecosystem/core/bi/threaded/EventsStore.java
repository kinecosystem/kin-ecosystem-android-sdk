package kin.ecosystem.core.bi.threaded;

import java.util.concurrent.atomic.AtomicBoolean;
import kin.ecosystem.core.bi.threaded.events.CommonProxy;
import kin.ecosystem.core.bi.threaded.events.CommonReadonly;
import kin.ecosystem.core.bi.threaded.events.UserProxy;
import kin.ecosystem.core.bi.threaded.events.UserReadonly;

public final class EventsStore {
    private static final Object userModifierMutex = new Object();
    private static final Object commonModifierMutex = new Object();

    private static final UserProxy user = new UserProxy();
    private static final CommonProxy common = new CommonProxy();

    private static AtomicBoolean isUserBeingModified;
    private static AtomicBoolean isCommonBeingModified;

    public interface DynamicValue<T> {
        T get();
    }

    public interface UserModifier {
        void modify(UserProxy mutable);
    }

    public interface CommonModifier {
        void modify(CommonProxy mutable);
    }

    public static void init() {
        init(null, null);
    }

    public static void init(UserModifier modifier) {
        init(modifier, null);
    }

    public static void init(CommonModifier modifier) {
        init(null, modifier);
    }

    public static void init(UserModifier userModifier, CommonModifier commonModifier) {
        isUserBeingModified = new AtomicBoolean(false);
        isCommonBeingModified = new AtomicBoolean(false);

        if (userModifier != null) {
            update(userModifier);
        }

        if (commonModifier != null) {
            update(commonModifier);
        }
    }

    public static UserReadonly user() {
        if (isUserBeingModified.get()) {
            synchronized (userModifierMutex) {
                return user.snapshot();
            }
        }

        return user.snapshot();
    }

    public static CommonReadonly common() {
        if (isCommonBeingModified.get()) {
            synchronized (commonModifierMutex) {
                return common.snapshot();
            }
        }

        return common.snapshot();
    }

    public static void update(UserModifier modifier) {
        synchronized (userModifierMutex) {
            isUserBeingModified.set(true);
            modifier.modify(user);
            isUserBeingModified.set(false);
        }
    }

    public static void update(CommonModifier modifier) {
        synchronized (userModifierMutex) {
            isUserBeingModified.set(true);
            modifier.modify(common);
            isUserBeingModified.set(false);
        }
    }
}
