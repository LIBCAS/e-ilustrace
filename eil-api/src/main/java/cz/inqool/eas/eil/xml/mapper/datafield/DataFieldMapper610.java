package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.subject.institution.SubjectInstitution;
import cz.inqool.eas.eil.subject.institution.SubjectInstitutionRepository;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.getNameFromDataField;

/**
 * {@link Record#subjectInstitutions}
 */
@Component
@Order(42)
public class DataFieldMapper610 implements DataFieldMapper {

    protected SubjectInstitutionRepository institutionRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String name = getNameFromDataField(field);
        SubjectInstitution institution = institutionRepository.findByName(name);
        institution = institution == null ? createSubjectInstitution(field) : updateSubjectInstitution(field, institution);
        record.getSubjectInstitutions().add(institution);
    }

    private SubjectInstitution createSubjectInstitution(DataFieldType field) {
        SubjectInstitution institution = new SubjectInstitution();
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    institution.setName(subfield.getValue());
                    break;
                case CODE_7:
                    institution.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return institution;
    }

    private SubjectInstitution updateSubjectInstitution(DataFieldType field, SubjectInstitution institution) {
        field.getSubfield().forEach(subfield -> {
            if (CODE_7.equals(subfield.getCode())) {
                institution.setAuthorityCode(subfield.getValue());
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return institution;
    }

    @Override
    public String getTag() {
        return TAG_610;
    }

    @Autowired
    public void setSubjectInstitutionRepository(SubjectInstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }
}
