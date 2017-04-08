package io.goudai.configurer.gateway.resources.model;

import lombok.*;

/**
 * Created by freeman on 17/3/29.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class R {
	private int code;
	private String msg;
	private Object result;

	public static interface C {
		int S = 200, BF = 300, SF = 500;

	}

	public static interface M {
		String S = "SUCCESS";
		String F = "FAILED";
	}
}
