import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileUpate {
	private File dir;// 当前目录
	private long time = 0;// 当前时间
	private int timeID = 0;// 当前时间id
	private DAO dao;// 数据库操作对象
	private Map<Integer, ArrayList> map;// 存放文件信息
	private ArrayList<Time> allTimes;

	// 构造函数
	public FileUpate(String path) {
		map = new HashMap<Integer, ArrayList>();
		allTimes = new ArrayList<Time>();
		dir = new File(path);
		time = new Date().getTime();
		dao = DAO.instance(new File(path).getAbsolutePath());
		try {
			dao.init();// 初始化数据库
			dao.beginTransation();
			// 获取当前时间ID
			timeID = dao.getMaxTimeID() + 1;
			map.put(timeID, new ArrayList<String>());
			// 获取所偶的时间ID，放入map对象，作为key值，value为arraylist数组，存放文件路径
			ResultSet set = dao.getAllTime();
			while (set.next()) {
				map.put(set.getInt("ID"), new ArrayList<String>());
				allTimes.add(new Time(set.getInt("ID"), set.getLong("time")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存信息到当前目录
	public void save() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(dir, "++++++文件更新列表++++++++++.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			writer.write("by hzw\r\n");
			for (int i = allTimes.size() - 1; i >= 0; i--) {
				Time t = allTimes.get(i);
				ArrayList<String> list = map.get(t.id);
				if (list.size() == 0)
					continue;
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				writer.write("\r\n" + sdf.format(new Date(t.time))
						+ "\r\n-----\r\n");
				for (String s : list) {
					writer.write(s + "\r\n");
				}
				writer.write("+++++++++++++++++++++++++++++++++++++++++++\r\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 查找文件更新
	public void update() {
		search(dir);
		// 如果有新的文件更新则在数据库中记录下来
		if (map.get(timeID).size() > 0) {
			dao.addTime(time);
			allTimes.add(new Time(timeID, time));
		}
		dao.commit();
		dao.close();
	}

	// 遍历当前目录下的文件
	private void search(File file) {
		if (file.isDirectory()) {
			// 过滤掉.开头的文件
			File[] lists = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return !name.startsWith(".");
				}
			});
			if (lists != null)
				for (File f : lists) {
					search(f);
				}
		} else {
			int id = checkPathIsRecorded(file.getAbsolutePath().replace("'",
					"''"));
			if (id != -1) {
				map.get(id).add(file.getAbsolutePath());
			} else {// 记录当前路径到数据库
				System.out.println(file.getAbsolutePath());
				dao.addFile(file.getAbsolutePath().replace("'", "''"), timeID);
				map.get(timeID).add(file.getAbsolutePath());
			}
		}
	}

	// 检查路径是否被记录在数据库
	private int checkPathIsRecorded(String path) {
		ResultSet set = dao.findFileByPath(path);
		try {
			if (set == null || !(set.next())) {
				return -1;
			} else {
				return set.getInt("timeID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private class Time {
		public int id;
		public long time;

		public Time(int id, long time) {
			this.id = id;
			this.time = time;
		}
	}
}
