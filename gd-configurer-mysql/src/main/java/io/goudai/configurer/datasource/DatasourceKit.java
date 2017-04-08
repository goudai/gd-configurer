package io.goudai.configurer.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class DatasourceKit {

	private static DataSource dataSource;
	static DruidDataSource druidDataSource;

//	static {
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			log.error(e.getMessage(),e);
//		}
//	}

	//
	public static final void init(@NonNull String url, @NonNull String username, String password, @NonNull String securityCode) {
		//"jdbc:mysql://192.168.10.240:3306/gd-configurer?useUnicode=true&characterEncoding=utf8"
		druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(url);
		druidDataSource.setUsername(username);
		druidDataSource.setPassword("123456");
		druidDataSource.setInitialSize(5);
		druidDataSource.setMaxActive(200);
		druidDataSource.setKeepAlive(true);
		druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		try {
			druidDataSource.init();
		} catch (SQLException e) {
			throw new RuntimeException("init druidDataSource fauld", e);
		}
		dataSource = druidDataSource;

		init();
		statement(statement -> {
			try (ResultSet resultSet = statement.executeQuery("SELECT * FROM gd_security_code")) {
				if (resultSet.next()) {
					statement.executeUpdate("UPDATE gd_security_code SET gd_security_code='" + securityCode+"'");
				} else {
					if(securityCode == null || "".equals(securityCode)){
						throw new RuntimeException("安全码必填");
					}
					statement.executeUpdate("INSERT INTO gd_security_code(gd_security_code) VALUES ('" + securityCode + "')");
				}
			}
		});
	}

	private static void init() {
		{
			statement(statement -> {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_configurer(id int auto_increment not null primary key,gd_app varchar(20) not null,gd_key varchar(255)  not null, gd_value varchar(255),UNIQUE KEY `app_key` (`gd_app`,`gd_key`))");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_application(id int auto_increment PRIMARY key,gd_name varchar(255) not null )");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_security_code(id int auto_increment PRIMARY key,gd_security_code varchar(255) not null )");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_user(id int auto_increment PRIMARY key,gd_username varchar(255) not null,gd_password VARCHAR(60) not null)");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_token(id int auto_increment PRIMARY key,gd_user_id int not NULL ,gd_token varchar(255) not null,created_time DATETIME)");
			});
		}

	}


	public static final void init(DataSource dataSource) {
		DatasourceKit.dataSource = dataSource;
		init();
	}

	public static final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	public static final void connection(ConnectionCallback callback) {
		try (Connection connection = DatasourceKit.getConnection();
		) {
			callback.apply(connection);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static final void statement(StatementCallback callback) {
		try (Connection connection = DatasourceKit.getConnection();
		     Statement statement = connection.createStatement();
		) {
			callback.apply(statement);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}


	@FunctionalInterface
	public interface StatementCallback {
		void apply(Statement connection) throws SQLException;
	}

	@FunctionalInterface
	public interface ConnectionCallback {
		void apply(Connection connection) throws SQLException;
	}


}
