/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.spi.AuthenticationProvider;

/**
 * A map with convenience methods to set session parameters.
 * 
 * @see SessionParameter
 * @see SessionFactory
 */
public class SessionParameterMap extends LinkedHashMap<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty map.
     */
    public SessionParameterMap() {
        super();
    }

    /**
     * Sets an integer value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * 
     * @return the previous value for this key or {@code null}
     */
    public String put(String key, long value) {
        return put(key, Long.toString(value));
    }

    /**
     * Sets a boolean value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * 
     * @return the previous value for this key or {@code null}
     */
    public String put(String key, boolean value) {
        return put(key, value ? "true" : "false");
    }

    /**
     * Sets the AtomPub URL and sets the binding to AtomPub.
     * 
     * @param url
     *            the AtomPub binding URL
     */
    public void setAtomPubBindingUrl(String url) {
        if (url == null) {
            remove(SessionParameter.BINDING_TYPE);
            remove(SessionParameter.ATOMPUB_URL);
        } else {
            put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            put(SessionParameter.ATOMPUB_URL, url);
        }
    }

    /**
     * Sets the Web Services WSDL URL and sets the binding to Web Services.
     * Assumes that all CMIS services have the same WSDL URL.
     * 
     * @param url
     *            the Web Services WSDL URL
     */
    public void setWebServicesBindingUrl(String url) {
        if (url == null) {
            remove(SessionParameter.BINDING_TYPE);
            remove(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE);
            remove(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE);
            remove(SessionParameter.WEBSERVICES_OBJECT_SERVICE);
            remove(SessionParameter.WEBSERVICES_VERSIONING_SERVICE);
            remove(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE);
            remove(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE);
            remove(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE);
            remove(SessionParameter.WEBSERVICES_ACL_SERVICE);
            remove(SessionParameter.WEBSERVICES_POLICY_SERVICE);
        } else {
            put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
            put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, url);
            put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, url);
            put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, url);
            put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, url);
            put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, url);
            put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, url);
            put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, url);
            put(SessionParameter.WEBSERVICES_ACL_SERVICE, url);
            put(SessionParameter.WEBSERVICES_POLICY_SERVICE, url);
        }
    }

    /**
     * Sets the Web Service memory threshold.
     * 
     * @param threshold
     *            the threshold in bytes
     */
    public void setWebServicesMemoryThreshold(long threshold) {
        put(SessionParameter.WEBSERVICES_MEMORY_THRESHOLD, threshold);
    }

    /**
     * Sets the Browser URL and sets the binding to Browser.
     * 
     * @param url
     *            the Browser binding URL
     */
    public void setBrowserBindingUrl(String url) {
        if (url == null) {
            remove(SessionParameter.BINDING_TYPE);
            remove(SessionParameter.BROWSER_URL);
        } else {
            put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
            put(SessionParameter.BROWSER_URL, url);
        }
    }

    /**
     * Sets whether properties should be sent in the succinct format or not.
     * 
     * @param succinct
     *            {@code true} if properties should be sent in the succinct
     *            format, {@code false} otherwise
     */
    public void setBrowserBindingSuccinct(boolean succinct) {
        put(SessionParameter.BROWSER_SUCCINCT, succinct);
    }

    /**
     * Sets the local service factory and sets the binding to Local.
     * 
     * @param serviceFactoryClass
     *            the local service factory class
     */
    public void setLocalBindingClass(Class<? extends SessionFactory> serviceFactoryClass) {
        if (serviceFactoryClass == null) {
            remove(SessionParameter.BINDING_TYPE);
            remove(SessionParameter.LOCAL_FACTORY);
        } else {
            put(SessionParameter.BINDING_TYPE, BindingType.LOCAL.value());
            put(SessionParameter.LOCAL_FACTORY, serviceFactoryClass.getName());
        }
    }

    /**
     * Sets the repository id.
     * 
     * @param repositoryId
     *            the repository id
     */
    public void setRepositoryId(String repositoryId) {
        if (repositoryId == null) {
            remove(SessionParameter.REPOSITORY_ID);
        } else {
            put(SessionParameter.REPOSITORY_ID, repositoryId);
        }
    }

    /**
     * Sets user and password.
     * 
     * @param user
     *            the user
     * @param password
     *            the password
     */
    public void setUserAndPassword(String user, String password) {
        if (user == null) {
            remove(SessionParameter.USER);
            remove(SessionParameter.PASSWORD);
        } else {
            put(SessionParameter.USER, user);
            put(SessionParameter.PASSWORD, password);
        }
    }

    /**
     * Turns all authentication off if the standard authentication provider is
     * used.
     */
    public void setNoAuthentication() {
        put(SessionParameter.AUTH_HTTP_BASIC, false);
        put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, false);
    }

    /**
     * Turns basic authentication on and UsernameToken authentication off if the
     * standard authentication provider is used.
     * 
     * @param user
     *            the user
     * 
     * @param password
     *            the password
     */
    public void setBasicAuthentication(String user, String password) {
        if (user == null) {
            throw new IllegalArgumentException("User must be set!");
        }

        setUserAndPassword(user, password);

        put(SessionParameter.AUTH_HTTP_BASIC, true);
        put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, false);
    }

    /**
     * Turns UsernameToken authentication on for the Web Services binding if the
     * standard authentication provider is used.
     * 
     * @param user
     *            the user
     * @param password
     *            the password
     * @param basicAuth
     *            {@code true} if basic authentication should be used in
     *            addition to the UsernameToken authentication (required by some
     *            servers), {@code false} otherwise
     */
    public void setUsernameTokenAuthentication(String user, String password, boolean basicAuth) {
        if (user == null) {
            throw new IllegalArgumentException("User must be set!");
        }

        setUserAndPassword(user, password);

        put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, true);
        put(SessionParameter.AUTH_HTTP_BASIC, basicAuth);
    }

    /**
     * Turns NTLM authentication on and basic authentication and UsernameToken
     * authentication off.
     * <p>
     * <em>Works only in single user environments and only with NTLMv1!</em>
     * 
     * @param user
     *            the user
     * @param password
     *            the password
     */
    public void setNtlmAuthentication(String user, String password) {
        if (user == null) {
            throw new IllegalArgumentException("User must be set!");
        }

        setUserAndPassword(user, password);

        put(SessionParameter.AUTH_HTTP_BASIC, false);
        put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, false);
        put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
                "org.apache.chemistry.opencmis.client.bindings.spi.NTLMAuthenticationProvider");
    }

    /**
     * Sets the locale of the session.
     * 
     * @param locale
     *            the locale
     */
    public void setLocale(Locale locale) {
        if (locale == null) {
            remove(SessionParameter.LOCALE_ISO639_LANGUAGE);
            remove(SessionParameter.LOCALE_ISO3166_COUNTRY);
            remove(SessionParameter.LOCALE_VARIANT);
        } else {
            if (locale.getLanguage().length() == 0) {
                remove(SessionParameter.LOCALE_ISO639_LANGUAGE);
            } else {
                put(SessionParameter.LOCALE_ISO639_LANGUAGE, locale.getLanguage());
            }
            if (locale.getCountry().length() == 0) {
                remove(SessionParameter.LOCALE_ISO3166_COUNTRY);
            } else {
                put(SessionParameter.LOCALE_ISO3166_COUNTRY, locale.getCountry());
            }
            if (locale.getVariant().length() == 0) {
                remove(SessionParameter.LOCALE_VARIANT);
            } else {
                put(SessionParameter.LOCALE_VARIANT, locale.getVariant());
            }
        }
    }

    /**
     * Sets the locale of the session.
     * 
     * @param language
     *            ISO 639 language code
     * @param country
     *            ISO 3166 country code
     */
    public void setLocale(String language, String country) {
        setLocale(new Locale(language, country));
    }

    /**
     * Sets the locale of the session.
     * 
     * @param language
     *            ISO 639 language code
     */
    public void setLocale(String language) {
        setLocale(new Locale(language));
    }

    /**
     * Sets whether cookies should be managed or not.
     * 
     * @param cookies
     *            {@code true} if cookies should be managed, {@code false}
     *            otherwise
     */
    public void setCookies(boolean cookies) {
        put(SessionParameter.COOKIES, cookies);
    }

    /**
     * Sets if the server should be asked to use compression.
     * 
     * @param compression
     *            {@code true} if the server should be asked to use compression,
     *            {@code false} otherwise
     */
    public void setCompression(boolean compression) {
        put(SessionParameter.COMPRESSION, compression);
    }

    /**
     * Sets whether requests to the server should be compressed or not.
     * 
     * @param compression
     *            {@code true} if requests should be compressed, {@code false}
     *            otherwise
     */
    public void setClientCompression(boolean compression) {
        put(SessionParameter.CLIENT_COMPRESSION, compression);
    }

    /**
     * Sets the HTTP connection timeout.
     * 
     * @param timeout
     *            the connection timeout in milliseconds
     */
    public void setConnectionTimeout(long timeout) {
        put(SessionParameter.CONNECT_TIMEOUT, timeout);
    }

    /**
     * Sets the HTTP read timeout.
     * 
     * @param timeout
     *            the read timeout in milliseconds
     */
    public void setReadTimeout(long timeout) {
        put(SessionParameter.READ_TIMEOUT, timeout);
    }

    /**
     * Sets the HTTP invoker class.
     * 
     * @param httpInvokerClass
     *            the HTTP invoker class
     */
    public void setHttpInvoker(Class<?> httpInvokerClass) {
        if (httpInvokerClass == null) {
            remove(SessionParameter.HTTP_INVOKER_CLASS);
        } else {
            put(SessionParameter.HTTP_INVOKER_CLASS, httpInvokerClass.getName());
        }
    }

    /**
     * Adds a HTTP header.
     * 
     * @param header
     *            the header name
     * @param value
     *            the header value
     */
    public void addHeader(String header, String value) {
        if (header == null || header.trim().length() == 0) {
            return;
        }

        int x = 0;
        while (containsKey(SessionParameter.HEADER + "." + x)) {
            x++;
        }

        put(SessionParameter.HEADER + "." + x, header + ":" + value);
    }

    /**
     * Sets HTTP proxy user and password.
     * 
     * @param user
     *            the user
     * @param password
     *            the password
     */
    public void setProxyUserAndPassword(String user, String password) {
        if (user == null) {
            remove(SessionParameter.PROXY_USER);
            remove(SessionParameter.PROXY_PASSWORD);
        } else {
            put(SessionParameter.PROXY_USER, user);
            put(SessionParameter.PROXY_PASSWORD, password);
        }
    }

    /**
     * Sets the authentication provider class.
     * 
     * @param authenticationProviderClass
     *            the authentication provider class
     */
    public void setAuthenticationProvider(Class<? extends AuthenticationProvider> authenticationProviderClass) {
        if (authenticationProviderClass == null) {
            remove(SessionParameter.AUTHENTICATION_PROVIDER_CLASS);
        } else {
            put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, authenticationProviderClass.getName());
        }
    }

    /**
     * Sets the object factory class.
     * 
     * @param objectFactoryClass
     *            the object factory class
     */
    public void setObjectFactory(Class<? extends ObjectFactory> objectFactoryClass) {
        if (objectFactoryClass == null) {
            remove(SessionParameter.OBJECT_FACTORY_CLASS);
        } else {
            put(SessionParameter.OBJECT_FACTORY_CLASS, objectFactoryClass.getName());
        }
    }

    // --- load and save ---

    /**
     * Loads entries from the given UTF-8 encoded file.
     * 
     * @param file
     *            the file
     * 
     * @see #load(InputStream)
     */
    public void load(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File must be set!");
        }

        FileInputStream stream = new FileInputStream(file);
        try {
            load(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Loads entries from the given, UTF-8 encoded stream and leaves the stream
     * open. Empty lines and lines starting with '#' are ignored. Entries are
     * added and replaced.
     * 
     * @param stream
     *            the stream
     */
    public void load(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Stream must be set!");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));

        String line;
        while ((line = reader.readLine()) != null) {
            putLine(line);
        }
    }

    /**
     * Parses a string that contains key-value pairs.
     * 
     * @param parameters
     *            the parameters string
     */
    public void parse(String parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters string must be set!");
        }

        for (String line : parameters.split("\n")) {
            putLine(line);
        }
    }

    protected void putLine(String line) {
        assert line != null;

        String lineTrim = line.trim();
        if (lineTrim.length() == 0 || lineTrim.charAt(0) == '#') {
            return;
        }

        int sep = lineTrim.indexOf('=');
        if (sep == -1) {
            sep = lineTrim.indexOf(':');
        }

        if (sep == -1) {
            put(lineTrim, null);
        } else {
            put(lineTrim.substring(0, sep).trim(), lineTrim.substring(sep + 1).trim());
        }
    }

    /**
     * Writes all entries to the given file.
     * 
     * @param stream
     *            the stream
     * 
     * @see #store(OutputStream)
     */
    public final void store(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File must be set!");
        }

        FileOutputStream stream = new FileOutputStream(file);
        try {
            store(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Writes all entries UTF-8 encoded to the given stream and leaves the
     * stream open.
     * 
     * @param stream
     *            the stream
     */
    public void store(final OutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Stream must be set!");
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));

        List<String> keys = new ArrayList<String>(keySet());
        Collections.sort(keys);

        for (String key : keys) {
            String value = get(key);
            if (value == null) {
                value = "";
            }

            writer.write(key);
            writer.write('=');
            writer.write(value);
            writer.newLine();
        }

        writer.flush();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('\n');
        }

        return sb.toString();
    }

}
