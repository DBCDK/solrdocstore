/*
 * Copyright (C) 2017 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-solr-doc-store-updater
 *
 * dbc-solr-doc-store-updater is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-solr-doc-store-updater is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.search.solrdocstore.updater.rest;

import dk.dbc.search.solrdocstore.updater.Config;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DBC {@literal <dbc.dk>}
 */
@Stateless
@Path("status")
public class Status {

    private static final Logger log = LoggerFactory.getLogger(Status.class);

    @Resource(lookup = Config.DATABASE)
    DataSource dataSource;

    public Status() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStatus() {
        log.info("getStatus called");

        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement stmt = connection.prepareStatement("SELECT clock_timestamp()") ;
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                resultSet.getTimestamp(1);
                return Response.ok(StatusResponse.ok()).build();
            }
            return Response.ok(StatusResponse.error("Could not get timestamp from database")).build();
        } catch (SQLException ex) {
            log.error("Error accessing database by status(rest): {}", ex.getMessage());
            log.debug("Error accessing database by status(rest):", ex);
            return Response.ok(StatusResponse.error("Cannot access database (" + ex.getMessage() + ")")).build();
        }
    }

    /**
     * Status payload
     */
    @SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static final class StatusResponse {

        public boolean ok;

        public String error;

        public StatusResponse() {
        }

        private StatusResponse(boolean ok, String error) {
            this.ok = ok;
            this.error = error;
        }

        public static StatusResponse ok() {
            return new StatusResponse(true, null);
        }

        public static StatusResponse error(String message) {
            return new StatusResponse(false, message);
        }
    }
}