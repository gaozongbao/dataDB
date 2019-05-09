package cmdi.sd.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 数据库连接对象
 * @author gaozb
 */
public class DBHelper {
	private String driver;
	private String dburl;
	private String userName;
	private String passWd;
	private Connection connection;

	public DBHelper(String driver, String dburl, String userName, String passWd) {
		super();
		this.driver = driver;
		this.dburl = dburl;
		this.userName = userName;
		this.passWd = passWd;
	}

	public DBHelper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Connection initDB() {
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(dburl + "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false", userName, passWd);
			return this.connection;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭连接
	 * @author gaozb
	 */
	public void closeConn() {
		if (null != connection)
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public String toString() {
		return "DBHelper{" +
				"driver='" + driver + '\'' +
				", dburl='" + dburl + '\'' +
				", userName='" + userName + '\'' +
				", passWd='" + passWd + '\'' +
				'}';
	}

	/**
	 * 执行sql
	 * @author gaozb
	 */
	public void executeSQL(String Sql) {
		Statement statement = null;
		try {
			// 把自动提交关闭
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement.executeUpdate(Sql);
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param file
	 * @param tableName
	 * @param columns
	 * @param delimiter
	 * @return
	 */
	public boolean copyByStDin(File file, String tableName, String columns, String delimiter) {
		boolean flag = false;
		
		return flag;
	}

}
