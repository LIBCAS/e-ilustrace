package cz.inqool.eas.common.ws.soap.logger.message;

import cz.inqool.eas.common.authored.AuthoredApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "SOAP message logger", description = "SOAP message logger CRUD API")
@ResponseBody
@RequestMapping("${soap-logger.message.url}")
public class SoapMessageApi extends AuthoredApi<
        SoapMessage,
        SoapMessageDetail,
        SoapMessageList,
        SoapMessageCreate,
        SoapMessageUpdate,
        SoapMessageService> {
}
