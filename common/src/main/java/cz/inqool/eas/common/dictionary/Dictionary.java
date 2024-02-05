package cz.inqool.eas.common.dictionary;

import cz.inqool.eas.common.authored.Authored;
import cz.inqool.eas.common.multiString.MultiString;

import java.time.Instant;

public interface Dictionary<ROOT> extends Authored<ROOT> {
    void setName(String name);

    void setMultiName(MultiString multiName);

    void setCode(String code);
    void setActive(boolean active);
    void setValidFrom(Instant from);
    void setValidTo(Instant to);
    void setOrder(Integer order);

    String getName();

    MultiString getMultiName();

    String getCode();
    boolean isActive();
    Instant getValidFrom();
    Instant getValidTo();
    Integer getOrder();
}
