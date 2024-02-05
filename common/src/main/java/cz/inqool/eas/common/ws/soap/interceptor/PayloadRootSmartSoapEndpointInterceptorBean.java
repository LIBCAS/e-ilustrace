package cz.inqool.eas.common.ws.soap.interceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadRootSmartSoapEndpointInterceptor;

public class PayloadRootSmartSoapEndpointInterceptorBean extends PayloadRootSmartSoapEndpointInterceptor implements InitializingBean {

    public PayloadRootSmartSoapEndpointInterceptorBean(EndpointInterceptor delegate, String namespaceUri, String localPart) {
        super(delegate, namespaceUri, localPart);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        EndpointInterceptor interceptor = getDelegate();
        if (interceptor instanceof InitializingBean) {
            ((InitializingBean) interceptor).afterPropertiesSet();
        }
    }
}
