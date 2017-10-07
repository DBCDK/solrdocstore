package dk.dbc.search.solrdocstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

    /**
     * This class defines the other classes that make up this JAX-RS application
     * by having the getClasses method return a specific set of resources.
     */
@ApplicationPath("/")
public class DocStoreApplication extends Application {
        private static final Logger log = LoggerFactory.getLogger(DocStoreApplication.class);

        @Override
        public Set<Class<?>> getClasses() {
            final Set<Class<?>> classes = new HashSet<>();
            classes.add(StatusBean.class);
            for (Class<?> clazz : classes) {
                log.info("Registered {} resource", clazz.getName());
            }
            return classes;
        }
    }

