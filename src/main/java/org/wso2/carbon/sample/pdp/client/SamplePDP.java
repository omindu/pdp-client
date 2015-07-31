package org.wso2.carbon.sample.pdp.client;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceException;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceStub;

import java.io.File;
import java.rmi.RemoteException;


public class SamplePDP {


    private static String SERVER_URL = "https://localhost:9443";
    private static String USER_NAME = "admin";
    private static String PASSWORD = "admin";
    private static String TRUST_STORE_PATH = "resources" + File.separator + "wso2carbon.jks";
    private static String TRUST_STORE_PASSWORD = "wso2carbon";
    private static String TRUST_STORE_TYPE = "JKS";

    private static Log log = LogFactory.getLog(SamplePDP.class);

    public static void main(String[] args) {

        /**
         * Call to https://localhost:9443/services/ uses HTTPS protocol.
         * Therefore we have to validate the Identity Server certificate. The server certificate is looked up in the
         * trust store. Following code sets what trust-store to look for and its JKs password.
         */
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", TRUST_STORE_TYPE);


        try {

            /**
             * Create a configuration context. A configuration context contains information for axis2 environment.
             * This is needed to create an axis2 client.
             */
            ConfigurationContext configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

            EntitlementServiceStub entitlementServiceStub = new EntitlementServiceStub(configurationContext, SERVER_URL + "/services/EntitlementService");
            ServiceClient client = entitlementServiceStub._getServiceClient();
            Options options = client.getOptions();

            /**
             * Setting basic auth headers to authenticate the admin service call
             */
            HttpTransportProperties.Authenticator authenticator = new HttpTransportProperties.Authenticator();
            authenticator.setUsername(USER_NAME);
            authenticator.setPassword(PASSWORD);
            authenticator.setPreemptiveAuthentication(true);
            options.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
            options.setManageSession(true);

            String sampleRequest = "<Request xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" CombinedDecision=\"false\" " +
                    "ReturnPolicyIdList=\"false\">\n" +
                    "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">\n" +
                    "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" IncludeInResult=\"false\">\n" +
                    "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">read</AttributeValue>\n" +
                    "</Attribute>\n" +
                    "</Attributes>\n" +
                    "<Attributes Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">\n" +
                    "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" IncludeInResult=\"false\">\n" +
                    "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">admin</AttributeValue>\n" +
                    "</Attribute>\n" +
                    "</Attributes>\n" +
                    "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\">\n" +
                    "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:environment:environment-id\" IncludeInResult=\"false\">\n" +
                    "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">*</AttributeValue>\n" +
                    "</Attribute>\n" +
                    "</Attributes>\n" +
                    "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
                    "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" IncludeInResult=\"false\">\n" +
                    "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">TestResource</AttributeValue>\n" +
                    "</Attribute>\n" +
                    "</Attributes>\n" +
                    "</Request>";

            /**
             * Call to getDecision() operation of EntitlementService
             * This returns a XACML decision for a given request.
             */
            String decision = entitlementServiceStub.getDecision(sampleRequest);

            System.out.println(decision);

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (EntitlementServiceException e) {
            e.printStackTrace();
        }

    }

}
