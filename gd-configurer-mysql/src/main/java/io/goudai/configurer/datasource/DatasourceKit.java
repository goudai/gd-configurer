package io.goudai.configurer.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class DatasourceKit {

	private static DataSource dataSource;
	static DruidDataSource druidDataSource;

	static {
		try {
			Class.forName(com.mysql.jdbc.Driver.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//
	public static final void init(String url, String username, String password) {
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
	}

	{
		statement(sts -> {
			try {
				sts.executeUpdate("CREATE TABLE IF NOT EXISTS gd_configurer(id int auto_increment not null primary key,gd_app varchar(20) not null,gd_key varchar(255)  not null, gd_value varchar(255),UNIQUE KEY `app_key` (`gd_app`,`gd_key`))");
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	{
		statement(statement -> {
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS gd_application(id int auto_increment PRIMARY key,gd_name varchar(255) not null )");
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	public static final void init(DataSource dataSource) {
		DatasourceKit.dataSource = dataSource;
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
			callback.callback(connection);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static final void statement(StatementCallback callback) {
		try (Connection connection = DatasourceKit.getConnection();
		     Statement statement = connection.createStatement();
		) {
			callback.callback(statement);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}


	@FunctionalInterface
	public interface StatementCallback {
		void callback(Statement connection) throws SQLException;
	}

	@FunctionalInterface
	public interface ConnectionCallback {
		void callback(Connection connection);
	}


}
