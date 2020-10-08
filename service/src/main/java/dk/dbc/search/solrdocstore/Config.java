package dk.dbc.search.solrdocstore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

@Stateless
@Startup
public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private long jobPruneMinutes;
    private String systemName;
    private Boolean allowNonEmptySchema;
    private int[] openAgencyValidateTime;
    private long deleteMarkedDelay;
    private long holdingsQueueDelay;
    private String vipCoreEndpoint;
    private Client httpClient;

    @PostConstruct
    public void loadProperties() {
        Properties props = findProperties("solr-doc-store-service");
        jobPruneMinutes = getValue(props, "jobPruneMinutes", "JOB_PRUNE_MINUTES", "60", null, Long::parseUnsignedLong);
        // Name displayed in frontend to tell the user which system they are looking at (FBSTest, Cisterne etc.)
        systemName = getValue(props, "systemName", "SYSTEM_NAME", "System navn ikke konfigureret", null);
        allowNonEmptySchema = getValue(props, "allowNonEmptySchema", "ALLOW_NON_EMPTY_SCHEMA", "false", null, Boolean::parseBoolean);
        openAgencyValidateTime = getValue(props, "openAgencyValidateTime", "OPEN_AGENCY_VALIDATE_TIME", "04:23:17", null, Config::validateTime);
        // Number of milliseconds to delay bib entities that are being deleted
        deleteMarkedDelay = getValue(props, "deleteMarkedDelay", "DELETE_MARKED_DELAY", "200000", null, Long::parseUnsignedLong);
        holdingsQueueDelay = getValue(props, "holdingsQueueDelay", "HOLDINGS_QUEUE_DELAY", "300000", null, Long::parseUnsignedLong);
        vipCoreEndpoint = getValue(props, "vipCoreEndpoint", "VIPCORE_ENDPOINT", "http://vipcore.iscrum-vip-staging.svc.cloud.dbc.dk/1.0", "No URL found for VipCore");
        log.info("Reading/verifying configuration");
        String userAgent = getValue(props, "userAgent", "USER_AGENT", "SolrDocStoreService/1.0", null);
        log.debug("Using: {} as HttpUserAgent", userAgent);
        this.httpClient = clientBuilder()
                .register((ClientRequestFilter) (ClientRequestContext context) ->
                        context.getHeaders().putSingle("User-Agent", userAgent)
                )
                .build();

    }

    public String getVipCoreEndpoint() {
        return vipCoreEndpoint;
    }

    public long getJobPruneMinutes() {
        return jobPruneMinutes;
    }

    public String getSystemName() {
        return systemName;
    }

    public Boolean getAllowNonEmptySchema() {
        return allowNonEmptySchema;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public int[] getOpenAgencyValidateTime() {
        return openAgencyValidateTime;
    }

    public long getDeleteMarkedDelay() {
        return deleteMarkedDelay;
    }

    public long getHoldingQueueDelay() {
        return holdingsQueueDelay;
    }

    public Client getHttpClient() {
        return httpClient;
    }

    private static String getValue(Properties props, String propertyName, String envName, String defaultValue, String error) {
        String value = props.getProperty(propertyName);
        if (value == null) {
            value = System.getenv(envName);
        }
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            throw new EJBException(error + ". " +
                                   "Please provide env:" + envName +
                                   " or property:" + propertyName);
        }
        return value;
    }

    private static <T> T getValue(Properties props, String propertyName, String envName, String defaultValue, String error, Function<String, T> mapper) {
        return mapper.apply(getValue(props, propertyName, envName, defaultValue, error));
    }

    private Properties findProperties(String resourceName) {
        try {
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourceName + ".properties");
            if (resource != null) {
                Properties fromFile = new Properties();
                try {
                    fromFile.load(resource);
                    return fromFile;
                } catch (IOException e) {
                    throw new EJBException("Property file" + resourceName + ".properties exists. But I can't load it?", e);
                }
            }
            Object lookup = InitialContext.doLookup(resourceName);
            if (lookup instanceof Properties) {
                return (Properties) lookup;
            } else if (lookup != null) {
                throw new NamingException("Found " + resourceName + ", but not of type Properties of type: " + lookup.getClass().getTypeName());
            }
        } catch (NamingException ex) {
            log.debug("Exception: {}", ex.getMessage());
        }
        return new Properties();
    }

    /**
     * Create a clientBuilder (useful for unittesting with specific
     * implementation)
     *
     * @return Http client builder
     */
    protected ClientBuilder clientBuilder() {
        return ClientBuilder.newBuilder();
    }

    private static int[] validateTime(String time) {
        String[] parts = time.split(":");
        try {
            if (parts.length == 3) {
                int[] ret = new int[3];

                for (int i = 0 ; i < 3 ; i++) {
                    if (parts[i].isEmpty()) {
                        throw new IllegalArgumentException("Empty part");
                    }
                    ret[i] = Integer.parseUnsignedInt(parts[i], 10);
                }
                return ret;
            }
        } catch (RuntimeException ex) {
            log.error("Error processing time: {}", ex.getMessage());
            log.debug("Error processing time: ", ex);
        }
        throw new IllegalArgumentException("Invalid time: " + time);
    }
}
