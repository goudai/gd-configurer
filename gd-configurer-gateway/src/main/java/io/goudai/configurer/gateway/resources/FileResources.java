package io.goudai.configurer.gateway.resources;

import io.goudai.configurer.Entry;
import io.goudai.configurer.GoudaiFactory;
import io.goudai.configurer.gateway.JacksonKit;
import io.goudai.configurer.gateway.resources.model.R;
import lombok.NonNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by freeman on 17/3/30.
 */
public class FileResources extends HttpServlet {

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doHead(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		@NonNull String appName = req.getParameter("app_name");
		if (req.getParts().size() > 1) {
			JacksonKit.getInstance().write(resp.getOutputStream(), R.builder().code(R.C.BF).msg("只支持传入一个文件解析").build());
		} else {
			for (Part part : req.getParts()) {
				Set<Entry> entries = new HashSet<>();
				Properties properties = new Properties();
				properties.load(part.getInputStream());
				properties.forEach((k, v) -> {
					entries.add(Entry.builder().key((String) k).value((String) v).build());
				});
				GoudaiFactory.getApplication().createApp(appName, new ArrayList<>(entries));
				part.delete();
			}
			JacksonKit.getInstance().write(resp.getOutputStream(), R.builder().code(R.C.S).msg(R.M.S).build());
		}
	}
}
