package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class AstMapTree {

	public ASTNode root;
	public List<String> key = new ArrayList<String>();
	public List<ASTNode> nodes = new ArrayList<ASTNode>();
	public List<KeyNode> stack = new ArrayList<KeyNode>();
	public int count;
	public List<AstMapTree> input = new ArrayList<AstMapTree>();
	//位置规则，00代表父节点，>00代表子节点，0A字母代表序列中节点所在的位置，每一步的节点均有两个字符表示
	
	public AstMapTree(){
		
	}
	
	/*
	 * 在封装成AstMapTree的同时，找到api节点
	 * @tree：根节点
	 * @api：api字符串
	 */
	public AstMapTree(ASTNode tree, String api){
		Visitor v = new Visitor(api);
		root = tree;
		key.add("");
		root.accept(v);
		nodes.add(v.resultNode);
		count = 0;
	}
	
	/*
	 * 设置一个AstMapTree的input集
	 * @ipt：input集
	 */
	public void setInput(List<AstMapTree> ipt){
		input.removeAll(input);
		input.addAll(ipt);
	}
	
	/*
	 * 设置一个节点对应字符串路径
	 * @node：某一节点
	 * @str：该节点对应路径
	 */
	public void setPosi(ASTNode node, String str){
		key.add(str);
		nodes.add(node);
	}
	
	/*
	 * 通过节点寻找其路径
	 * @node：某一节点
	 */
	public String getPosi(ASTNode node){
		if (nodes.contains(node))
		{
			int posi = nodes.indexOf(node);
			String str = key.get(posi);
			return str;
		}
		return null;
	}
	
	/*
	 * 通过路径寻找其节点
	 * @str：某一路径
	 */
	public ASTNode getNode(String str){
		if (key.contains(str))
		{
			int posi = key.indexOf(str);
			ASTNode node = nodes.get(posi);
			return node;
		}
		return null;
	}
	
	public AstMapTree copyTree(AstMapTree input, AST ast){
		ASTNode newNode = ASTNode.copySubtree(ast, input.root);
		return null;
	}
	
	public List<ASTNode> getNodes(){
		return nodes;
	}
	
	public List<String> getKey(){
		return key;
	}
}
