package io.goudai.configurer.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by freeman on 17/3/29.
 */
@Slf4j
public class JacksonKit {
	private static JacksonKit ourInstance = new JacksonKit();
	private ObjectMapper objectMapper = new ObjectMapper();

	public static JacksonKit getInstance() {
		return ourInstance;
	}

	private JacksonKit() {
	}

	public ObjectMapper objectMapper() {
		return objectMapper;
	}

	public void write(OutputStream out, Object value) {
		try {
			this.objectMapper.writeValue(out, value);
		} catch (IOException e) {
			log.error(e.getMessage(), e);

		}
	}
}
