/**
 * The purpose of this class is to allow Java programmers to very quickly craft a stand alone, 
 * simple XMLRPC server using the Apache xml-rpc library. 
 * No servlet or container required.
 * A simple implementation of XML-RPC server for apache ws-xmlrpc
 * support single instance handler object, but not yet multi-threaded
 *  http://www.zugiart.com/2010/09/java-apache-simple-xmlrpc-server/
 */

package hpn.apache.xml.webserver;


/**
 * A handler mapping based on a property file. 
 * The property file contains a set of properties. 
 * The property key is taken as the handler name. 
 * The property value is taken as the name of a class being instantiated. 
 * For any non-void, non-static, and public method in the class, an entry in the handler map is generated. 
 * A typical use would be, to specify interface names as the property keys and implementations as the values.
 */
import java.net.InetAddress;

import org.apache.xmlrpc.server.PropertyHandlerMapping;


/**
 * First of all, there is an object, called the XmlRpcServer. 
 * This objects purpose is to receive and execute XML-RPC calls by the clients.
 */
import org.apache.xmlrpc.server.XmlRpcServer;


/**
 *Like the XmlRpcClient, the XmlRpcServer needs a configuration, 
 *which is given by the XmlRpcServerConfigImpl object.
 */
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;


/**
 * 
 // public class WebServer
 // extends java.lang.Object
 // implements java.lang.Runnable
 * 
 * The WebServer is a minimal HTTP server, that might be used as an embedded web server.
 * Use of the WebServer has grown very popular amongst users of Apache XML-RPC. 
 * Why this is the case, can hardly be explained, because the WebServer is at best a workaround, 
 * compared to full blown servlet engines like Tomcat or Jetty.
 * 
 * For example, under heavy load it will almost definitely be slower than a real servlet engine, 
 * because it does neither support proper keepalive (multiple requests per physical connection) 
 * nor chunked mode (in other words, it cannot stream requests).
 *
 *If you still insist in using the WebServer, it is recommended to use its subclass, 
 *the ServletWebServer instead, which offers a minimal subset of the servlet API. 
 *In other words, you keep yourself the option to migrate to a real servlet engine later.
 *
 *Use of the WebServer goes roughly like this: First of all, create a property file (for example "MyHandlers.properties") 
 *and add it to your jar file. 
 *The property keys are handler names and the property values are the handler classes. Once that is done, create an instance of WebServer:
 *
 *****************************************************************
 //  final int port = 8088;
 //  final String propertyFile = "MyHandler.properties";
 //
 //  PropertyHandlerMapping mapping = new PropertyHandlerMapping();
 //  ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //  mapping.load(cl, propertyFile);
 //  WebServer webServer = new WebServer(port);
 //  XmlRpcServerConfigImpl config = new XmlRpcServerConfigImpl();
 //  XmlRpcServer server = webServer.getXmlRpcServer();
 //  server.setConfig(config);
 //  server.setHandlerMapping(mapping);
 //  webServer.start();
 *****************************************************************
 */
import org.apache.xmlrpc.webserver.WebServer;
 

public class HpnXmlRpcServer
{
	/**
	 * runtime attributes
	 */
 
	private int port;
	private InetAddress ipv4;
	private HpnXmlRpcRequestHandlerFactory handler = null; //The class 'SimpleXmlRpcRequestHandlerFactory' has defined in separated file.
	private PropertyHandlerMapping phm = null;
	private XmlRpcServer xmlRpcServer = null;
	private WebServer webServer = null;
	
	/**
	 * constructor
	 */
 
	/**
	 * Creates an instance of xmlrpc server, using :handler as the class that
	 * will handle all request. Note that a new instance of :handler will be
	 * created at every xml-rpc request.
	 *
	 * @param name
	 * @param port
	 * @param handler
	 * @throws Exception
	 */
	public HpnXmlRpcServer(int port,String ipv4) throws Exception
	{
		this.port = port;
		this.ipv4 = InetAddress.getByName(ipv4); 
		// bind
		this.webServer = new WebServer(this.port, this.ipv4); 
		this.xmlRpcServer = this.webServer.getXmlRpcServer();
		this.handler = new HpnXmlRpcRequestHandlerFactory();
 
		this.phm = new PropertyHandlerMapping();
		this.phm.setRequestProcessorFactoryFactory(this.handler);
	}
 
	/**
	 * services 
	 */


	/**
	 * Adds a handler instance (which CAN be stateful) for each request. Note that
	 * every public method in this object will be callable via xml-rpc client
	 *
	 * @param name - handler name
	 * @param requestHandler - handler obj instance
	 * @throws Exception
	 */
	public void addHandler(String name, Object requestHandler) throws Exception
	{
		this.handler.setHandler(name, requestHandler);
		this.phm.addHandler(name, requestHandler.getClass());
	}
 
	/**
	 * Start the xml-rpc server forever. 
	 * In the rare case of fatal exception, the web server will be restarted automatically. 
	 * This is a blocking call (not thread-based).
	 */
	public void startServing() throws Exception
	{
		// initializing the server serving
		this.xmlRpcServer.setHandlerMapping(this.phm);
		XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) this.xmlRpcServer.getConfig();
		serverConfig.setEnabledForExtensions(true);
		this.webServer.start();
		ServerStatus.initServerStatus(this);
		ServerStatus.setServerStatus(true);
	}
	public void signOn() throws Exception
	{
		//this.webServer.start();
		ServerStatus.setServerStatus(true);
	}
	public void signOff() throws Exception
	{
		//this.webServer.shutdown(); //it has a problem in C# xmlrpc library so we changed the program in a new way to support signoff
		ServerStatus.setServerStatus(false);
	}
	/**
	 * getter/setter 
	 */
 
	public int getPort() 
	{ 
		return this.port; 
	}
	public String getIpv4() {
		return ipv4.getHostAddress();
	}
	public WebServer getWebServer() 
	{ 
		return this.webServer; 
	}
	public void setWebServer(WebServer webServer) 
	{ 
		this.webServer = webServer; 
	}
	
	public PropertyHandlerMapping getPhm() 
	{ 
		return this.phm; 
	}
	public void setPhm(PropertyHandlerMapping phm) 
	{ 
		this.phm = phm; 
	}
	
	public HpnXmlRpcRequestHandlerFactory getHandler() 
	{ 
		return this.handler; 
	}
	public void setHandler(HpnXmlRpcRequestHandlerFactory handler) 
	{ 
		this.handler = handler; 
	}
	
	public XmlRpcServer getXmlRpcServer() 
	{ 
		return this.xmlRpcServer; 
	}
	public void setXmlRpcServer(XmlRpcServer xmlRpcServer) 
	{ 
		this.xmlRpcServer = xmlRpcServer; 
	} 
 
}
