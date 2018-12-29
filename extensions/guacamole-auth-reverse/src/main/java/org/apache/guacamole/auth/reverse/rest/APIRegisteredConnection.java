/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.auth.reverse.rest;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.protocol.GuacamoleConfiguration;

/**
 * A simple connection to expose through the REST endpoints.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class APIRegisteredConnection {
    
    /**
     * The name of this connection.
     */
    private String name;
    
    /**
     * The UUID of this connection.
     */
    private UUID uuid;
    
    /**
     * The identifier of this connection.
     */
    private String identifier;
    
    /**
     * The identifier of the parent connection group for this connection.
     */
    private String parentIdentifier;

    /**
     * The protocol of this connection.
     */
    private String protocol;
    
    /**
     * Map of all associated parameter values, indexed by parameter name.
     */
    private Map<String, String> parameters;
    
    /**
     * Map of all associated attributes by attribute identifier.
     */
    private Map<String, String> attributes;

    /**
     * The count of currently active connections using this connection.
     */
    private int activeConnections;

    /**
     * The date and time that this connection was last used, or null if this
     * connection has never been used or this information is unavailable.
     */
    private Date lastActive;

    /**
     * Create an empty APIConnection.
     */
    public APIRegisteredConnection() {}
    
    /**
     * Create an APIConnection from a Connection record. Parameters for the
     * connection will not be included.
     *
     * @param connection
     *     The connection to create this RegisteredConnection from.
     * 
     * @param uuid
     *     The UUID of this connection.
     * 
     * @throws GuacamoleException If a problem is encountered while
     *                            instantiating this new APIConnection.
     */
    public APIRegisteredConnection(Connection connection, UUID uuid) 
            throws GuacamoleException {

        // Set connection information
        this.name = connection.getName();
        this.identifier = connection.getIdentifier();
        this.parentIdentifier = connection.getParentIdentifier();
        this.activeConnections = connection.getActiveConnections();
        this.lastActive = connection.getLastActive();
        
        // Set protocol from configuration
        GuacamoleConfiguration configuration = connection.getConfiguration();
        this.protocol = configuration.getProtocol();

        // Associate any attributes
        this.attributes = connection.getAttributes();
        
        this.uuid = uuid;

    }

    /**
     * Returns the name of this connection.
     * @return The name of this connection.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this connection.
     * @param name The name of this connection.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the unique identifier for this connection.
     * @return The unique identifier for this connection.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identifier for this connection.
     * @param identifier The unique identifier for this connection.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Returns the unique identifier for this connection.
     * @return The unique identifier for this connection.
     */
    public String getParentIdentifier() {
        return parentIdentifier;
    }

    /**
     * Sets the parent connection group identifier for this connection.
     * @param parentIdentifier The parent connection group identifier 
     *                         for this connection.
     */
    public void setParentIdentifier(String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    /**
     * Returns the parameter map for this connection.
     * @return The parameter map for this connection.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameter map for this connection.
     * @param parameters The parameter map for this connection.
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns the protocol for this connection.
     * @return The protocol for this connection.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol for this connection.
     * @param protocol protocol for this connection.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Returns the number of currently active connections using this
     * connection.
     *
     * @return
     *     The number of currently active usages of this connection.
     */
    public int getActiveConnections() {
        return activeConnections;
    }

    /**
     * Set the number of currently active connections using this connection.
     *
     * @param activeConnections
     *     The number of currently active usages of this connection.
     */
    public void setActiveUsers(int activeConnections) {
        this.activeConnections = activeConnections;
    }

    /**
     * Returns a map of all attributes associated with this connection. Each
     * entry key is the attribute identifier, while each value is the attribute
     * value itself.
     *
     * @return
     *     The attribute map for this connection.
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets the map of all attributes associated with this connection. Each
     * entry key is the attribute identifier, while each value is the attribute
     * value itself.
     *
     * @param attributes
     *     The attribute map for this connection.
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Returns the date and time that this connection was last used, or null if
     * this connection has never been used or this information is unavailable.
     *
     * @return
     *     The date and time that this connection was last used, or null if this
     *     connection has never been used or this information is unavailable.
     */
    public Date getLastActive() {
        return lastActive;
    }

    /**
     * Sets the date and time that this connection was last used.
     *
     * @param lastActive
     *     The date and time that this connection was last used, or null if this
     *     connection has never been used or this information is unavailable.
     */
    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }
    
    /**
     * Get the UUID of this connection.
     * 
     * @return 
     *     The UUID of this connection.
     */
    public UUID getUUID() {
        return uuid;
    }

}
