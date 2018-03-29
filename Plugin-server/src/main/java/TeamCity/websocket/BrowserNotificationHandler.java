package TeamCity.websocket;

import com.intellij.openapi.diagnostic.Logger;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.atmosphere.util.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.atmosphere.cpr.ApplicationConfig.PROPERTY_USE_STREAM;


public class BrowserNotificationHandler extends AbstractReflectorAtmosphereHandler {

    private static final Logger LOG = Logger.getInstance(BrowserNotificationHandler.class.getName());

    public BrowserNotificationHandler(){

    }
    @Override
    public final void onRequest(AtmosphereResource resource) throws IOException {
        LOG.info("HELLO------------->>>>>>>>>>>>>>>>>>>>");
        onStateChange(resource.getAtmosphereResourceEvent());
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event)
            throws IOException {

        Object message = event.getMessage();
        AtmosphereResource resource = event.getResource();
        AtmosphereResponse r = resource.getResponse();
        AtmosphereRequest request = resource.getRequest();

        boolean writeAsBytes = IOUtils.isBodyBinary(request);
        if (message == null) {
            LOG.info("Message was null for AtmosphereEvent {}");
            return;
        }

        if (resource.getSerializer() != null) {
            try {

                if (message instanceof List) {
                    for (Object s : (List<Object>) message) {
                        resource.getSerializer().write(resource.getResponse().getOutputStream(), s);
                    }
                } else {
                    resource.getSerializer().write(resource.getResponse().getOutputStream(), message);
                }
            } catch (Throwable ex) {
                LOG.info("Serializer exception: message: {}", ex);
                throw new IOException(ex);
            }
        } else {
            boolean isUsingStream = true;
            Object o = resource.getRequest().getAttribute(PROPERTY_USE_STREAM);
            if (o != null) {
                isUsingStream = (Boolean) o;
            }

            if (!isUsingStream) {
                try {
                    r.getWriter();
                } catch (IllegalStateException e) {
                    isUsingStream = true;
                }
                if (writeAsBytes) {
                    throw new IllegalStateException("Cannot write bytes using PrintWriter");
                }
            }

            if (message instanceof List) {
                Iterator<Object> i = ((List) message).iterator();
                try {
                    Object s;
                    while (i.hasNext()) {
                        s = i.next();
                        if (String.class.isAssignableFrom(s.getClass())) {
                            if (isUsingStream) {
                                r.getOutputStream().write(s.toString().getBytes(r.getCharacterEncoding()));
                            } else {
                                r.getWriter().write(s.toString());
                            }
                        } else if (byte[].class.isAssignableFrom(s.getClass())) {
                            if (isUsingStream) {
                                r.getOutputStream().write((byte[]) s);
                            } else {
                                r.getWriter().write(s.toString());
                            }
                        } else {
                            if (isUsingStream) {
                                r.getOutputStream().write(s.toString().getBytes(r.getCharacterEncoding()));
                            } else {
                                r.getWriter().write(s.toString());
                            }
                        }
                        i.remove();
                    }
                } catch (IOException ex) {
                    event.setMessage(new ArrayList<String>().addAll((List) message));
                    throw ex;
                }

                if (isUsingStream) {
                    r.getOutputStream().flush();
                } else {
                    r.getWriter().flush();
                }
            } else {
                if (isUsingStream) {
                    r.getOutputStream().write(writeAsBytes ? (byte[])message : message.toString().getBytes(r.getCharacterEncoding()));
                    r.getOutputStream().flush();
                } else {
                    r.getWriter().write(message.toString());
                    r.getWriter().flush();
                }
            }
        }
        postStateChange(event);
    }

}
