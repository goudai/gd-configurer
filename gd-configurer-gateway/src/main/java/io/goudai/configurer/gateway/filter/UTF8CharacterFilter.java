package io.goudai.configurer.gateway.filter;

import io.goudai.configurer.gateway.JacksonKit;
import io.goudai.configurer.gateway.resources.model.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

import static io.goudai.configurer.gateway.resources.model.R.C.SF;

/**
 * Created by freeman on 17/3/30.
 */
@Slf4j
public class UTF8CharacterFilter implements Filter {
	public UTF8CharacterFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			response.setContentType("application/json; charset=utf-8");
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			ServletOutputStream outputStream = response.getOutputStream();
			JacksonKit.getInstance().write(outputStream, R.builder().code(SF).msg(e.getMessage()).build());
			outputStream.flush();
		}
	}

	@Override
	public void destroy() {

	}
}
