package cz.inqool.eas.common.antivirus.scan;

import cz.inqool.eas.common.dated.store.DatedStore;

public class ScanStore extends DatedStore<Scan, Scan, QScan> {

    public ScanStore() {
        super(Scan.class);
    }
}
