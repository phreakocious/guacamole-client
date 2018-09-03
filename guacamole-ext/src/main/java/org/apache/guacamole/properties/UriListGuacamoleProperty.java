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

package org.apache.guacamole.properties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;

/**
 * A GuacamoleProperty whose value is a List of URIs. The string value is first
 * parsed to produce a list of string values, and then new URIs
 * are created which are at least valid URIs.
 */
public abstract class UriListGuacamoleProperty implements GuacamoleProperty<List<URI>> {

    /**
     * A pattern which matches against the delimiters between values. This is
     * currently simply a comma and any following whitespace. Parts of the
     * input string which match this pattern will not be included in the parsed
     * result.
     */
    private static final Pattern DELIMITER_PATTERN = Pattern.compile(",\\s*");

    @Override
    public List<URI> parseValue(String values) throws GuacamoleException {

        // If no property provided, return null.
        if (values == null)
            return null;

        // Split string into a list of individual values
        List<String> stringValues = Arrays.asList(DELIMITER_PATTERN.split(values));
        if (stringValues.isEmpty())
            return null;
        
        List<URI> uriValues = Collections.<URI>emptyList();
        for (String value : stringValues) {
            try {
                URI uri = new URI(value);
                uriValues.add(uri);
            }
            catch(URISyntaxException e) {
                throw new GuacamoleServerException("Invalid URI specified.", e);
            }
            
        }

        return uriValues;

    }

}
