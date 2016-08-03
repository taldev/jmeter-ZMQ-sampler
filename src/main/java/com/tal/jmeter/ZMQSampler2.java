package com.tal.jmeter;

import java.io.Serializable;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.zeromq.ZMQ;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class ZMQSampler2 extends AbstractJavaSamplerClient {

    private static final Logger log = LoggingManager.getLoggerForClass();

    /**
    * Parameter for setting the socket type for example, "DEALER,REQ,RES".
    */
    private static final String PARAMETER_ZMQ_SOCKET_TYPE = "ZMQ_SOCKET_TYPE";

    /**
    * Parameter for setting the socket endpoint for example,
    * "tcp://192.168.10.10:5557".
    */
    private static final String PARAMETER_ZMQ_ENDPOINT = "ZMQ_ENDPOINT";

    /**
    * Parameter for setting the ZMQ message for example, " "{\
    * "Service\":\"/frontend/auth/login\",\"RequestId\":\"0\",\"Payload\":\"{\\\"userName\\\":\\\"johndoe\\\",\\\"password\\\":\\\"123123\\\",\\\"storeId\\\":7}\"}"
    * ".
    */
    private static final String PARAMETER_ZMQ_MESSAGE = "ZMQ_MESSAGE";

    private ZMQ.Socket requester = null;
    private ZMQ.Context ctxt = null;

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
                    Arguments defaultParameters = new Arguments();
                    defaultParameters.addArgument(PARAMETER_ZMQ_SOCKET_TYPE, "${ZMQ_SOCKET_TYPE}");
                    defaultParameters.addArgument(PARAMETER_ZMQ_ENDPOINT, "${ZMQ_ENDPOINT}");
                    defaultParameters.addArgument(PARAMETER_ZMQ_MESSAGE, "${ZMQ_MESSAGE}");
                    return defaultParameters;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
                    if (null == ctxt) {
                                    log.debug(new String("ctxt not null"));
                                    ctxt = ZMQ.context(1);
                    }
    }

    public SampleResult runTest(JavaSamplerContext context) {

                    SampleResult result = new SampleResult();
                    String endpoint = context.getParameter(PARAMETER_ZMQ_ENDPOINT);
                    String message = context.getParameter(PARAMETER_ZMQ_MESSAGE);
                    String responseData = "";
                    
                    
                    //create desired request string
                    String requestData = new String("Request Endpoint :\n\n" + endpoint + "\n\nPayload :\n\n" + message);

                    try {
                                    sampleResultStart(result, requestData); // start test and stop watch

                                    if (requester == null) {
                                                    requester = ctxt.socket(ZMQ.DEALER); //can be changed to passed as parameter 
                                                    requester.connect(endpoint);
                                    }

                                    requester.send(message.getBytes(), 0);
                                    byte[] reply = requester.recv(0);

                                    responseData = new String(reply);

                                    result.setSuccessful(true);
                                    result.setResponseMessage("Successfully performed ZMQ request");
                                    result.setResponseCodeOK(); // 200 code

                    } catch (Exception e) {

                                    result.setSuccessful(false);
                                    result.setResponseMessage("Exception: " + e);

                                    // get stack trace as a String to return as document data
                                    java.io.StringWriter stringWriter = new java.io.StringWriter();
                                    e.printStackTrace(new java.io.PrintWriter(stringWriter));

                                    result.setDataType(SampleResult.TEXT);
                                    result.setResponseCode("500");
                    } finally {
                                    result.sampleEnd(); // stop stopwatch
                                    result.setResponseData(responseData, null);
                    }
                    return result;
    }

    private void sampleResultStart(SampleResult result, String data) {
                    result.setSamplerData(data);
                    result.sampleStart();
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
                    try {
                                    if (requester != null) {
                                                    requester.close();
                                                    requester = null;
                                    }
                                    if (null != ctxt) {
                                                    ctxt.close();
                                                    ctxt = null;
                                    }
                    } catch (Exception e) {

                    }
    }
}
