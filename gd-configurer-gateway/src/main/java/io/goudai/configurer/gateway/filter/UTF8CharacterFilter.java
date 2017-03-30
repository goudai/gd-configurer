package io.goudai.configurer.gateway.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by freeman on 17/3/30.
 */
public class UTF8CharacterFilter implements Filter {
	public UTF8CharacterFilter() {
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		response.setContentType("application/json; charset=utf-8");
		chain.doFilter(request, response);
	}
	@Override
	public void destroy() {

	}
}
