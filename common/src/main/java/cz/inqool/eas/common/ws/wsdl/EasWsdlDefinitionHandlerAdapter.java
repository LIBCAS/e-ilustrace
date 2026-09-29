package cz.inqool.eas.common.ws.wsdl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class EasWsdlDefinitionHandlerAdapter extends WsdlDefinitionHandlerAdapter {
    @Override
    protected String transformLocation(String location, HttpServletRequest request) {
        String transformed = super.transformLocation(location, request);

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return transformed.replace("/{context}", contextPath + servletPath);
    }
}
