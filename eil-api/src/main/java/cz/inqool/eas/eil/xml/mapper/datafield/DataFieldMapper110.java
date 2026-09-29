package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.institution.Institution;
import cz.inqool.eas.eil.institution.InstitutionRepository;
import cz.inqool.eas.eil.institution.record.RecordInstitution;
import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.*;

/**
 * {@link Record#getMainWorkshop()}
 */
@Component
@Order(6)
public class DataFieldMapper110 implements DataFieldMapper {

    private InstitutionRepository institutionRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String name = getNameFromDataField(field);
        Institution institution = institutionRepository.findByName(name);
        institution = institution == null ? createInstitution(field) : updateInstitution(field, institution);
        record.getInstitutions().add(createMainRecordWorkshop(field, institution));
    }

    private Institution createInstitution(DataFieldType field) {
        Institution institution = new Institution();
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
        return institutionRepository.create(institution);
    }

    private Institution updateInstitution(DataFieldType field, Institution institution) {
        institution = updateInstitutionAttributes(field, institution);
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return institutionRepository.update(institution);
    }

    private static RecordInstitution createMainRecordWorkshop(DataFieldType field, Institution workshop) {
        RecordInstitution recordInstitution = new RecordInstitution();
        recordInstitution.setInstitution(workshop);
        recordInstitution.getRoles().addAll(mapXmlRolesToMarcRoles(field));
        recordInstitution.setMainWorkshop(true);
        return recordInstitution;
    }

    @Override
    public String getTag() {
        return TAG_110;
    }

    @Autowired
    public void setInstitutionRepository(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }
}
