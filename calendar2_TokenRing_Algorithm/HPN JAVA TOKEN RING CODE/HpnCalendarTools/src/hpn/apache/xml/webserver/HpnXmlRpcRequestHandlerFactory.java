/**
 * Simple XMLRPC request handler factory - this little magic class allows
 * for a request INSTANCE to be set as the handler for the xmlrpc server.
 * Ideally, a client shouldn't even NEED to know what this is doing.
 * http://www.zugiart.com/2010/09/java-apache-simple-xmlrpc-server/
 */

package hpn.apache.xml.webserver;

import java.util.*;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory;


public class HpnXmlRpcRequestHandlerFactory implements RequestProcessorFactoryFactory, RequestProcessorFactory
{
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	
	public void setHandler(String name, Object handler) 
	{ 
		this.handlerMap.put(name, handler); 
	}
	
	public Object getHandler(String name) 
	{ 
		return this.handlerMap.get(name); 
	}
 
	public RequestProcessorFactory getRequestProcessorFactory(Class arg0) throws XmlRpcException 
	{ 
		return this; 
	}
	
	@Override
	public Object getRequestProcessor(XmlRpcRequest request) throws XmlRpcException
	{
		String handlerName = request.getMethodName().substring(0,request.getMethodName().lastIndexOf("."));
		if( !handlerMap.containsKey(handlerName)) throw new XmlRpcException("Unknown handler: "+handlerName);
		return handlerMap.get(handlerName);
	}
}