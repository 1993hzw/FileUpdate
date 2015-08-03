import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//sqilte数据库操作类
public class DAO {
	private boolean isInited=false;
	private static DAO dao;
	private Connection c = null;
	private Statement stmt = null;
	private String path;
	private DAO(String path) {
		this.path=path;
	}
//单例
	public static DAO instance(String path) {
		if(dao==null){
			dao=new DAO(path);
		}
		return dao;
	}
	//初始化
	public void init(){
		try {
			Class.forName("org.sqlite.JDBC");
			//在当前目录下生成files.db数据库
			c = DriverManager.getConnection("jdbc:sqlite:"+path+File.separator+"files.db");
			stmt = c.createStatement();
			ArrayList<String> list=new ArrayList<String>();
			//创建TIME FILE 表
			list.add("CREATE TABLE IF NOT EXISTS TIME "
					+ "(ID INTEGER PRIMARY KEY,"
					+ " time            INT     NOT NULL)" );
			list.add("CREATE TABLE IF NOT EXISTS FILE "
					+ "(ID INTEGER PRIMARY KEY,"
					+ " path            INT     NOT NULL," +
					"   timeID          INT     NOT NULL)"); 
			for(String sql:list)
			     stmt.executeUpdate(sql);
			isInited=true;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Tables created successfully");
	}
	//开启事务
	public void beginTransation(){
		String sql="BEGIN TRANSACTION";
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//提交事务
	public void commit(){
		String sql="COMMIT";
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//关闭数据库
	public void close(){
		if(!isInited) return;
		try {
			if(stmt!=null)
			    stmt.close();
			if(c!=null)
			    c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//添加一个新文件
	public void addFile(String path,int timeId){
		String sql="INSERT INTO FILE (path,timeID) values ('"+path+"',"+timeId+")";
		try {
			int n=stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//添加一个时间戳
	public void addTime(long time){
		String sql="INSERT INTO TIME (time) values ("+time+")";
		try {
			int n=stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//根据时间戳查找
	public ResultSet findTime(long time){
		String sql="SELECT * FROM TIME WHERE time="+time;
		try {
			ResultSet set=stmt.executeQuery(sql);
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	//获取所有时间
	public ResultSet getAllTime(){
		String sql="SELECT * FROM TIME";
		try {
			ResultSet set=stmt.executeQuery(sql);
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	//通过时间id查找文件
	public ResultSet findFileByTimeID(int timeID){
		String sql="SELECT * FROM FILE WHERE timeID="+timeID;
		try {
			ResultSet set=stmt.executeQuery(sql);
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	//获取最大的时间id
	public int getMaxTimeID(){
		String sql="SELECT max(ID) as ID FROM TIME";
		try {
			ResultSet set=stmt.executeQuery(sql);
			return set.getInt("ID");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	//通过路径查找文件
	public ResultSet findFileByPath(String path){
		String sql="SELECT * FROM FILE WHERE path='"+path+"'";
		try {
			ResultSet set=stmt.executeQuery(sql);
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
