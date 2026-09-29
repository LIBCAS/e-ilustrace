package cz.inqool.eas.common.dictionary;

import cz.inqool.eas.common.authored.AuthoredIndexed;
import cz.inqool.eas.common.multiString.MultiStringIndexedObject;
import cz.inqool.eas.common.projection.Projectable;

import java.time.Instant;

public interface DictionaryIndexed<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> extends AuthoredIndexed<ROOT, PROJECTED> {
    String getName();

    MultiStringIndexedObject getMultiName();

    String getCode();
    boolean isActive();
    Instant getValidFrom();
    Instant getValidTo();
}
