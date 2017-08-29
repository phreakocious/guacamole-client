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

package org.apache.guacamole.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filtering object which replaces tokens of the form "${TOKEN_NAME}" with
 * their corresponding values. Unknown tokens are not replaced. If TOKEN_NAME
 * is a valid token, the literal value "${TOKEN_NAME}" can be included by using
 * "$${TOKEN_NAME}".
 */
public class TokenFilter {

    /**
     * Regular expression which matches individual tokens, with additional
     * capturing groups for convenient retrieval of leading text, the possible
     * escape character preceding the token, the name of the token, and the
     * entire token itself.
     */
    private static final Pattern tokenPattern = Pattern.compile("(.*?)(^|.)(\\$\\{([A-Za-z0-9_]*)\\})");

    /**
     * The index of the capturing group within tokenPattern which matches
     * non-token text preceding a possible token.
     */
    private static final int LEADING_TEXT_GROUP = 1;

    /**
     * The index of the capturing group within tokenPattern which matches the
     * character immediately preceding a possible token, possibly denoting that
     * the token should instead be interpreted as a literal.
     */
    private static final int ESCAPE_CHAR_GROUP = 2;

    /**
     * The index of the capturing group within tokenPattern which matches the
     * entire token, including the leading "${" and terminating "}" strings.
     */
    private static final int TOKEN_GROUP = 3;

    /**
     * The index of the capturing group within tokenPattern which matches only
     * the token name contained within the "${" and "}" strings.
     */
    private static final int TOKEN_NAME_GROUP = 4;
    
    /**
     * The values of all known tokens.
     */
    private final Map<String, String> tokenValues = new HashMap<String, String>();

    /**
     * Sets the token having the given name to the given value. Any existing
     * value for that token is replaced.
     *
     * @param name
     *     The name of the token to set.
     *
     * @param value
     *     The value to set the token to.
     */
    public void setToken(String name, String value) {
        tokenValues.put(name, value);
    }

    /**
     * Returns the value of the token with the given name, or null if no such
     * token has been set.
     *
     * @param name
     *     The name of the token to return.
     * 
     * @return
     *     The value of the token with the given name, or null if no such
     *     token exists.
     */
    public String getToken(String name) {
        return tokenValues.get(name);
    }

    /**
     * Removes the value of the token with the given name. If no such token
     * exists, this function has no effect.
     *
     * @param name
     *     The name of the token whose value should be removed.
     */
    public void unsetToken(String name) {
        tokenValues.remove(name);
    }

    /**
     * Returns a map of all tokens, with each key being a token name, and each
     * value being the corresponding token value. Changes to this map will
     * directly affect the tokens associated with this filter.
     *
     * @return
     *     A map of all token names and their corresponding values.
     */
    public Map<String, String> getTokens() {
        return tokenValues;
    }

    /**
     * Replaces all current token values with the contents of the given map,
     * where each map key represents a token name, and each map value
     * represents a token value.
     *
     * @param tokens
     *     A map containing the token names and corresponding values to
     *     assign.
     */
    public void setTokens(Map<String, String> tokens) {
        tokenValues.clear();
        tokenValues.putAll(tokens);
    }
    
    /**
     * Filters the given string, replacing any tokens with their corresponding
     * values.
     *
     * @param input
     *     The string to filter.
     *
     * @return
     *     A copy of the input string, with any tokens replaced with their
     *     corresponding values.
     */
    public String filter(String input) {

        StringBuilder output = new StringBuilder();
        Matcher tokenMatcher = tokenPattern.matcher(input);

        // Track last regex match
        int endOfLastMatch = 0;

        // For each possible token
        while (tokenMatcher.find()) {

            // Pull possible leading text and first char before possible token
            String literal = tokenMatcher.group(LEADING_TEXT_GROUP);
            String escape = tokenMatcher.group(ESCAPE_CHAR_GROUP);

            // Append leading non-token text
            output.append(literal);

            // If char before token is '$', the token itself is escaped
            if ("$".equals(escape)) {
                String notToken = tokenMatcher.group(TOKEN_GROUP);
                output.append(notToken);
            }

            // If char is not '$', interpret as a token
            else {

                // The char before the token, if any, is a literal
                output.append(escape);

                // Pull token value
                String tokenName = tokenMatcher.group(TOKEN_NAME_GROUP);
                String tokenValue = getToken(tokenName);

                // If token is unknown, interpret as literal
                if (tokenValue == null) {
                    String notToken = tokenMatcher.group(TOKEN_GROUP);
                    output.append(notToken);
                }

                // Otherwise, substitute value
                else
                    output.append(tokenValue);

            }

            // Update last regex match
            endOfLastMatch = tokenMatcher.end();
            
        }

        // Append any remaining non-token text
        output.append(input.substring(endOfLastMatch));
        
        return output.toString();
       
    }

    /**
     * Given an arbitrary map containing String values, replace each non-null
     * value with the corresponding filtered value.
     *
     * @param map
     *     The map whose values should be filtered.
     */
    public void filterValues(Map<?, String> map) {

        // For each map entry
        for (Map.Entry<?, String> entry : map.entrySet()) {

            // If value is non-null, filter value through this TokenFilter
            String value = entry.getValue();
            if (value != null)
                entry.setValue(filter(value));
            
        }
        
    }

    /**
     * Given an arbitrary map containing string values, return a map that contains
     * the parameter names and an array of instances in those parameters where
     * prompts should occur.
     *
     * @param parameters
     *     The parameters to search for prompts.
     *
     * @return
     *     A map where the key is the parameter name and the value is an array
     *     of 0-indexed instances in the parameter that need to be prompted.
     */
    public static Map<String, List<Integer>> getPrompts(Map<?, String> parameters) {

        Map<String, List<Integer>> prompts = new HashMap<String, List<Integer>>();
        final String fullPromptString = "${" + PromptTokens.PROMPT_TOKEN_STRING + "}";

        // Loop through each parameter entry
        for (Map.Entry<?, String> entry : parameters.entrySet()) {

            String key = entry.getKey().toString();
            String value = entry.getValue();

            // If the entire parameter value equals one of the tokens,
            // add it to the list with the -1 sentinel and go to next
            // entry.
            if (value.equals(PromptTokens.PROMPT_TOKEN_NUMERIC) ||
                value.equals(fullPromptString)) {

                prompts.put(key, Collections.singletonList(-1));
                continue;

            }

            Matcher tokenMatcher = tokenPattern.matcher(value);

            // For each possible token
            while (tokenMatcher.find()) {

                // Pull possible leading text and first char before possible token
                String literal = tokenMatcher.group(LEADING_TEXT_GROUP);
                String escape = tokenMatcher.group(ESCAPE_CHAR_GROUP);

                // If char before token is '$', the token itself is escaped
                if ("$".equals(escape)) {
                    String notToken = tokenMatcher.group(TOKEN_GROUP);
                    continue;
                }

                // If char is not '$', interpret as a token
                else {

                    // Pull token value
                    String tokenName = tokenMatcher.group(TOKEN_NAME_GROUP);
                    if (tokenName.equals(PromptTokens.PROMPT_TOKEN_STRING)) {
                        List<Integer> paramList = prompts.get(key);

                        // If no current list exists, create it and upate the map.
                        if (paramList == null) {
                            paramList = new ArrayList<Integer>();
                            paramList.add(0);
                            prompts.put(key, paramList);
                        }

                        // Add the new position to the list.
                        else
                            paramList.add(paramList.size());    
                    }

                }

            }

        }

        return prompts;

    }

}
