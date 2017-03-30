package io.goudai.configurer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.goudai.configurer.datasource.DatasourceKit.connection;
import static io.goudai.configurer.datasource.DatasourceKit.statement;

/**
 * Created by freeman on 17/3/27.
 */
@Slf4j
public class MysqlApplication implements Application {


	static String insert = "insert into gd_application(gd_name) values (?)";
	static String delete = "delete from gd_application where gd_name=?";
	static String selectOne = "select gd_name from gd_application WHERE  gd_name = ?";
	static String update = "update gd_application set gd_name=? where gd_name =?";

	@Override
	public void deleteApplication(String app) {
		connection(connection -> executeDelete(app, connection, delete));
	}

	@Override
	public void createApplication(String app) {
		connection(connection -> {
			ResultSet resultSet = null;
			try (val st = connection.prepareStatement("SELECT * FROM gd_application WHERE gd_name = ?")) {
				st.setString(1, app);
				resultSet = st.executeQuery();
				if (resultSet.next()) {
					return;
				} else {
					try (val st2 = connection.prepareStatement(insert)) {
						st2.setString(1, app);
						st2.executeUpdate();
					}
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}

		});
	}

	@Override
	public void updateApplication(String app, String newApp) {
		connection(connection -> {
			try {
				connection.setAutoCommit(false);
				try (val ps = connection.prepareStatement(update)) {
					ps.setString(2, app);
					ps.setString(1, newApp);
					ps.executeUpdate();
					try (val ps2 = connection.prepareStatement("UPDATE gd_configurer SET gd_app = ? where gd_app=? ")) {
						ps2.setString(2, app);
						ps2.setString(1, newApp);
						ps2.executeUpdate();
					}
					ps.executeUpdate();
					connection.commit();
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}

	private void executeDelete(String app, Connection connection, String sql) {
		try {
			connection.setAutoCommit(false);
			try (val ps = connection.prepareStatement(delete)) {
				ps.setString(1, app);
				ps.executeUpdate();
				try (val ps2 = connection.prepareStatement("DELETE FROM gd_configurer where gd_app=?")) {
					ps2.setString(1, app);
					ps2.executeUpdate();
				}
				ps.executeUpdate();
				connection.commit();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public List<String> applicationList() {
		List<String> strings = new ArrayList<>();
		statement(statement -> {
			try (ResultSet resultSet = statement.executeQuery("select gd_name from gd_application")) {
				while (resultSet.next()) {
					strings.add(resultSet.getString("gd_name"));
				}
			}
		});
		return strings;
	}

	@Override
	public void createApp(String app, List<Entry> entries) {
		connection(connection -> {
			try {
				connection.setAutoCommit(false);
				try (val psp = connection.prepareStatement(selectOne)) {
					psp.setString(1, app);
					try (val rs = psp.executeQuery()) {
						if (!rs.next()) {
							try (val ps = connection.prepareStatement(insert)) {
								ps.setString(1, app);
								ps.executeUpdate();
							}
						}
					}
				}

				try (val ps2 = connection.prepareStatement("insert into gd_configurer(gd_app,gd_key,gd_value) values(?,?,?);")) {
					for (Entry entry : entries) {
						try (val st = connection.prepareStatement("SELECT gd_app,gd_key,gd_value from gd_configurer where gd_app =? and  gd_key = ?")) {
							st.setString(1, app);
							st.setString(2, entry.getKey());
							try (val rs = st.executeQuery()) {
								if (!rs.next()) {
									ps2.setString(1, app);
									ps2.setString(2, entry.getKey());
									ps2.setString(3, entry.getValue());
									ps2.addBatch();
								}
							}

						}
					}
					ps2.executeBatch();

					connection.commit();
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}
}
