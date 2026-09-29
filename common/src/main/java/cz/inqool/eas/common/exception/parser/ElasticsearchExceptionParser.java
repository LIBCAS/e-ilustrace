package cz.inqool.eas.common.exception.parser;

import org.elasticsearch.ElasticsearchException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticsearchExceptionParser implements ExceptionMessageParser<ElasticsearchException> {

    @Override
    public Class<ElasticsearchException> getType() {
        return ElasticsearchException.class;
    }

    @Override
    public String getMessage(ElasticsearchException throwable) {
        StringBuilder sb = new StringBuilder(throwable.getMessage());
        for (String key : throwable.getHeaderKeys()) {
            List<String> header = throwable.getHeader(key);
            sb.append("\n\t").append(String.format("%-20s", key)).append(": ").append(header);
        }
        return sb.toString();
    }
}
