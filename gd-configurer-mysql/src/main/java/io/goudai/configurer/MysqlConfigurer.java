package io.goudai.configurer;


import io.goudai.configurer.datasource.DatasourceKit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.goudai.configurer.datasource.DatasourceKit.connection;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class MysqlConfigurer implements Configurer {



	static String insert = "insert into gd_configurer(gd_app,gd_key,gd_value) values(?,?,?);";
	static String delete = "DELETE FROM gd_configurer where gd_app = ? and gd_key = ?";
	static String update = "update gd_configurer set gd_value= ? where gd_app = ? and gd_key = ?";
	static String selectOne = "SELECT gd_app,gd_key,gd_value from gd_configurer where gd_app = ? gd_key = ?";
	static String selectByApp = "SELECT gd_app,gd_key,gd_value from gd_configurer where gd_app = ?";


	public void addAttribute(String app, String key, String value) {
		connection(connection -> {
			try (val ps = connection.prepareStatement(insert)) {
				exec(app, key, value, ps);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	public void deleteAttribute(String app, String key) {
		connection(connection -> {
			try (val ps = connection.prepareStatement(delete)) {
				ps.setString(1, app);
				ps.setString(2, key);
				ps.executeUpdate();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	public void update(String app, String key, String value) {
		connection(connection -> {
			try (val ps = connection.prepareStatement(update)) {
				exec(app, key, value, ps);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		});
	}


	public Entry getAttribute(String app, String key) {
		ResultSet resultSet = null;
		try (val connection = DatasourceKit.getConnection();
		     val ps = connection.prepareStatement(selectOne);
		) {
			ps.setString(1, app);
			ps.setString(2, key);
			resultSet = ps.executeQuery();
			if (resultSet.next()) {
				return Entry.builder()
						.key(resultSet.getString("gd_key"))
						.value(resultSet.getString("gd_value"))
						.build();

			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {

				}
			}
		}
		return new Entry();
	}

	public List<Entry> getAttributes(String app) {
		ResultSet resultSet = null;
		try (val connection = DatasourceKit.getConnection();
		     val ps = connection.prepareStatement(selectByApp);
		) {
			ps.setString(1, app);
			resultSet = ps.executeQuery();
			List<Entry> result = new ArrayList<>(10);
			while (resultSet.next()) {
				result.add(Entry.builder()
						.key(resultSet.getString("gd_key"))
						.value(resultSet.getString("gd_value"))
						.build());
			}
			return result;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {

				}
			}
		}
		return Collections.emptyList();
	}



	private void exec(String app, String key, String value, PreparedStatement ps) throws SQLException {
		ps.setString(1, app);
		ps.setString(2, key);
		ps.setString(3, value);
		ps.executeUpdate();
	}
}
