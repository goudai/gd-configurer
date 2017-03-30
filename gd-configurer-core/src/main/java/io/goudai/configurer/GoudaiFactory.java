package io.goudai.configurer;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class GoudaiFactory {


	public static Configurer getConfigurer() {
		ServiceLoader<Configurer> serviceLoader = ServiceLoader.load(Configurer.class);
		Iterator<Configurer> configurerIterator = serviceLoader.iterator();
		if (configurerIterator.hasNext()) {
			Configurer next = configurerIterator.next();
			log.info("found Configurer impl at " + next);
			return next;
		}
		throw new RuntimeException("can not found Configurer impl");
	}

	public static Application getApplication() {
		ServiceLoader<Application> serviceLoader = ServiceLoader.load(Application.class);
		Iterator<Application> configurerIterator = serviceLoader.iterator();
		if (configurerIterator.hasNext()) {
			Application next = configurerIterator.next();
			log.info("found Application impl at " + next);
			return next;
		}
		throw new RuntimeException("can not found Application impl");
	}
}
