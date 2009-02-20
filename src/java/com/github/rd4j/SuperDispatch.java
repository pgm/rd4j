package com.github.rd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.github.rd4j.analysis.MethodParameter;
import com.github.rd4j.form.FormBinder;
import com.github.rd4j.form.types.Rd4jType;
import com.github.rd4j.form.types.Types;

public class SuperDispatch extends HttpServlet {

	private static final long serialVersionUID = -954607108295938014L;
	private static final Logger log = Logger.getLogger(SuperDispatch.class);
	protected ServletContext servletContext;
	
	protected Dispatcher dispatcher;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response, "GET");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doRequest(request, response, "POST");
	}

	private List<RequestInterceptor> prebindingInterceptors = new ArrayList<RequestInterceptor>();
	
	protected void doRequest(HttpServletRequest request, HttpServletResponse response, String method)
	throws ServletException, IOException {
		Resolution resolution = null;

		// find the path within the this context
		String relativeToContext = getPathRelativeToContext(request);
	
		// now map that to a method (bound to an instance of some object)
		RequestContext bound = dispatcher.getMethod(relativeToContext, request); 

		// fire any interceptors now that we know what the request target it
		for(RequestInterceptor interceptor : prebindingInterceptors) {
			resolution = interceptor.intercept(bound);
			if(resolution != null)
				break;
		}

		if(resolution == null) {
			// construct an argument list based on the request for that method
			Object args[] = buildArgumentsFromRequest(bound);
		
			// execute the method to get a resolution
			resolution = bound.getHandler().invoke(args);
		}
		
		// and execute that resolution
		resolution.go(servletContext, response);
	}

	public void addPrebindInterceptor(RequestInterceptor interceptor) {
		this.prebindingInterceptors.add(interceptor);
	}

	protected String getPathRelativeToContext(HttpServletRequest request) {
		String relativeToContext = request.getRequestURI().substring(request.getContextPath().length());
		return relativeToContext;
	}

	static class DuplicatedParameterException extends RuntimeException {
		String parameterName;
		
		public DuplicatedParameterException(String parameterName) {
			super("The parameter "+parameterName+" appeared multiple times on the request");
			this.parameterName = parameterName;
		}
	}
	
	/**
	 * Given a list of parameters, and a request object, construct values for each parameter
	 * 
	 * @param parameters
	 * @param request
	 * @return An array of parameters that can be passed to the method
	 */
	protected Object[] buildArgumentsFromRequest(RequestContext requestContext) {
		MethodParameter[] parameters = requestContext.getHandler().getParameters();
		
		// build a TypedMap with the parameters for the method
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		for(MethodParameter mp : parameters) {
			types.put(mp.getName(), Types.coerceToRd4jType(mp.getType()));
		}
 		
		// now perform the conversion from strings to domain types
		// construct an error collection to collect any errors that might arise
		// from data conversion or validation
		ErrorCollection errorCollection = new ErrorCollection();

		// use the names of the parameters and the type information in map
		// to populate that map
		Map map = new HashMap<String, Object>();
		FormBinder.bind(map, types, requestContext.getParameters(), errorCollection);
		
		// add the request context specially because this varies from
		// request to request
		if(types.containsKey("requestContext")) {
			map.put("requestContext", requestContext);
		}

		if(requestContext.getUrlBinding() != null) {
			// Now, add any static parameters (clobbering those that may have come on the url)
			for(Entry<String,Object>entry : requestContext.getUrlBinding().staticBindings.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		
		// unpack the map into an array of parameters
		Object args[] = new Object[parameters.length];
		for(int i = 0;i<parameters.length; i++) {
			String parameterName = parameters[i].getName();

			args[i] = map.get(parameterName);
		}

		// and return those parameters
		return args;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		servletContext = config.getServletContext();
		
		String dispatcherClassName = config.getInitParameter("dispatcherClassName");
		
		Class<?> dispatchClass;
		try {
			dispatchClass = Thread.currentThread().getContextClassLoader().loadClass(dispatcherClassName);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	
		try {
			dispatcher = (Dispatcher)dispatchClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		dispatcher.init(this, config);
	}
}
