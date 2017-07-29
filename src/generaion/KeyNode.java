package generaion;

import org.eclipse.jdt.core.dom.*;

public class KeyNode {
	public String key;
	public Object node;
	public boolean flag;
	public KeyNode(String s, Object o)
	{
		key = s;
		node = o;
		flag = false;
	}
}
