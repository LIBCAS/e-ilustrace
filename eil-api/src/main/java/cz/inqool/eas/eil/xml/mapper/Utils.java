package cz.inqool.eas.eil.xml.mapper;

import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.eil.author.Author;
import cz.inqool.eas.eil.institution.Institution;
import cz.inqool.eas.eil.person.Person;
import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.role.MarcRole;
import gov.loc.marc21.slim.DataFieldType;
import gov.loc.marc21.slim.SubfieldatafieldType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.eil.role.MarcRole.PRINTER;
import static cz.inqool.eas.eil.role.MarcRole.PUBLISHER;
import static cz.inqool.eas.eil.xml.mapper.Constants.*;

@Slf4j
public class Utils {

    public static ArchiveInputStream initZipArchiveInputStream(File file) {
        try {
            return new ZipArchiveInputStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new MissingObject("Specified archive " + file.getName() + " does not exist");
        }
    }

    public static Author parseAuthorAttributes(DataFieldType field) {
        Author author = new Author();
        String[] nameParts = new String[3]; // stores parsed name elements in order 1) name 2) regnal number 3) name supplement

        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    nameParts[0] = subfield.getValue();
                    break;
                case CODE_B:
                    nameParts[1] = subfield.getValue();
                    break;
                case CODE_C:
                    nameParts[2] = subfield.getValue();
                    break;
                case CODE_D:
                    setBirthDeathYears(author, subfield);
                    break;
                case CODE_7:
                    author.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        String fullName = Arrays.stream(nameParts).filter(Objects::nonNull).collect(Collectors.joining(" "));
        author.setFullName(fullName);
        return author;
    }

    public static Author updateAuthorAttributes(DataFieldType field, Author author) {
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_D:
                    setBirthDeathYears(author, subfield);
                    break;
                case CODE_7:
                    author.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        return author;
    }

    public static PublicationEntry createPublicationEntry(DataFieldType field) {
        PublicationEntry entry = new PublicationEntry();
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    entry.getPlacesOfPublication().add(subfield.getValue());
                    break;
                case CODE_B:
                    entry.getOriginators().add(subfield.getValue());
                    break;
                case CODE_C:
                    entry.setDate(subfield.getValue());
                    break;
            }
        });
        return entry;
    }

    public static void setBirthDeathYears(Person person, SubfieldatafieldType subfield) {
        String datesString = subfield.getValue();
        String[] splitString = datesString.split("-");

        person.setBirthYear(splitString[0]);
        if (splitString.length != 1) {
            person.setDeathYear(splitString[1]);
        }
    }

    /**
     * Name of institution, subject place etc. is typically stored in a subfield with code 'a'
     * @param field datafield that required subfield is extracted from
     * @return value of subfield tagged with 'a', null if the subfield is not present
     */
    public static String getNameFromDataField(DataFieldType field) {
        return field.getSubfield().stream()
                .filter(s -> s.getCode().equals(CODE_A))
                .map(SubfieldatafieldType::getValue)
                .findFirst().orElse(null);
    }

    public static Institution updateInstitutionAttributes(DataFieldType field, Institution institution) {
        field.getSubfield().forEach(subfield -> {
            if (subfield.getCode().equals(CODE_7)) {
                institution.setAuthorityCode(subfield.getValue());
            }
        });
        return institution;
    }

    public static boolean isNotPrtOrPbl(DataFieldType field) {
        return field.getSubfield().stream()
                .filter(s -> s.getCode().equals(CODE_4))
                .map(SubfieldatafieldType::getValue)
                .noneMatch(s -> s.equalsIgnoreCase(PRINTER.getLabel()) || s.equalsIgnoreCase(PUBLISHER.getLabel()));
    }

    public static Set<MarcRole> mapXmlRolesToMarcRoles(DataFieldType field) {
        return field.getSubfield().stream()
                .filter(s -> s.getCode().equals(CODE_4))
                .map(SubfieldatafieldType::getValue)
                .map(Utils::mapStringToMarcRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static MarcRole mapStringToMarcRole(String role) {
        return Arrays.stream(MarcRole.values())
                .filter(r -> r.getLabel().equalsIgnoreCase(role))
                .findFirst()
                .orElse(null);
    }
}
