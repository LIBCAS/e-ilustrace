package cz.inqool.eas.eil.download;

import cz.inqool.eas.common.dated.store.InstantGenerator;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.record.Record;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "eil_download_reference")
public class DownloadReference extends DomainObject<DownloadReference> {

    @GeneratorType(type = InstantGenerator.class, when = GenerationTime.INSERT)
    Instant created;

    String url;

    @Enumerated(value = EnumType.STRING)
    ImageForDownload referencedAttribute;

    @OneToOne
    Record record;

    boolean downloaded;

    boolean failed;
}
