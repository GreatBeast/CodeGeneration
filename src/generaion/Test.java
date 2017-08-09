package generaion;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

	public static void main(String[] args){
		String srcPath = "E://test//test.java"; // java文件所在的文件夹
		ASTNode node = AstProd.getAstTree(srcPath);
		List<StructuralPropertyDescriptor> spd = node.structuralPropertiesForType();
		List<ASTNode> nodeList = (List<ASTNode>)node.getStructuralProperty(spd.get(0));
		ASTNode node1 = nodeList.get(0);
		List<StructuralPropertyDescriptor> spd1 = node1.structuralPropertiesForType();
		List<ASTNode> nodeList1 = (List<ASTNode>)node1.getStructuralProperty(spd1.get(0));
		
		VariableDeclarationStatement node2 = (VariableDeclarationStatement)nodeList1.get(0);
//		List<StructuralPropertyDescriptor> spd2 = node2.structuralPropertiesForType();
		List<Object> fg = node2.fragments();
		Type tp = node2.getType();
		
		System.out.println(0);
	}
	
}
