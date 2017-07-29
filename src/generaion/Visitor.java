package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class Visitor extends ASTVisitor {

	public ASTNode resultNode;
	private String str;
	
	public Visitor(String apiName){
		str = apiName;
		resultNode = null;
	}
	
	public void setAPIName(String apiName){
		str = apiName;
	}
	
	public boolean visit(SimpleName node){
		if (node.getIdentifier().equals(str))
		{
			resultNode = node;
			return false;
		}
		else
			return true;
	}
	
}
