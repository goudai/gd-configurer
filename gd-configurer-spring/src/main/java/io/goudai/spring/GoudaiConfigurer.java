package io.goudai.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created by freeman on 17/3/27.
 */
@Order(Integer.MIN_VALUE)
public class GoudaiConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {
	//HTTP://config.goudai.com
	private String server;
	// 8080
	private int port;
	// la
	private String appName;

	private final Properties prop = new Properties();

	@Override
	protected Properties mergeProperties() throws IOException {
		Properties properties = super.mergeProperties();
		properties.putAll(prop);
		return properties;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(appName, "app name can not be null");
		Assert.notNull(server, "server can not be null");
		Assert.notNull(port, "port can not be null");
		ObjectMapper objectMapper = new ObjectMapper();
		R r = objectMapper.readValue(new URL(String.format(String.format("%s:%s/configs/%s", this.server, this.port, this.appName))), R.class);
		if (r.getCode() == 200) {
			r.getResult().forEach(entry -> prop.put(entry.getKey(), entry.getValue()));
		} else {
			throw new RuntimeException(String.format("connected goudai configurer server failed , server = %s ,port=%s,appname=%s", this.server, this.port, this.appName));
		}
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}

class R {
	private int code;
	private String msg;
	private List<Entry> result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Entry> getResult() {
		return result;
	}

	public void setResult(List<Entry> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "R{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", result=" + result +
				'}';
	}
}

class Entry {
	private String key;
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Entry{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
