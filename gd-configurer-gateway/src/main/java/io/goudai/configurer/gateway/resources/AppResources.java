package io.goudai.configurer.gateway.resources;

import io.goudai.configurer.Application;
import io.goudai.configurer.GoudaiFactory;
import io.goudai.configurer.gateway.JacksonKit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by freeman on 17/3/29.
 */

public class AppResources extends HttpServlet {

	private Application application = GoudaiFactory.getApplication();
	private JacksonKit jacksonKit = JacksonKit.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<String> strings = application.applicationList();
		jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).result(strings).build());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("app_name");
		if (appName == null || "".equals(appName)) {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("需要创建appname不能为空").build());
		} else {
			application.createApplication(appName);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("app_name");
		if (appName == null || "".equals(appName)) {
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("需要删除的appname不能为空").build());
		} else {
			application.deleteApplication(appName);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("app_name");
		String newAppName = req.getParameter("new_app_name");
		if(appName == null || "".equals(appName)
		||newAppName == null  || "".equals(newAppName)){
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg("请使用标准restful规范传递appname => /apps/appname/newAppname").build());
		}else {
			application.updateApplication(appName, newAppName);
			jacksonKit.write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}



	}
}
