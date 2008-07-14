package com.github.rd4j.sample.blog;

import java.lang.reflect.Field;

import com.github.rd4j.RequestContext;
import com.github.rd4j.RequestInterceptor;
import com.github.rd4j.Resolution;

public class InjectorInterceptor implements RequestInterceptor {
	DbSession dbSession = new DbSession();
	
	public Resolution intercept(RequestContext ctx) {
		Object target = ctx.handler.getTarget();
		for(Field field:target.getClass().getFields()) {
			InjectThis annotation = field.getAnnotation(InjectThis.class);
			if(annotation != null) {
				if(field.getType().isAssignableFrom(DbSession.class)) {
					try {
						field.set(target, dbSession);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					throw new RuntimeException("Unknown class "+field.getType());
				}
			}
		}
		return null;
	}

}
