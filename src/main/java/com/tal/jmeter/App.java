package com.tal.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.zeromq.ZMQ;

public class App {
	public static void main(String[] args) {

		App.test1();
		
	}
	
public static void test1() {

		
		ZMQSampler2 sampler= new ZMQSampler2();
		JavaSamplerContext context=new JavaSamplerContext(new Arguments());
		sampler.runTest(context);
}
		

	public static void test() {
		
		
		String endpoint = "tcp://{server}:{port}"; //your endpoint here
		
		//
		String message = "";//Your request body here - Example: "{\"Service\":\"/{request path}\",\"RequestId\":\"0\",\"Payload\":\"{\\\"userName\\\":\\\"user@name.com\\\",\\\"password\\\":\\\"password\\\",\\\"clientIP\\\":\\\"79.178.222.222\\\",\\\"clientId\\\":7}\",\"EndPoint\":\"tcp://{server}:{port}\"}";
		ExecuteRequest(message, endpoint);

		ZMQ.Context context = ZMQ.context(1);

		// Socket to talk to server
		System.out.println("Connecting to hello world server…");

		ZMQ.Socket requester = context.socket(ZMQ.DEALER);
		requester.connect(endpoint);

		for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
			String request = "Hello";
			System.out.println("Sending Hello " + requestNbr);
			requester.send(message.getBytes(), 0);

			byte[] reply = requester.recv(0);
			System.out.println("Received " + new String(reply) + " "
					+ requestNbr);
		}

		requester.close();
		context.term();
	}

	public static boolean ExecuteRequest(String message, String endpoint) {
		ZMQ.Context context = ZMQ.context(1);//int is number of threads to use
		ZMQ.Socket requester = context.socket(ZMQ.DEALER);
		requester.connect(endpoint);
		requester.send(message.getBytes(), 0);
		byte[] reply = requester.recv(0);
		System.out.println(reply);
		requester.close();
		context.term();
		return true;

	}
}
