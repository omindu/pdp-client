# pdp-client
Sample PDP client for WSO2 Identity Server
## Running the sample
 * Publish the policy given in `resources/policy/TestPolicy.xml` to IS PDP
 * Change the configurations accorginly in `resources/client.properties`
 * Run the client using 
```mvn
 mvn exec:java -Dexec.mainClass="org.wso2.carbon.sample.pdp.client.SamplePDPClient" -Dexec.classpathScope=runtime
```
 * XACML request can be changed by editing `resources/request/Sample.xml`

