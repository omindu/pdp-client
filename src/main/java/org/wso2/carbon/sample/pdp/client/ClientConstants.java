package org.wso2.carbon.sample.pdp.client;

import java.io.File;

public class ClientConstants {

    public static final String RESOURCE_PATH = System.getProperty("user.dir") + File.separator + "resources" + File.separator;
    public static final String PROPERTIES_FILE_PATH = RESOURCE_PATH + "client.properties";

    public static final String SERVER_URL = "server.url";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";

    public static final String TRUST_STORE_PATH = "trust.store.path" ;
    public static final String TRUST_STORE_PASSWORD = "trust.store.password";

    public static final String XACML_REQUEST_FILE_PATH = "xacml.request.file.path";
}
