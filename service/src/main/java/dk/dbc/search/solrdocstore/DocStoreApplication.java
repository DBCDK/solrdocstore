package dk.dbc.search.solrdocstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This class defines the other classes that make up this JAX-RS application by
 * having the getClasses method return a specific set of resources.
 */
@ApplicationPath("/api")
public class DocStoreApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(DocStoreApplication.class);

    private static final Set<Class<?>> CLASSES = makeClasses();

    private static Set<Class<?>> makeClasses() {
        HashSet<Class<?>> classes = new HashSet<>();

        classes.add(StatusBean.class);
        classes.add(BibliographicBean.class);
        classes.add(HoldingsItemBean.class);
        classes.add(FrontendAPIBean.class);
        classes.add(DocumentRetrieveBean.class);
        classes.add(EvictAll.class);

        for (Class<?> clazz : classes) {
            log.info("Registered {} resource", clazz.getName());
        }
        return classes;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return CLASSES;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put("jersey.config.server.disableMoxyJson", true);

        return props;
    }
}