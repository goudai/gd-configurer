package io.goudai.configurer.gateway;

import io.goudai.configurer.datasource.DatasourceKit;
import io.goudai.configurer.gateway.filter.SessionFilter;
import io.goudai.configurer.gateway.filter.UTF8CharacterFilter;
import io.goudai.configurer.gateway.resources.AppResources;
import io.goudai.configurer.gateway.resources.ConfigurerResources;
import io.goudai.configurer.gateway.resources.FileResources;
import io.goudai.configurer.gateway.resources.SessionResources;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import java.nio.charset.Charset;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.redirect;
import static io.undertow.servlet.Servlets.*;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class Main {
	public static void main(final String[] args) throws ServletException {
//		init datasource
		DatasourceKit.init("jdbc:mysql://192.168.10.240:3306/gd-configurer?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "123456","mizi");

		DeploymentInfo deploymentInfo = deployment()
				.setClassLoader(Main.class.getClassLoader())
				.setContextPath("/")
				.setDefaultEncoding("UTF-8")
				.setDeploymentName("gd.war")
				.addServlets(
						servlet(ConfigurerResources.class)
								.setLoadOnStartup(1)
								.addMapping("/configs/*")

						,servlet(AppResources.class)
								.setLoadOnStartup(1)
								.addMapping("/apps/*")

						,servlet(FileResources.class)
								.setMultipartConfig(new MultipartConfigElement("/tmp"))
								.setLoadOnStartup(1)
								.addMapping("/files")

						,servlet(SessionResources.class)
								.setLoadOnStartup(1)
								.addMapping("/sessions/*")

				).addFilters(

						filter("UTF8CharacterFilter", UTF8CharacterFilter.class)
						, filter("SessionFilter", SessionFilter.class))

				.addFilterUrlMapping("UTF8CharacterFilter", "/*", DispatcherType.REQUEST)
				.addFilterUrlMapping("SessionFilter", "/*", DispatcherType.REQUEST);

		DeploymentManager deploymentManager = defaultContainer()
				.addDeployment(deploymentInfo);
		deploymentManager.deploy();
		HttpHandler servletHandler = deploymentManager.start();
		HttpHandler errorHandler = exchange -> {
			try {
				servletHandler.handleRequest(exchange);
			}catch (Exception e){
				exchange.getOutputStream().write(e.getMessage().getBytes(Charset.forName("UTF-8")));
			}
		};

		PathHandler pathHandler = path(redirect("/")).addPrefixPath("/", errorHandler);

		Undertow server = Undertow.builder()
				.addHttpListener(8888, "localhost")
				.setHandler(pathHandler).build();
		log.info("start");
		server.start();
	}


}
