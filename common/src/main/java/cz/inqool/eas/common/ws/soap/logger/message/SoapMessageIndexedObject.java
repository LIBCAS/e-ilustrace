package cz.inqool.eas.common.ws.soap.logger.message;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.domain.index.field.Boost;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.FOLDING_AND_TOKENIZING;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.TEXT_SHORT_KEYWORD;

@Getter
@Setter
@Document(indexName = "eas_soap_message")
public class SoapMessageIndexedObject extends AuthoredIndexedObject<SoapMessage, SoapMessage> {

    @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, fielddata = true)
    protected String service;

    @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, fielddata = true)
    private String localPart;

    @Field(type = FieldType.Long)
    private Long duration;

    @Boost
    @Field(type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING)
    private String request;

    @Boost
    @Field(type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING)
    private String response;


    @Override
    public void toIndexedObject(SoapMessage obj) {
        super.toIndexedObject(obj);

        this.service = obj.getService();
        this.localPart = obj.getLocalPart();
        this.duration = obj.getDuration();
        this.request = obj.getRequest();
        this.response = obj.getResponse();
    }
}
