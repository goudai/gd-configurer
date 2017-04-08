package io.goudai.configurer.gateway.resources;

import io.goudai.configurer.Authentication;
import io.goudai.configurer.GoudaiFactory;
import io.goudai.configurer.gateway.JacksonKit;
import io.goudai.configurer.gateway.resources.model.R;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

import static io.goudai.configurer.gateway.resources.model.R.C.S;
import static io.goudai.configurer.gateway.resources.model.R.C.SF;

/**
 * Created by freeman on 17/4/6.
 */
public class SessionResources extends HttpServlet {

	Authentication authentication = GoudaiFactory.getAuthentication();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		String securityCode = request.getParameter("security_code");
		if (authentication.checkSecurityCode(securityCode)) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			if (username == null || "".equals(username)
					|| password == null || "".equals(password)
					) {
				JacksonKit.getInstance().write(resp.getOutputStream(), R.builder().code(SF).msg("用户名和密码不能为空").build());
			} else {
				Principal register = authentication.register(username, password, securityCode);
				JacksonKit.getInstance().write(resp.getOutputStream(), R.builder().code(S).msg("恭喜注册成功").build());
			}

		} else {
			JacksonKit.getInstance().write(resp.getOutputStream(), R.builder().code(SF).msg("安全码，校验不通过").build());
		}


	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("");
	}
}
