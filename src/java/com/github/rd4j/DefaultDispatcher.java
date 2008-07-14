package com.github.rd4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import com.github.rd4j.SuperDispatch.DuplicatedParameterException;
import com.github.rd4j.analysis.MethodParameter;
import com.github.rd4j.analysis.ParameterTypeReader;

public abstract class DefaultDispatcher implements Dispatcher {

	public static class UrlBinding {
		final NamedGroupPattern urlRegExp;
		final MethodAndParams methodAndParams;
		final String action;
		final Map<String,Object> staticBindings;
		
		public UrlBinding(String urlRegExp, MethodAndParams methodAndParams, String action, Map<String,Object>staticBindings) {
			super();
			this.methodAndParams = methodAndParams;
			this.urlRegExp = new NamedGroupPattern(urlRegExp);
			this.action = action;
			this.staticBindings = staticBindings;
		}
	}
	
	List<UrlBinding> bindings = new ArrayList<UrlBinding>();

	static Map<String,MethodParameter[]> getMethodParameterMap(Class<?> clazz) {
		Map<String,MethodParameter[]> map = ParameterTypeReader.getMethodParameters(clazz);
		return map;
	}

	final MethodAndParams notFoundHandler;
	
	public DefaultDispatcher() {
		notFoundHandler = constructMethodAndParams(StandardRequestHandlers.class, "pageNotFound", "");
	}
	
	protected MethodParameter[] getParameters(Class<?> clazz, String methodName) {
		return getMethodParameterMap(clazz).get(methodName);
	}

	static class MethodAndParams {
		public String action;
		public Method method;
		public MethodParameter parameters[];
	}

	protected MethodAndParams constructMethodAndParams(Class<?> clazz, String methodName, String action) {
		Method methodRef = null;
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(methodName)) {
				methodRef = m;
				break;
			}
		}
		
		if(methodRef == null) {
			throw new RuntimeException("no such method "+methodName+" in "+clazz.getCanonicalName());
		}

		MethodParameter parameters[] = getParameters(clazz, methodName);
		MethodAndParams mp = new MethodAndParams();
		mp.action = action;
		mp.method = methodRef;
		mp.parameters = parameters;
		return mp;
	}
	
	protected void addPath(String path, String action, Class<?> clazz, String methodName, Map<String,Object>staticParameters) {
		MethodAndParams mp = constructMethodAndParams(clazz, methodName, action);
		
		bindings.add(new UrlBinding(path, mp, action, staticParameters));
	}

	protected void addPath(String path, String action, Class<?> clazz, String methodName) {
		addPath(path, action, clazz, methodName, Collections.EMPTY_MAP);
	}

	static public BoundMethod constructBoundMethod(MethodAndParams mp) {
		Class<?> clazz = mp.method.getDeclaringClass();
		Object instance;
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return new BoundMethod(mp.parameters, instance, mp.method);
	}
	
/*	
	public BoundMethod getMethod(String path, HttpServletRequest request) {
		List<MethodAndParams> mps = mappedPaths.get(path);

		if(mps == null) {
			throw new RuntimeException("path "+path+" is unmapped.  Mapped paths are: "+mappedPaths.keySet());
		}
		
		MethodAndParams handler = null;
		for(MethodAndParams mp : mps) {
			if(mp.action.equals("*")) {
				handler = mp;
			} else if(request.getParameter("action_"+mp.action) != null) {
				handler = mp;
				break;
			}
		}
	}
*/

	protected void addPathesForClass(Class<?> clazz) {
		for(Method m : clazz.getMethods()) {
			Exposed exposed = m.getAnnotation(Exposed.class);
			if(exposed != null) {
				this.addPath(exposed.url(), exposed.action(), clazz, m.getName());
			}
		}
	}

	protected Class<?> convertUrlToClass(URL url) {
		return null;
	}
	
	protected void addPathesForPackage(String packageName) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> urls;
		try {
			urls = cl.getResources(packageName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		while(urls.hasMoreElements()) {
			URL url = urls.nextElement();

			Class<?> clazz = convertUrlToClass(url);
			if(clazz == null)
				continue;

			addPathesForClass(clazz);
		}
	}
	
	protected void cleanupRequestParameters(HttpServletRequest request, Map<String, String> cleanedParamMap) {
		// iterate through the parameters and throw an exception on duplicate parameters
		// we may want to allow this in the future, but it does make things
		// a little more complicated.
		Map<?,?> paramMap = request.getParameterMap();
		for(Map.Entry<?,?> e : paramMap.entrySet()) {
			String key = (String)e.getKey();
			String[] values = (String[])e.getValue();
			if(cleanedParamMap.containsKey(key)) {
				throw new DuplicatedParameterException(key);
			}
			cleanedParamMap.put(key, values[0]);
		}
	}
	
	public RequestContext getMethod(String path, HttpServletRequest request) {
		RequestContext ctx = new RequestContext();
		ctx.httpRequest = request;
		ctx.parameters = new HashMap<String, String>();
		
		for(UrlBinding binding: bindings) {
			Matcher m = binding.urlRegExp.matcher(path);
			if(m.matches()) {
				
				// now check action
				if(binding.action != null && binding.action.length() > 0) {
					// if we don't have the 'action' then continue 
					if(request.getParameter(binding.action) == null) {
						continue;
					}
				}
				
				Map<String, String> pathParameters = binding.urlRegExp.getAllGroups(m);
				ctx.parameters.putAll(pathParameters);
				cleanupRequestParameters(request, ctx.parameters);
				ctx.handler = constructBoundMethod(binding.methodAndParams);
				ctx.urlBinding = binding;
				return ctx;
			}
		}
		
		// if we reached here, we didn't find a matching binding
		ctx.handler = constructBoundMethod(notFoundHandler);
		return ctx;
	}
}
