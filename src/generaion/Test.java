package generaion;

import org.eclipse.jdt.core.dom.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

	public static void main(String[] args){
		 try {
	            File file = new File("result.txt");
	            PrintStream ps = new PrintStream(new FileOutputStream(file));
	            ps.println("1");// ���ļ���д���ַ���
	            ps.println("2");
	            ps.append("3");// �����еĻ���������ַ���
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	}
	
}
