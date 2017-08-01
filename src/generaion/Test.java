package generaion;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

	public static void main(String[] args){
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		List<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		list.add(a);
		List<Integer> b = new ArrayList<Integer>();
		b.add(4);
		b.add(5);
		list.add(b);
		List<Integer> c = new ArrayList<Integer>();
		c.add(1);
		c.add(2);
		//c.add(3);
		System.out.println(list.indexOf(c));
		System.out.println(a.containsAll(c));
	}
	
}
