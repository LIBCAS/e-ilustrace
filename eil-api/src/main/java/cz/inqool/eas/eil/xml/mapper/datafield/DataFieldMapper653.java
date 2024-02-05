package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.keyword.Keyword;
import cz.inqool.eas.eil.keyword.KeywordRepository;
import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.CODE_A;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_653;

/**
 * {@link Record#keywords}
 */
@Component
@Order(51)
public class DataFieldMapper653 implements DataFieldMapper {
    private KeywordRepository keywordRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        field.getSubfield().forEach(subfield -> {
            if (subfield.getCode().equals(CODE_A)) {
                Keyword keyword = keywordRepository.findByLabel(subfield.getValue());
                if (keyword == null) {
                    keyword = new Keyword();
                    keyword.setLabel(subfield.getValue());
                }
                record.getKeywords().add(keyword);
            }
        });
    }

    @Override
    public String getTag() {
        return TAG_653;
    }

    @Autowired
    public void setKeywordRepository(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }
}
