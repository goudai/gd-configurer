package io.goudai.configurer;

import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.goudai.configurer.datasource.DatasourceKit.connection;

/**
 * Created by freeman on 17/4/6.
 */
public class MysqlAuthentication implements Authentication {

	static String selectUsername = "select * from gd_user where gd_username=?";
	static String selectToken = "select * from gd_token where gd_token=?";
	static String register = "INSERT INTO gd_user(gd_username,gd_password) VALUES (?,?)";
	static String selectSecurityCode = "SELECT * FROM gd_security_code where gd_security_code=?";
	static String insertToken = "INSERT INTO gd_token(gd_user_id,gd_token,created_time) VALUES (?,?,?)";
	static String selectTokenByUserId = "select * from gd_token where gd_user_id=?";
	static String updateTokenByUserId = "UPDATE gd_token SET gd_token=? WHERE gd_user_id=?";

	@Override
	public Principal authentic(String username, String password) {
		AtomicBoolean ok = new AtomicBoolean(false);
		String token = UUID.randomUUID().toString().replace("-", "");

		connection(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(selectUsername);
			preparedStatement.setString(1, username);
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				if (resultSet.next()) {
					String gd_password = resultSet.getString("gd_password");
					if (gd_password.equals(password)) {
						ok.set(true);
					} else {
						int id = resultSet.getInt("id");
						try (PreparedStatement preparedStatement2 = conn.prepareStatement(selectTokenByUserId)) {
							preparedStatement2.setInt(1, id);
							try (ResultSet resultSet1 = preparedStatement2.executeQuery();) {
								if (resultSet1.next()) {
									try (PreparedStatement preparedStatement3 = conn.prepareStatement(updateTokenByUserId)) {
										preparedStatement3.setString(1, token);
										preparedStatement3.setInt(2, id);
									}
								} else {
									try (PreparedStatement preparedStatement1 = conn.prepareStatement(insertToken)) {
										preparedStatement1.setInt(1, id);
										preparedStatement1.setString(2, token);
										preparedStatement1.setObject(3, new Date());
										preparedStatement1.executeUpdate();
									}
								}
							}
						}
						ok.set(true);
					}
				}
			}
		});
		if (ok.get()) {
			return () -> token;
		}
		throw new RuntimeException("账户密码错误");
	}

	@Override
	public Principal authentic(String token) {
		AtomicBoolean ok = new AtomicBoolean(true);
		connection(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(selectToken);
			preparedStatement.setString(1, token);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				ok.set(true);
			} else {
				ok.set(false);
			}
		});
		if (ok.get()) {
			return () -> token;
		}
		throw new RuntimeException("token错误");
	}

	@Override
	public Principal register(String username, String password, String securityCode) {
		AtomicBoolean ok = new AtomicBoolean(false);
		connection(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(register);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			ok.set(true);
		});
		if (ok.get()) {
			return () -> username;
		}
		throw new RuntimeException("注册失败");
	}

	@Override
	public boolean checkSecurityCode(String securityCode) {
		AtomicBoolean ok = new AtomicBoolean(true);
		connection(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(selectSecurityCode);
			preparedStatement.setString(1, securityCode);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				ok.set(true);
			} else {
				ok.set(false);
			}
		});
		return ok.get();
	}
}
