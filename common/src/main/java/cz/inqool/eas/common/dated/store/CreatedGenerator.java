package cz.inqool.eas.common.dated.store;

import cz.inqool.eas.common.dated.Dated;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

import java.time.Instant;

public class CreatedGenerator implements ValueGenerator<Instant> {

    public static ThreadLocal<Boolean> bypassed = ThreadLocal.withInitial(() -> false); //can be used to disable generator and save preset value

    @Override
    public Instant generateValue(Session session, Object owner) {
        if (bypassed.get()) {
            return ((Dated) owner).getCreated();
        }
        return InstantGenerator.generateValue();
    }
}
