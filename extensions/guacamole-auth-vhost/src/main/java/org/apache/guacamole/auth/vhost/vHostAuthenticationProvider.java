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

package org.apache.guacamole.auth.vhost;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthenticationProvider implementation which uses TOTP as an additional
 * authentication factor for users which have already been authenticated by
 * some other AuthenticationProvider.
 */
public class vHostAuthenticationProvider extends AbstractAuthenticationProvider {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates a new TOTPAuthenticationProvider that verifies users using TOTP.
     *
     * @throws GuacamoleException
     *     If a required property is missing, or an error occurs while parsing
     *     a property.
     */
    public vHostAuthenticationProvider() throws GuacamoleException {
        super();
    }

    @Override
    public String getIdentifier() {
        return "vhost";
    }

    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {

        getRequestHost(credentials.getRequest());
        
        // return new vHostUserContext(context);
        return context;

    }

    @Override
    public UserContext redecorate(UserContext decorated, UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        getRequestHost(credentials.getRequest());
        
        // return new vHostUserContext(context);
        return context;
    }
    
    private String getRequestHost(HttpServletRequest request) 
            throws GuacamoleException {
        
        String strUrl = request.getRequestURL().toString();
        String referrer = request.getHeader("referrer");
        
        try {
            URL url = new URL(strUrl);
            URI uri = url.toURI();
        
            logger.debug(">>>VHOST<<< Got request for host {}", url.getHost());
            logger.debug(">>>VHOST<<< Referrer: {}", referrer);
            
            return url.getHost();
            
        }
        catch (MalformedURLException e) {
            throw new GuacamoleServerException("Bad URL provided.", e);
        }
        catch (URISyntaxException e) {
            throw new GuacamoleServerException("URI Syntax exception.", e);
        }
        
    }

}
