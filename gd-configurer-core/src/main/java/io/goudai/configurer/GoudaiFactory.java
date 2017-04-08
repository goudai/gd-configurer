package io.goudai.configurer;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class GoudaiFactory {

	private static Configurer configurer = null;
	private static Application application = null;
	private static Authentication authentication = null;

	static {
		ServiceLoader<Configurer> serviceLoader = ServiceLoader.load(Configurer.class);
		Iterator<Configurer> configurerIterator = serviceLoader.iterator();
		if (configurerIterator.hasNext()) {
			configurer = configurerIterator.next();
			log.info("found Configurer impl at " + configurer);
		} else {
			throw new ImplNotFoundException("can not found Configurer impl");

		}

		ServiceLoader<Application> serviceLoader2 = ServiceLoader.load(Application.class);
		Iterator<Application> configurerIterator2 = serviceLoader2.iterator();
		if (configurerIterator2.hasNext()) {
			application = configurerIterator2.next();
			log.info("found Application impl at " + application);
		} else {
			throw new ImplNotFoundException("can not found Application impl");
		}

		ServiceLoader<Authentication> serviceLoader3 = ServiceLoader.load(Authentication.class);
		Iterator<Authentication> configurerIterator3 = serviceLoader3.iterator();
		if (configurerIterator3.hasNext()) {
			authentication = configurerIterator3.next();
			log.info("found Application impl at " + authentication);
		} else {
			throw new ImplNotFoundException("can not found Authentication impl");
		}
	}


	public static Configurer getConfigurer() {
		return configurer;
	}

	public static Application getApplication() {

		return application;

	}

	public static Authentication getAuthentication() {
		return authentication;

	}
}

class ImplNotFoundException extends RuntimeException {
	public ImplNotFoundException(String message) {
		super(message);
	}
}
