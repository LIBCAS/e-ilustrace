package cz.inqool.eas.eil.institution.record;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.institution.Institution;
import cz.inqool.eas.eil.role.MarcRole;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static cz.inqool.eas.common.domain.DomainViews.*;

@Viewable
@ViewableClass(views = {DEFAULT})
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@Getter
@Setter
@Entity
@Table(name = "eil_record_institution")
public class RecordInstitution extends DomainObject<RecordInstitution> {

    @ViewableProperty(views = DEFAULT)
    @ViewableMapping(views = DEFAULT, mappedTo = LIST)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    Institution institution;

    @ViewableProperty(views = DEFAULT)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = MarcRole.class)
    @CollectionTable(name = "eil_record_institution_role", joinColumns = @JoinColumn(name = "record_institution_id"))
    @Column(name = "marc_role")
    @Enumerated(EnumType.STRING)
    Set<MarcRole> roles = new LinkedHashSet<>();

    @ViewableProperty(views = DEFAULT)
    boolean isMainWorkshop;
}
