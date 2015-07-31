package org.wso2.carbon.sample.pdp.client;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceException;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceStub;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Properties;


public class SamplePDPClient {


    private static String serverURL;
    private static String userName;
    private static String password;
    private static String trustStorePath;
    private static String trustStorePassword;
    private static String xacmlRequestFilePath;


    private static void loadConfigurations() {

        Properties properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(ClientConstants.PROPERTIES_FILE_PATH);
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println("Error reading configurations from file");
            e.printStackTrace();
            System.exit(0);
        }

        serverURL = properties.getProperty(ClientConstants.SERVER_URL);
        userName = properties.getProperty(ClientConstants.USER_NAME);
        password = properties.getProperty(ClientConstants.PASSWORD);
        trustStorePath = properties.getProperty(ClientConstants.TRUST_STORE_PATH);
        trustStorePassword = properties.getProperty(ClientConstants.TRUST_STORE_PASSWORD);
        xacmlRequestFilePath = properties.getProperty(ClientConstants.XACML_REQUEST_FILE_PATH);

    }

    public static void main(String[] args)  {

        loadConfigurations();

        /**
         * Call to https://localhost:9443/services/ uses HTTPS protocol.
         * Therefore we have to validate the Identity Server certificate. The server certificate is looked up in the
         * trust store. Following code sets what trust-store to look for and its JKs password.
         */
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        try {

            /**
             * Create a configuration context. A configuration context contains information for axis2 environment.
             * This is needed to create an axis2 client.
             */
            ConfigurationContext configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

            EntitlementServiceStub entitlementServiceStub = new EntitlementServiceStub(configurationContext, serverURL + "/services/EntitlementService");
            ServiceClient client = entitlementServiceStub._getServiceClient();
            Options options = client.getOptions();

            /**
             * Setting basic auth headers to authenticate the admin service call
             */
            HttpTransportProperties.Authenticator authenticator = new HttpTransportProperties.Authenticator();
            authenticator.setUsername(userName);
            authenticator.setPassword(password);
            authenticator.setPreemptiveAuthentication(true);
            options.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
            options.setManageSession(true);

            String sampleRequest = readRequestFromFile(xacmlRequestFilePath);

            /**
             * Call to getDecision() operation of EntitlementService
             * This returns a XACML decision for a given request.
             */
            String decision = entitlementServiceStub.getDecision(sampleRequest);

            System.out.println(decision);

        } catch (AxisFault e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (EntitlementServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readRequestFromFile(String path) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }


}
