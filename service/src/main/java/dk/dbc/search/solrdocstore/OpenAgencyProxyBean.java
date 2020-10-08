package dk.dbc.search.solrdocstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dbc.openagency.http.VipCoreHttpClient;
import dk.dbc.vipcore.marshallers.LibraryRulesResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author DBC {@literal <dbc.dk>}
 */
@Stateless
public class OpenAgencyProxyBean {

    private static final Logger log = LoggerFactory.getLogger(OpenAgencyProxyBean.class);

    private static final ObjectMapper O = new ObjectMapper();

    @Inject
    Config config;

    private UriBuilder uriBase;
    private UriBuilder libraryRulesUri;

    @PostConstruct
    public void init() {
        this.uriBase = UriBuilder.fromUri(URI.create(config.getVipCoreEndpoint()))
                .path("api");
        this.libraryRulesUri = uriBase.clone()
// This constant when moved will make the dependency unneeded
// <dependency>
//     <groupId>dk.dbc</groupId>
//     <artifactId>openagency</artifactId>
//     <version>1.0.0-SNAPSHOT</version>
//     <classifier>vipcore-httpclient</classifier>
// </dependency>
                .path(VipCoreHttpClient.LIBRARY_RULES_PATH)
                .path("{agencyId}");
    }

    @Timed
    @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION")
    public OpenAgencyEntity loadOpenAgencyEntry(int agencyId) {
        URI uri = libraryRulesUri.build(agencyId);
        log.debug("Fetching vip-core uri: {}", uri);
        try (InputStream is = config.getHttpClient()
                .target(uri)
// At the moment we don't have a tracking id
//                .register((ClientRequestFilter) (ClientRequestContext context) ->
//                        context.getHeaders().putSingle(Constants.XDBCTRACKINGID, "")
//                )
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(InputStream.class)) {
            return openAgencyEntityFromInputStream(is);
        } catch (IOException ex) {
            log.error("Error happened while fetching vipCore library rules for agency {}: {}", agencyId, ex.getMessage());
            throw new EJBException(ex);
        }
    }

    public static OpenAgencyEntity openAgencyEntityFromInputStream(final InputStream is) throws IOException {
        LibraryRulesResponse libraryRulesResponse = O.readValue(is, LibraryRulesResponse.class);
        if (libraryRulesResponse != null &&
            libraryRulesResponse.getLibraryRules() != null &&
            !libraryRulesResponse.getLibraryRules().isEmpty()) {
            return new OpenAgencyEntity(libraryRulesResponse.getLibraryRules().get(0));
        } else {
            return null;
        }
    }

}
