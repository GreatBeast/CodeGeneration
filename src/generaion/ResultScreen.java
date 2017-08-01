package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class ResultScreen {

	public List<Visitor> vst;
	
	public ResultScreen(List<String> api){
		vst = new ArrayList<Visitor>();
		for (String a : api)
		{
			Visitor v = new Visitor(a);
			vst.add(v);
		}
	}
	
	public boolean screen(ASTNode root){
		for (Visitor v : vst)
		{
			v.resultNode = null;
			root.accept(v);
			if (v.resultNode == null)
				return false;
		}
		return true;
	}
	
}
