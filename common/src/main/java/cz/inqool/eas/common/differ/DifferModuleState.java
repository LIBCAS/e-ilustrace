package cz.inqool.eas.common.differ;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Simple enabler/disabler of DifferModule diffing.
 * If Differ module is not enabled then this class practically does nothing as it is only a class with a flag switching.
 *
 * If disabled then Differ Module will not proceed with diffing.
 *
 * Can be used to (temporary) disabled diffing in some situations. E.g. data init with DelegateDataInitializer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DifferModuleState {
    private static final ThreadLocal<Boolean> diffingActive = ThreadLocal.withInitial(() -> true);

    public static void enable() {
        diffingActive.set(true);
    }

    public static void disable() {
        diffingActive.set(false);
    }

    public static boolean isDisabled() {
        return !isEnabled();
    }

    public static boolean isEnabled() {
        return diffingActive.get();
    }
}
