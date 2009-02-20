package com.github.rd4j.sample.blog;

import java.lang.reflect.Field;

import javax.servlet.ServletContext;

import com.github.rd4j.DjangoTemplateResolutionFactory;
import com.github.rd4j.RequestContext;
import com.github.rd4j.RequestInterceptor;
import com.github.rd4j.Resolution;

public class InjectorInterceptor implements RequestInterceptor {
	ServletContext servletContext;
	DbSession dbSession;
	DjangoTemplateResolutionFactory djangoTemplateResolutionFactory;
	Object [] singletons;
	
	public InjectorInterceptor(ServletContext servletContext) 
	{
		this.servletContext = servletContext;
		dbSession = new DbSession();
		djangoTemplateResolutionFactory = new DjangoTemplateResolutionFactory(servletContext);
		
		singletons = new Object[]{ dbSession, djangoTemplateResolutionFactory, servletContext };
	}

	public Resolution intercept(RequestContext ctx) {
		Object target = ctx.getHandler().getTarget();
		for(Field field:target.getClass().getFields()) {
			InjectThis annotation = field.getAnnotation(InjectThis.class);
			if(annotation != null) {
				boolean assigned = false;
				for(Object obj : singletons) {
					if(field.getType().isAssignableFrom(obj.getClass())) {
						try {
							field.set(target, obj);
							assigned = true;
							break;
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
				if (!assigned) {
					throw new RuntimeException("Unknown class "+field.getType());
				}
			}
		}
		return null;
	}

}
