import java.io.File;

public class Main {
	public static void main(String[] args) {
		//获取当前目录
		FileUpate fileUpate=new FileUpate(new File("").getAbsolutePath());
		//检查更新
		fileUpate.update();
		//保存
		fileUpate.save();
	}
}
