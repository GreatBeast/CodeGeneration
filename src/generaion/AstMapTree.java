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
	//λ�ù���00�����ڵ㣬>00�����ӽڵ㣬0A��ĸ���������нڵ����ڵ�λ�ã�ÿһ���Ľڵ���������ַ���ʾ
	
	public AstMapTree(){
		
	}
	
	/*
	 * �ڷ�װ��AstMapTree��ͬʱ���ҵ�api�ڵ�
	 * @tree�����ڵ�
	 * @api��api�ַ���
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
	 * ����һ��AstMapTree��input��
	 * @ipt��input��
	 */
	public void setInput(List<AstMapTree> ipt){
		input.removeAll(input);
		input.addAll(ipt);
	}
	
	/*
	 * ����һ���ڵ��Ӧ�ַ���·��
	 * @node��ĳһ�ڵ�
	 * @str���ýڵ��Ӧ·��
	 */
	public void setPosi(ASTNode node, String str){
		key.add(str);
		nodes.add(node);
	}
	
	/*
	 * ͨ���ڵ�Ѱ����·��
	 * @node��ĳһ�ڵ�
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
	 * ͨ��·��Ѱ����ڵ�
	 * @str��ĳһ·��
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
