package cz.inqool.eas.common.exception.v2.rest;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class with common exception handling - processing methods
 */
@Beta
public class RestExceptionUtils {

    /**
     * Returns hierarchy 'distance' of two given classes.
     * <p>
     * If the classes are in no parent-child relationship of each other ({@code superClass} isn't actually a superclass
     * of {@code subClass}), value '-1' is returned. Otherwise, the method returns the number of steps required to
     * traverse from {@code subClass} to {@code superClass} (the distance from {@code subClass} to the {@code
     * superClass}). If both classes are the same, a value of '0' (zero) is returned.
     */
    public static int getDistance(@NonNull Class<? extends Throwable> superClass, @NonNull Class<? extends Throwable> subClass) {
        int distance;

        if (superClass.equals(subClass)) {
            distance = 0;
        } else if (superClass.isAssignableFrom(subClass)) {
            Class<?> currentClass = subClass.getSuperclass();
            distance = 1;

            while (!currentClass.equals(Object.class)) {
                if (currentClass.equals(superClass)) {
                    break;
                }
                currentClass = currentClass.getSuperclass();
                distance++;
            }
        } else {
            distance = -1;
        }

        return distance;
    }

    /**
     * Get current timestamp value
     */
    public static String getTimestamp() {
        return LocalDateTime.now().toString();
    }

    /**
     * Generate user reference of calling user (if possible)
     */
    public static UserReference getUserReference() {
        return UserGenerator.generateValue();
    }

    /**
     * Process and return HTTP status code from given request attributes
     */
    public static int getStatus(@NonNull RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes, RequestDispatcher.ERROR_STATUS_CODE);
        return Objects.requireNonNullElse(status, 999);
    }

    /**
     * Construct URL path from given HTTP request
     */
    public static String getUrlPath(@NonNull HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI() + getParameterString(request);
    }

    private static String getParameterString(@NonNull HttpServletRequest request) {
        if (request.getParameterMap().isEmpty()) {
            return "";
        }

        return request.getParameterMap().entrySet().stream()
                .map(entry -> Arrays.stream(entry.getValue())
                        .map(parameterValue -> Pair.of(entry.getKey(), parameterValue))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .map(parameterNameValuePair -> parameterNameValuePair.getFirst() + "=" + parameterNameValuePair.getSecond())
                .collect(Collectors.joining("&", "?", ""));
    }

    /**
     * Construct URL path from given request
     */
    public static String getUrlPath(@NonNull RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, RequestDispatcher.ERROR_REQUEST_URI);

        if (requestAttributes instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) requestAttributes;
            HttpMethod httpMethod = servletWebRequest.getHttpMethod();
            if (httpMethod != null) {
                return httpMethod + " " + path;
            }
        }

        return path;
    }

    /**
     * Obtain client IP address from given request
     *
     * @see <a href="https://simplesolution.dev/spring-boot-web-get-client-ip-address/#implement-request-service-to-get-client-s-ip-address">Implement
     * Request service to get Client’s IP Address</a>
     */
    public static String getClientIpAddress(@NonNull HttpServletRequest request) {
        final String LOCALHOST_IPV4 = "127.0.0.1";
        final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }

    private static <T> T getAttribute(@NonNull RequestAttributes requestAttributes, @NonNull String name) {
        //noinspection unchecked
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
