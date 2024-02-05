package cz.inqool.eas.common.alog;

import cz.inqool.eas.common.alog.event.Event;

public interface AlogAccessChecker {
    boolean checkListAccess();
    boolean checkRecordAccess(Event event);
}
