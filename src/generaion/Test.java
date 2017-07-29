package generaion;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

	public static void main(String[] args){
		List<Integer> a = new ArrayList();
		a.add(0);
		a.add(1);
		a.add(2);
		System.out.println(a.subList(0, a.size() - 1));
	}
	
}
