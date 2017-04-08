package io.goudai.configurer;

import java.security.Principal;

/**
 * Created by freeman on 17/4/6.
 */
public interface Authentication {

	Principal authentic(String username, String password);

	Principal authentic(String token);

	Principal register(String username, String password, String securityCode);

	boolean checkSecurityCode(String securityCode);


}
