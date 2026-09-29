package cz.inqool.eas.common.utils;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static cz.inqool.eas.common.exception.ExceptionUtils.checked;

/**
 * Utility methods for working with XML.
 */
public class XmlUtils {

    protected static final Cache<Class<?>, JAXBContext> JAXB_CONTEXT_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();


    /**
     * Converts given object to an XML string
     */
    public static <T> String toXmlString(@Nullable T object) {
        StringWriter stringWriter = new StringWriter();

        marshall(object, (marshaller, obj) -> checked(() -> marshaller.marshal(obj, stringWriter)));

        return stringWriter.toString();
    }

    /**
     * Converts given object to an XML using given
     *
     * @param object object to be converted
     * @param marshallerConsumer marshalling function
     */
    public static <T> void toXml(@Nullable T object, @NotNull BiConsumer<Marshaller, T> marshallerConsumer) {
        marshall(object, marshallerConsumer);
    }

    /**
     * Converts given object to an XML response entity (for API endpoints - XML file providers)
     *
     * @param object object to be converted
     * @param filename name of response filename attachment
     */
    public static <T> ResponseEntity<StreamingResponseBody> toXmlResponse(@Nullable T object, @NotNull String filename) {
        StreamingResponseBody responseBody = outputStream -> toXml(object, (marshaller, obj) -> checked(() -> marshaller.marshal(obj, outputStream)));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_XML)
                .body(responseBody);
    }

    protected static <T> void marshall(@Nullable T object, @NotNull BiConsumer<Marshaller, T> marshallerConsumer) {
        if (object == null) {
            return;
        }

        try {
            JAXBContext context = getContext(object.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshallerConsumer.accept(marshaller, object);
        } catch (JAXBException e) {
            throw new GeneralException("Failed to marshall object.", e);
        }
    }

    /**
     * Reads an XML node into POJO of given type.
     *
     * @param source XML node
     * @param type type of object to convert the node to
     */
    public static <T> T fromXml(@NotNull Node source, @NotNull Class<T> type) {
        //noinspection unchecked
        return unmarshall(source, type, (unmarshaller, node) -> (T) checked(() -> unmarshaller.unmarshal(node)));
    }

    /**
     * Reads an XML input stream into POJO of given type.
     *
     * @param source input stream with XML data
     * @param type type of object to convert the stream to
     */
    public static <T> T fromXml(@NotNull InputStream source, @NotNull Class<T> type) {
        //noinspection unchecked
        return unmarshall(source, type, (unmarshaller, inputStream) -> (T) checked(() -> unmarshaller.unmarshal(inputStream)));
    }

    public static <TARGET, SOURCE> TARGET unmarshall(@Nullable SOURCE source, @NotNull Class<TARGET> type, @NotNull BiFunction<Unmarshaller, SOURCE, TARGET> marshallerConsumer) {
        if (source == null) {
            return null;
        }

        try {
            JAXBContext context = getContext(type);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            return marshallerConsumer.apply(unmarshaller, source);
        } catch (JAXBException e) {
            throw new GeneralException("Failed to marshall object.", e);
        }
    }

    public static String prettyPrint(String xmlString) {
        if (xmlString == null) {
            return null;
        }

        try {
            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, Charsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new GeneralException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }

    @SneakyThrows
    protected static JAXBContext getContext(@NotNull Class<?> type) {
        return JAXB_CONTEXT_CACHE.get(type, () -> JAXBContext.newInstance(type));
    }
}
