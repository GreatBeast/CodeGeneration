package generaion;

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class pyJava {
	public static ArrayList<String> run_py(String script) {
        String s = null;
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> error = new ArrayList<String>();
        try {
            Process p = Runtime.getRuntime().exec(script);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                 System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                error.add(s);
            }
            if (error.size() > 0) {
                System.out.println(error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public static String readTxtFile(String filePath){
        try {
                String encoding="UTF-8";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                        return lineTxt;
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return null;
    }
	
	public static void main(String[] args){
		System.out.println("Please input your query :");
		Scanner sc = new Scanner(System.in);  
		String query = sc.nextLine();
		
		run_py("python src/search/getsequence.py " + query);
		System.out.println("end");
		
		System.out.println("Do you need to download code :");
		String yon = sc.nextLine();
		if (yon.equals("y"))
		{
			System.out.println("Downloading code, please wait...");
			run_py("python src/search/getcode.py 1");
			System.out.println("end");
		}
		
//		String query = "rename a file";
		String srcPath = "src/search/code";
		String seq = readTxtFile("src/search/api.txt");
		System.out.println(seq);
		String[] apis = seq.split("[+]");
		List<String> allApi = new ArrayList<String>();
		System.out.println("The api sequence is :");
		for(String api : apis)
		{
			String a = api.substring(1, api.length());
			if (!a.equals("new"))
			{
				allApi.add(a);
				System.out.println(api.substring(1, api.length()));
			}
		}
		
		List<ASTNode> result = Generation.mainGeneration(allApi, srcPath);
		try {
            File file = new File("result.txt");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println("Query: " + query);
            ps.println("API Sequence: " + seq);
            ps.println("OUTPUT: ");
            for (ASTNode node : result)
            {
            	ps.println(node);
            	ps.println("==================");
            }
            ps.println("end");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
}
