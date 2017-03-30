# 狗带配置中心

## 主要功能
* 以简单为核心思想
* 主要是使用mysql存储 
* 接口全部采用标准restful 接口
* 支持配置文件上传解析
* 使用内嵌undertow servlet容器
	
## 实现技术

* 数据库连接池 druid
* json解析 jackson
* web容器 undertow
	
## 安装到本地

	git https://github.com/goudai/gd-configurer.git
	cd gd-configurer
	mvn clean install
	
## 启动
	修改 io.goudai.configurer.gateway.Main 中的Datasource数据库url等
	
## restful api 一览


### 返回对象定义

````

public class R {
	private int code;
	private String msg; //异常信息描述
	private Object result;

	public static interface C {
		
		int S = 200, 
		BF = 300, //业务异常
		SF = 500; //服务器异常

	}

	public static interface M {
		String S = "SUCCESS";
		String F = "FAILED";
	}
}

````


#### apps 接口 

* 注意 request body 一般使用body的形式 不推荐使用url拼接 

| 方法        | api           | requestBody  | 描述
| ------------- |:-------------:| -----:| ---------:|
| get      | /apps  | 无 | 返回所有的应用列表 |
| post      | /apps       |   app_name | 新增一个应用 |
| delete | /apps       |    app_name | 删除一个应用，注意这里会级联删除配置 |
| put | /apps       |    app_name,new_app_name | 修改一个应用的名称，将级联修改配置 |


#### configs 接口

| 方法        | api           | requestBody  | 描述
| ------------- |:-------------:| -----:| ---------:|
| get      | /configs/${app_name}  | 无 | 返回指定应用的配置信息 |
| post      | /configs       |   app_name,key,value | 为应用添加一个配置 |
| delete | /configs       |    app_name,key | 删除一个应用的配置 |
| put | /configs       |    app_name,key,value | 修改一个应用的配置 |

#### file 接口 
* 这个接口处理 properties 

| 方法        | api           | requestBody  | 描述
| ------------- |:-------------:| -----:| ---------:|
| post      | /files       |   app_name,file | 通过文件上传的形式添加配置 |


##TODO
	鉴权
     web 页面控制台

