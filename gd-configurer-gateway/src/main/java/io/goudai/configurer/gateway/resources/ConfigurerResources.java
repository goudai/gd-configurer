package io.goudai.configurer.gateway.resources;

import io.goudai.configurer.Configurer;
import io.goudai.configurer.Entry;
import io.goudai.configurer.GoudaiFactory;
import io.goudai.configurer.gateway.JacksonKit;
import io.goudai.configurer.gateway.resources.model.R;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by freeman on 17/3/29.
 */
//@Resources("configurer")
public class ConfigurerResources extends HttpServlet {

	Configurer configurer = GoudaiFactory.getConfigurer();
	private JacksonKit jacksonKit = JacksonKit.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String request = req.getRequestURI();
		request = URLDecoder.decode(new String(request.getBytes(Charset.forName("ISO-8859-1"))), "utf-8");
		String[] split = request.substring(1, request.length()).split("/");
		if (split.length == 2) {
			String appName = split[1];
			List<Entry> attributes = configurer.getAttributes(appName);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).result(attributes).build());
		} else {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("请使用标准restful规范传递appname => /configs/appname").build());
		}


	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("app_name");
		String key = req.getParameter("key");
		String value = req.getParameter("value");
		if (appName == null || "".equals(appName)
				|| key == null || "".equals(key)
				|| value == null || "".equals(value)
				) {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.BF).msg("参数有误").build());
		} else {
			configurer.addAttribute(appName, key, value);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String request = req.getRequestURI();
		request = URLDecoder.decode(new String(request.getBytes(Charset.forName("ISO-8859-1"))), "utf-8");
		String[] split = request.substring(1, request.length()).split("/");
		if (split.length == 3) {
			String appName = split[1];
			String key = split[2];
			configurer.deleteAttribute(appName, key);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		} else {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("请使用标准restful规范传递appname => /configs/appname/key").build());
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("app_name");
		String key = req.getParameter("key");
		String value = req.getParameter("value");
		if (appName == null || "".equals(appName)
				|| key == null || "".equals(key)) {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("请使用put方法的参数传递方式 request body => app_name=,key=,value=").build());
		} else {
			configurer.update(appName, key, value);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}
	}
}
