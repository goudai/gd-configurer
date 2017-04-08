package io.goudai.configurer.gateway.filter;

import io.goudai.configurer.Authentication;
import io.goudai.configurer.GoudaiFactory;
import io.goudai.configurer.gateway.JacksonKit;
import io.goudai.configurer.gateway.resources.model.R;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import static io.goudai.configurer.gateway.resources.model.R.C.S;
import static io.goudai.configurer.gateway.resources.model.R.C.SF;

/**
 * Created by freeman on 17/4/6.
 */
public class SessionFilter implements Filter {

	Authentication authentication = GoudaiFactory.getAuthentication();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");
		if (authorization == null || "".equals(authorization)) {
			String requestURI = httpServletRequest.getRequestURI();

			if ("/sessions".equals(requestURI) && httpServletRequest.getMethod().toUpperCase().equals("POST".toUpperCase())) {
				chain.doFilter(request, response);
			} else if (requestURI.equals("/") && httpServletRequest.getMethod().toUpperCase().equals("POST".toUpperCase())) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				if (username == null || "".equals(username)
						|| password == null || "".equals(password)
						) {
					JacksonKit.getInstance().write(response.getOutputStream(), R.builder().code(SF).msg("用户名和密码不能为空").build());
				} else {
					Principal authentic = authentication.authentic(username, password);
					JacksonKit.getInstance().write(response.getOutputStream(),
							R.builder().code(S).msg(R.M.S)
									.result(Collections
											.singletonMap(authentic.getName(),
													UUID.randomUUID().toString()
															.replace("-", ""))).build());
				}

			} else {
				JacksonKit.getInstance().write(response.getOutputStream(),
						R.builder().code(S).msg("未认证的用户")
								.result(new SessionHelper()).build());
			}

		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {

	}
}

@Getter
@Setter
class SessionHelper {
	private String login = "curl -l -H \"Content-type: application/json\" -X POST -d '{\"username\":\"13521389587\",\"password\":\"test\"} 127.0.0.1:80", request = "curl -H \"Authorization:OAUTH-TOKEN\" 127.0.0.1:80", register = "curl -l -H \"Content-type: application/json\" -X POST -d '{\"username\":\"13521389587\",\"password\":\"test\",\"security_code\":\"your security code\"} host:port/sessions";
}

