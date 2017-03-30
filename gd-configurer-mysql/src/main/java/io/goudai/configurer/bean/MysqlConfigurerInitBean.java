package io.goudai.configurer.bean;

import io.goudai.configurer.datasource.DatasourceKit;
import lombok.Setter;

import javax.sql.DataSource;

/**
 * Created by freeman on 17/3/27.
 */
@Setter
public class MysqlConfigurerInitBean {

	private DataSource dataSource;

	public void init() {
		DatasourceKit.init(dataSource);
	}


}
