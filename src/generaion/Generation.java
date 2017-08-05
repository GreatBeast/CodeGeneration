package generaion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class Generation {

	
	public static final int MINSUP = 5; // 最小支持度
	public AST ast = AST.newAST(AST.JLS8); // 用来新建所有节点的ast
	public AstMapTree fqTree;
	public String apiStr; // 输入的api字符串
	public List<AstMapTree> allInput;
	public List<AstMapTree> finalResult;
	
	/*
	 * 项目的初始化相关操作，将所有输入集ASTNode节点类型封装成AstMapTree类型，设置了输入的api字符串
	 * @souInput：输入集
	 * @api：api字符串
	 */
	public void init(List<ASTNode> souInput, String api){
		allInput = new ArrayList<AstMapTree>();
		finalResult = new ArrayList<AstMapTree>();
		for (ASTNode tree : souInput)
		{
			AstMapTree amt = new AstMapTree(tree, api);
			allInput.add(amt);
		}
		ASTNode node = ast.createInstance(ASTNode.SIMPLE_NAME);
		((SimpleName)node).setIdentifier(api);
		fqTree = new AstMapTree(node, api);
		List<StructuralPropertyDescriptor> spd = node.structuralPropertiesForType();
		for (int j = 0; j < spd.size(); j++)
		{
			int k = j + 1;
			String newStr;
			if (k > 9)
				newStr = "" + k;
			else
				newStr = "0" + k;
			KeyNode knode = new KeyNode(newStr, spd.get(j));
			fqTree.stack.add(knode);
		}
		String faStr = "00";
		ASTNode faNode = ast.createInstance(ASTNode.SIMPLE_NAME); // 随便取得类型
		KeyNode knode = new KeyNode(faStr, faNode);
		fqTree.stack.add(knode);
		apiStr = api;
		
	}
	
	/*
	 * 将用位置序号表示的input集还原成AstMapTree的集合
	 * @fqInput：input序号集
	 * @everyInput：input集
	 * 
	 * @返回值：input转换成的AstMapTree集合
	 */
	public List<AstMapTree> intToInput(List<Integer> fqInput, List<AstMapTree> everyinput){
		List<AstMapTree> newInput = new ArrayList<AstMapTree>();
		for (Integer fi : fqInput)
		{
			newInput.add(everyinput.get(fi));
		}
		return newInput;
	}
	
	/*
	 * 将用位置序号表示的input集转换成新建AstMapTree的集合
	 * @fqInput：input序号集
	 * @everyInput：input集
	 * 
	 * @返回值：input转换成的AstMapTree集合
	 */
	public List<AstMapTree> intToNewInput(List<Integer> fqInput, List<AstMapTree> everyinput){
		List<AstMapTree> newInput = new ArrayList<AstMapTree>();
		for (Integer fi : fqInput)
		{
			AstMapTree ipt = everyinput.get(fi);
			AstMapTree copyIpt = copyAmt(ipt, apiStr);
			newInput.add(copyIpt);
		}
		return newInput;
	}
	
	/*
	 * 核心函数，用来进行主要的mining工作
	 * @fq_k：待mining的树的集合
	 */
	public void apriori_gen(List<AstMapTree> fq_k){
		for (int amtc = 0; amtc < fq_k.size(); amtc++)
		{
			System.out.print(amtc);
			System.out.print(" ");
			System.out.println(fq_k.size());
			
			AstMapTree amt = fq_k.get(amtc);
			while (amt.count < amt.stack.size())
			{
				Object ob = amt.stack.get(amt.count).node;
				String str = amt.stack.get(amt.count).key;
				boolean fg = amt.stack.get(amt.count).flag;
				String parentStr = str.substring(0, str.length() - 2);
				String subStr = str.substring(str.length() - 2, str.length());
				int sonStr = Integer.parseInt(subStr);
				if (ob instanceof ChildListPropertyDescriptor && fg == false) //如果该节点为list节点，且节点中无已存在的节点
				{
					List<List<Integer>> fqInput = new ArrayList<List<Integer>>();
					List<List<ASTNode>> newNodeSeqList = frequent_List(str, amt.input, fqInput); 
					
					if (newNodeSeqList.size() == 0)
						amt.count++;
					for (int c = 0; c < newNodeSeqList.size(); c++)
					{
						AstMapTree amTree;
						if (c == newNodeSeqList.size() - 1)
							amTree = amt;
						else
						{
							amTree = copyAmt(amt, apiStr);
							fq_k.add(amTree);
						}
						ASTNode parentNode = amTree.getNode(parentStr);
						List<ASTNode> newNodeSeq = newNodeSeqList.get(c);
						List<AstMapTree> newInput = intToNewInput(fqInput.get(c), amTree.input);
						fqSequence.setInputPosi(newNodeSeq, newInput, parentStr, sonStr - 1, str);
						ChildListPropertyDescriptor cpd = (ChildListPropertyDescriptor)((parentNode.structuralPropertiesForType()).get(sonStr - 1));
						
						List<ASTNode> existedSeq = (List<ASTNode>)parentNode.getStructuralProperty(cpd);
						
						existedSeq.removeAll(existedSeq);
						existedSeq.addAll(newNodeSeq);
						for (int j = 0; j < existedSeq.size(); j++)
						{
							char ch = (char)(j + 65);
							ASTNode nextNode = existedSeq.get(j);
							List<StructuralPropertyDescriptor> nextSpd = nextNode.structuralPropertiesForType(); 
							amTree.setPosi(nextNode, str + "0" + ch);
							for (int k = 0; k < nextSpd.size(); k++)
							{
								int h = k + 1;
								String newStr;
								if (h > 9)
									newStr = str + "0" + ch + h;
								else
									newStr = str + "0" + ch + "0" + h;
								KeyNode knode = new KeyNode(newStr, nextSpd.get(k));
								amTree.stack.add(knode);
							}
						}
						amTree.count = amTree.count + 1;
						amTree.setInput(newInput);
					}
				}
				else if (ob instanceof ChildListPropertyDescriptor && fg == true) //如果该节点为list节点，且节点中有已存在的节点
				{
					List<List<Integer>> fqInput = new ArrayList<List<Integer>>();
					List<List<Integer>> fqInput1 = new ArrayList<List<Integer>>();
					List<List<ASTNode>> newNodeSeqList = new ArrayList<List<ASTNode>>();
					List<List<ASTNode>> newNodeSeqList1 = new ArrayList<List<ASTNode>>();
					frequent_List1(str, amt.input, fqInput, fqInput1, newNodeSeqList, newNodeSeqList1);
					
//					if (newNodeSeqList.size() == 0 && newNodeSeqList1.size() == 0)
//						amt.count++;
					//newNodeSeqList.add(new ArrayList<ASTNode>());
					//newNodeSeqList1.add(new ArrayList<ASTNode>());
					if (newNodeSeqList.size() == 0 && newNodeSeqList1.size() == 0)
					{
						newNodeSeqList.add(new ArrayList<ASTNode>());
						newNodeSeqList1.add(new ArrayList<ASTNode>());
					}
					else if (newNodeSeqList.size() == 0)
						newNodeSeqList.add(new ArrayList<ASTNode>());
					else if (newNodeSeqList1.size() == 0)
						newNodeSeqList1.add(new ArrayList<ASTNode>());
					for (int c = 0; c < newNodeSeqList.size(); c++)
					{
						List<ASTNode> newNodeSeq = newNodeSeqList.get(c);
						
						for (int d = 0; d < newNodeSeqList1.size(); d++)
						{
							AstMapTree amTree;
							if (c == newNodeSeqList.size() - 1 && d == newNodeSeqList1.size() - 1)
								amTree = amt;
							else
							{
								amTree = copyAmt(amt, apiStr);
								fq_k.add(amTree);
							}
							ASTNode parentNode = amTree.getNode(parentStr);
							List<ASTNode> newNodeSeq1 = newNodeSeqList1.get(d);
							List<AstMapTree> newInput;
							List<Integer> input1, input2;
							if (newNodeSeq.size() == 0 && newNodeSeq1.size() == 0)
							{
								newInput = new ArrayList<AstMapTree>();
								newInput.addAll(amt.input);
							}
							else if (newNodeSeq.size() ==  0)
							{
								input2 = fqInput1.get(d);
								newInput = intToNewInput(input2, amTree.input);
							}
							else if (newNodeSeq1.size() == 0)
							{
								input1 = fqInput.get(c);
								newInput = intToNewInput(input1, amTree.input);
							}
							else
							{
								input1 = fqInput.get(c);
								input2 = fqInput1.get(d);
								List<Integer> inputcopy = new ArrayList<Integer>();
								inputcopy.addAll(input1);
								inputcopy.retainAll(input2);
								if (inputcopy.size() <= MINSUP)
								{
									amTree.count = amTree.count + 1;
									continue;
								}
								newInput = intToNewInput(inputcopy, amTree.input);
							}
							String existedStr = parentStr.substring(0, parentStr.length() - 2);
							fqSequence.setInputPosi1(newNodeSeq, newNodeSeq1, newInput, parentStr, sonStr - 1, str, existedStr);
							ChildListPropertyDescriptor cpd = (ChildListPropertyDescriptor)((parentNode.structuralPropertiesForType()).get(sonStr - 1));
							
							List<ASTNode> existedSeq = (List<ASTNode>)parentNode.getStructuralProperty(cpd);
							
							ASTNode existedNode = amTree.getNode(existedStr);

							existedSeq.removeAll(existedSeq);
							List<ASTNode> newList = copyList(newNodeSeq);
							existedSeq.addAll(newList);
							existedSeq.add(existedNode);
							List<ASTNode> newList1 = copyList(newNodeSeq1);
							existedSeq.addAll(newList1);
							for (int j = 0; j < existedSeq.size(); j++)
							{
								char ch = (char)(j + 65);
								ASTNode nextNode = existedSeq.get(j);
								if (nextNode == existedNode)
									continue;
								List<StructuralPropertyDescriptor> nextSpd = nextNode.structuralPropertiesForType(); 
								amTree.setPosi(nextNode, str + "0" + ch);
								for (int k = 0; k < nextSpd.size(); k++)
								{
									int h = k + 1;
									String newStr;
									if (h > 9)
										newStr = str +  "0" + ch + h;
									else
										newStr = str + "0" + ch + "0" + h;
									KeyNode knode = new KeyNode(newStr, nextSpd.get(k));
									amTree.stack.add(knode);
								}
							}
							amTree.count = amTree.count + 1;
							amTree.setInput(newInput);
						}
					}
					
				}
				else if (ob instanceof ChildPropertyDescriptor) // 如果该节点为Node节点
				{
					List<List<Integer>> fqInput = new ArrayList<List<Integer>>();
					List<ASTNode> newNodeList = frequent_Child(str, amt.input, fqInput);
					
					
					if (newNodeList.size() == 0)
						amt.count++;
					for (int c = 0; c < newNodeList.size(); c++)
					{
						AstMapTree amTree;
						if (c == newNodeList.size() - 1)
							amTree = amt;
						else
						{
							amTree = copyAmt(amt, apiStr);
							fq_k.add(amTree);
						}
						ASTNode parentNode = amTree.getNode(parentStr);
						ASTNode newNode = newNodeList.get(c);
						
						List<AstMapTree> newInput = intToInput(fqInput.get(c), amTree.input);
						
						ChildPropertyDescriptor cpd = (ChildPropertyDescriptor)((parentNode.structuralPropertiesForType()).get(sonStr - 1));
						parentNode.setStructuralProperty(cpd, newNode);
						List<StructuralPropertyDescriptor> nextSpd = newNode.structuralPropertiesForType();
						amTree.setPosi(newNode, str);
						for (int j = 0; j < nextSpd.size(); j++)
						{
							int k = j + 1;
							String newStr;
							if (k > 9)
								newStr = str + k;
							else
								newStr = str + "0" + k;
							KeyNode knode = new KeyNode(newStr, nextSpd.get(j));
							amTree.stack.add(knode);
						}
						amTree.count = amTree.count + 1;
						amTree.setInput(newInput);
					}
				}
				else if (ob instanceof SimplePropertyDescriptor) //如果该节点为Object节点
				{
					List<List<Integer>> fqInput = new ArrayList<List<Integer>>();
					List<Object> newObList = frequent_Object(str, amt.input, fqInput);
					
					if (newObList.size() == 0)
						amt.count++;
					for (int c = 0; c < newObList.size(); c++)
					{
						AstMapTree amTree;
						if (c == newObList.size() - 1)
							amTree = amt;
						else
						{
							amTree = copyAmt(amt, apiStr);
							fq_k.add(amTree);
						}
						ASTNode parentNode = amTree.getNode(parentStr);
						Object newOb = newObList.get(c);
						
						List<AstMapTree> newInput = intToInput(fqInput.get(c), amTree.input);
						
						SimplePropertyDescriptor cpd = (SimplePropertyDescriptor)((parentNode.structuralPropertiesForType()).get(sonStr - 1));
						
						parentNode.setStructuralProperty(cpd, newOb);
						amTree.count = amTree.count + 1;
						amTree.setInput(newInput);
					}
				}
				else if (ob instanceof ASTNode) //如果该节点为father节点
				{
					List<List<Integer>> fqInput = new ArrayList<List<Integer>>();
					List<TypePosi> newNodeInfoList = frequent_Father(str, amt.input, fqInput);
					
					if (newNodeInfoList.size() == 0)
						amt.count++;
					for (int c = 0; c < newNodeInfoList.size(); c++)
					{
						AstMapTree amTree;
						if (c == newNodeInfoList.size() - 1)
							amTree = amt;
						else
						{
							amTree = copyAmt(amt, apiStr);
							fq_k.add(amTree);
						}
						
						TypePosi newNodeInfo = newNodeInfoList.get(c);
						List<AstMapTree> newInput = intToInput(fqInput.get(c), amTree.input);
						ASTNode parentNode = amTree.getNode(parentStr);
						
						int newNodeType = newNodeInfo.type;
						int newNodePosi = newNodeInfo.posi;
						ASTNode newNode = ast.createInstance(newNodeType);
						List<StructuralPropertyDescriptor> spd = newNode.structuralPropertiesForType();
						Object s = newNode.getStructuralProperty(spd.get(newNodePosi));
						
						if (spd.get(newNodePosi) instanceof ChildListPropertyDescriptor)
						{
							List<ASTNode> nodeList = (List<ASTNode>)newNode.getStructuralProperty(spd.get(newNodePosi));
							nodeList.add(parentNode);
						}
						else if (spd.get(newNodePosi) instanceof ChildPropertyDescriptor)
						{
							ASTNode nodeList = (ASTNode)s;
							newNode.setStructuralProperty(spd.get(newNodePosi), parentNode);
						}
						amTree.setPosi(newNode, str);
						
						List<StructuralPropertyDescriptor> nextSpd = newNode.structuralPropertiesForType();
						for (int j = 0; j < nextSpd.size(); j++)
						{
							if (j == newNodePosi && nextSpd.get(j) instanceof ChildPropertyDescriptor)
								continue;
							else if (j == newNodePosi && nextSpd.get(j) instanceof ChildListPropertyDescriptor)
							{
								int k = j + 1;
								String newStr;
								if (k > 9)
									newStr = str + k;
								else
									newStr = str + "0" + k;
								KeyNode knode = new KeyNode(newStr, nextSpd.get(j));
								knode.flag = true;
								amTree.stack.add(knode);
							}
							else
							{
								int k = j + 1;
								String newStr;
								if (k > 9)
									newStr = str + k;
								else
									newStr = str + "0" + k;
								KeyNode knode = new KeyNode(newStr, nextSpd.get(j));
								amTree.stack.add(knode);
							}
						}
						if (newNodeType != 	ASTNode.METHOD_DECLARATION)
						{
							KeyNode newKnode = new KeyNode(str + "00", ast.createInstance(ASTNode.SIMPLE_NAME));
							amTree.stack.add(newKnode);
						}
						amTree.count = amTree.count + 1;
						amTree.setInput(newInput);
					}
				}
				
			}
		}
	}
	
	/*
	 * 复制列表
	 * @nodeList：输入的列表
	 * 
	 * @返回值：复制的列表
	 */
	public List<ASTNode> copyList(List<ASTNode> nodeList){
		List<ASTNode> newList = new ArrayList<ASTNode>();
		for (ASTNode node : nodeList)
		{
			int type = node.getNodeType();
			ASTNode newNode = ast.createInstance(type);
			newList.add(newNode);
		}
		return newList;
	}
	
	/*
	 * 复制一颗AstMapTree
	 * @amt：待复制的AstMapTree
	 * @api：输入的api字符串（其实已经定义成全局变量，这里没有改。。。）
	 * 
	 * @返回值：复制好的AstMapTree
	 */
	public AstMapTree copyAmt(AstMapTree amt, String api){
		amt.root = amt.root.getRoot();
		ASTNode node = ASTNode.copySubtree(ast, amt.root.getRoot());
		AstMapTree amTree = new AstMapTree(node, api);
		amTree.stack.addAll(amt.stack);
		amTree.count = amt.count;
		amTree.setInput(amt.input);
		for (int i = 1; i < amt.key.size(); i++)
		{
			String str = amt.key.get(i);
			char ch = str.charAt(str.length() - 1);
			String parentStr, sonStr;
			boolean flag;
			if (ch < '0' || ch > '9')
			{
				parentStr = str.substring(0, str.length() - 4);
				sonStr = str.substring(str.length() - 4, str.length());
				flag = true;
			}
			else
			{
				parentStr = str.substring(0, str.length() - 2);
				sonStr = str.substring(str.length() - 2, str.length());
				flag = false;
			}
			ASTNode parentNode = amTree.getNode(parentStr);
			if (flag)
			{
				String ch1 = sonStr.substring(0, 2);
				char ch2 = sonStr.charAt(3);
				List<StructuralPropertyDescriptor> spd = parentNode.structuralPropertiesForType();
				int i1 = Integer.parseInt(ch1);
				int i2 = ch2 - 'A';
				if (spd.get(i1 - 1) instanceof ChildListPropertyDescriptor)
				{
					List<ASTNode> nodeList = (List<ASTNode>)parentNode.getStructuralProperty(spd.get(i1 - 1));
					ASTNode oldNode = amt.getNode(str);
					ASTNode oldParent = amt.getNode(parentStr);
					List<StructuralPropertyDescriptor> oldSpd = oldParent.structuralPropertiesForType();
					List<ASTNode> oldNodeList = (List<ASTNode>)oldParent.getStructuralProperty(spd.get(i1 - 1));
					int ind = oldNodeList.indexOf(oldNode);
					amTree.setPosi(nodeList.get(ind), str);
				}
				else
				{
					System.out.println("error");
					return null;
				}
			}
			else
			{
				int i1 = Integer.parseInt(sonStr);
				List<StructuralPropertyDescriptor> spd = parentNode.structuralPropertiesForType();
				if (i1 > 0)
				{
					ASTNode nodeSon = (ASTNode)parentNode.getStructuralProperty(spd.get(i1 - 1));
					amTree.setPosi(nodeSon, str);
				}
				else
				{
					ASTNode nodeFa = parentNode.getParent();
					amTree.setPosi(nodeFa, str);
				}
			}
		}
		return amTree;
	}
	
	/*
	 * 用于频繁列表的挖掘
	 * @str：当前需要挖掘的节点的路径字符串
	 * @input：input集
	 * @fqInput：用来输出每一个频繁项对应的input集
	 * 
	 * @返回值：所有频繁列表的集合
	 */
	public List<List<ASTNode>> frequent_List(String str, List<AstMapTree> input, List<List<Integer>> fqInput){
		List<List<Integer>> nodeSeq = new ArrayList<List<Integer>>();
		 
		
		String parentStr = str.substring(0, str.length() - 2);
		String sonStr = str.substring(str.length() - 2, str.length());
		int index = Integer.parseInt(sonStr) - 1;
		for (AstMapTree amt : input)
		{
			ASTNode parentNode = amt.getNode(parentStr);
			if (parentNode != null)
			{
				List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
				if (parentSpd.size() > index)
				{
					List<Integer> nodeTypeSeq = new ArrayList<Integer>();
					List<ASTNode> sonNode = (List<ASTNode>)parentNode.getStructuralProperty(parentSpd.get(index));
					for (int j = 0; j < sonNode.size(); j++)
					{
						ASTNode node = sonNode.get(j);
						int type = node.getNodeType();
						nodeTypeSeq.add(type);
					}
					nodeSeq.add(nodeTypeSeq);
				}
			}
		}
		List<List<Integer>> fqSeq = fqSequence.frequent_Sequence(nodeSeq);
		
		List<List<ASTNode>> resultNode = new ArrayList<List<ASTNode>>();
		for (int i = 0; i < fqSeq.size(); i++)
		{
			List<Integer> resultInt = fqSeq.get(i);
			List<Integer> fqTemp = new ArrayList<Integer>();
			fqSequence.searchInput(resultInt, nodeSeq, input, parentStr, index, str, fqTemp);
			List<ASTNode> result = new ArrayList<ASTNode>();
			for (Integer type : resultInt)
			{
				ASTNode newNode = ast.createInstance(type);
				result.add(newNode);
			}
			resultNode.add(result);
			fqInput.add(fqTemp);
		}
		return resultNode;
	}
	
	/*
	 * 用于列表中已存在节点的频繁列表的挖掘
	 * @str：当前需要挖掘的节点的路径字符串
	 * @input：input集
	 * @fqInput：用来输出每一个前一段频繁列表项对应的input集
	 * @fqInput1：用来输出每一个后一段频繁列表项对应的input集
	 * @resultNode：所有前一段频繁列表项的集合
	 * @resultNode1：所有后一段频繁列表项的集合
	 */
	public void frequent_List1(String str, List<AstMapTree> input, List<List<Integer>> fqInput, List<List<Integer>> fqInput1, List<List<ASTNode>> resultNode, List<List<ASTNode>> resultNode1){
		List<List<Integer>> nodeSeq = new ArrayList<List<Integer>>();
		List<List<Integer>> nodeSeq1 = new ArrayList<List<Integer>>();
			
		String parentStr = str.substring(0, str.length() - 2);
		String sonStr = str.substring(str.length() - 2, str.length());
		String existedStr = parentStr.substring(0, parentStr.length() - 2);
		int index = Integer.parseInt(sonStr) - 1;
		for (AstMapTree amt : input)
		{
			ASTNode parentNode = amt.getNode(parentStr);
			ASTNode existedNode = amt.getNode(existedStr);
			if (parentNode != null)
			{
				List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
				if (parentSpd.size() > index)
				{
					List<Integer> nodeTypeSeq = new ArrayList<Integer>();
					List<Integer> nodeTypeSeq1 = new ArrayList<Integer>();
					List<ASTNode> sonNode = (List<ASTNode>)parentNode.getStructuralProperty(parentSpd.get(index));
					int divide = sonNode.indexOf(existedNode);
					for (int j = 0; j < divide; j++)
					{
						ASTNode node = sonNode.get(j);
						int type = node.getNodeType();
						nodeTypeSeq.add(type);
					}
					for (int k = divide + 1; k < sonNode.size(); k++)
					{
						ASTNode node = sonNode.get(k);
						int type = node.getNodeType();
						nodeTypeSeq1.add(type);
					}
					nodeSeq.add(nodeTypeSeq);
					nodeSeq1.add(nodeTypeSeq1);
				}
			}
		}
		List<List<Integer>> fqSeq = fqSequence.frequent_Sequence(nodeSeq);
		List<List<Integer>> fqSeq1 = fqSequence.frequent_Sequence(nodeSeq1);
		
		for (int i = 0; i < fqSeq.size(); i++)
		{
			List<Integer> resultInt = fqSeq.get(i);
			List<Integer> fqTemp = new ArrayList<Integer>(); 
			fqSequence.searchInput(resultInt, nodeSeq, input, parentStr, index, str, fqTemp);
			List<ASTNode> result = new ArrayList<ASTNode>();
			for (Integer type : resultInt)
			{
				ASTNode newNode = ast.createInstance(type);
				result.add(newNode);
			}
			resultNode.add(result);
			fqInput.add(fqTemp);
		}
		for (int i = 0; i < fqSeq1.size(); i++)
		{
			List<Integer> resultInt = fqSeq1.get(i);
			List<Integer> fqTemp1 = new ArrayList<Integer>();
			fqSequence.searchInput(resultInt, nodeSeq1, input, parentStr, index, str, fqTemp1);
			List<ASTNode> result = new ArrayList<ASTNode>();
			for (Integer type : resultInt)
			{
				ASTNode newNode = ast.createInstance(type);
				result.add(newNode);
			}
			resultNode1.add(result);
			fqInput1.add(fqTemp1);
		}
	}
	
	/*
	 * 用于频繁Node节点的挖掘
	 * @str：当前需要挖掘的节点的路径字符串
	 * @input：input集
	 * @fqInput：用来输出每一个频繁项对应的input集
	 * 
	 * @返回值：所有频繁项的集合
	 */
	public List<ASTNode> frequent_Child(String str, List<AstMapTree> input, List<List<Integer>> fqInput){
		List<Integer> fqNode = new ArrayList<Integer>();
		List<Integer> fqCount = new ArrayList<Integer>();
		List<List<Integer>> fqTemp = new ArrayList<List<Integer>>();
		
		String parentStr = str.substring(0, str.length() - 2);
		String sonStr = str.substring(str.length() - 2, str.length());
		int index = Integer.parseInt(sonStr) - 1;
		for (int i = 0; i < input.size(); i++)
		{
			AstMapTree amt = input.get(i);
			ASTNode parentNode = amt.getNode(parentStr); 
			if (parentNode != null)
			{
				List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
				if (parentSpd.size() > index)
				{
					if (parentSpd.get(index) instanceof ChildPropertyDescriptor)
					{
						ASTNode sonNode = (ASTNode)parentNode.getStructuralProperty(parentSpd.get(index));
						// （会不会不是该节点类型）
						if (sonNode != null)
						{
							Integer nodeType = sonNode.getNodeType();
							countFqChild(fqNode, fqCount, fqTemp, nodeType, i);
							amt.setPosi(sonNode, str);
						}
					}
				}
			}
		}
		List<ASTNode> resultNode = new ArrayList<ASTNode>();
		for (int i = 0; i < fqCount.size(); i++)
		{
			if (fqCount.get(i) > MINSUP)
			{
				ASTNode newNode = ast.createInstance(fqNode.get(i));
				resultNode.add(newNode);
				fqInput.add(fqTemp.get(i));
			}
		}
		return resultNode;
	}
	
	/*
	 * 用于频繁Object节点的挖掘
	 * @str：当前需要挖掘的节点的路径字符串
	 * @input：input集
	 * @fqInput：用来输出每一个频繁项对应的input集
	 * 
	 * @返回值：所有频繁项的集合
	 */
	public List<Object> frequent_Object(String str, List<AstMapTree> input, List<List<Integer>> fqInput){
		List<Object> fqNode = new ArrayList<Object>();
		List<Integer> fqCount = new ArrayList<Integer>();
		List<List<Integer>> fqTemp = new ArrayList<List<Integer>>();
		
		String parentStr = str.substring(0, str.length() - 2);
		String sonStr = str.substring(str.length() - 2, str.length());
		int index = Integer.parseInt(sonStr) - 1;
		for (int i = 0; i < input.size(); i++)
		{
			AstMapTree amt = input.get(i);
			ASTNode parentNode = amt.getNode(parentStr);
			if (parentNode != null)
			{
				List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
				if (parentSpd.size() > index)
				{
					Object sonOb = parentNode.getStructuralProperty(parentSpd.get(index));
					// （会不会不是该节点类型）
					countFqObject(fqNode, fqCount, fqTemp, sonOb, i);
				}
			}
		}

		List<Object> resultObject = new ArrayList<Object>();
		for (int i = 0; i < fqCount.size(); i++)
		{
			if (fqCount.get(i) > MINSUP)
			{
				resultObject.add(fqNode.get(i));
				fqInput.add(fqTemp.get(i));
			}
		}
		return resultObject;
	}
	
	/*
	 * 用于频繁father节点的挖掘
	 * @str：当前需要挖掘的节点的路径字符串
	 * @input：input集
	 * @fqInput：用来输出每一个频繁项对应的input集
	 * 
	 * @返回值：所有频繁项的集合
	 */
	public List<TypePosi> frequent_Father(String str, List<AstMapTree> input, List<List<Integer>> fqInput){
		List<TypePosi> fqNode = new ArrayList<TypePosi>();
		List<Integer> fqCount = new ArrayList<Integer>();
		List<List<Integer>> fqTemp = new ArrayList<List<Integer>>();
		
		String sonStr = str.substring(0, str.length() - 2);
		for (int j = 0; j < input.size(); j++)
		{
			AstMapTree amt = input.get(j);
			ASTNode sonNode = amt.getNode(sonStr);
			ASTNode parentNode = sonNode.getParent();
			if (parentNode == null)
				continue;
			List<StructuralPropertyDescriptor> spd = parentNode.structuralPropertiesForType();
			int nodeType = parentNode.getNodeType();
			for (int i = 0; i < spd.size(); i++)
			{
				StructuralPropertyDescriptor s = spd.get(i);
				if (s instanceof ChildListPropertyDescriptor)
				{
					List<ASTNode> nodeList = (List<ASTNode>)parentNode.getStructuralProperty(s);
					if (nodeList.contains(sonNode))
					{
						TypePosi tp = new TypePosi(nodeType, i);
						countFqChildList(fqNode, fqCount, fqTemp, tp, j);
						amt.setPosi(parentNode, str);
						break;
					}
				}
				else if (s instanceof ChildPropertyDescriptor)
				{
					ASTNode node = (ASTNode)parentNode.getStructuralProperty(s);
					if (node == sonNode)
					{
						TypePosi tp = new TypePosi(nodeType, i);
						countFqChildList(fqNode, fqCount, fqTemp, tp, j);
						amt.setPosi(parentNode, str);
						break;
					}
				}
			}
		}

		List<TypePosi> resultTp = new ArrayList<TypePosi>();
		for (int i = 0; i < fqCount.size(); i++)
		{
			if (fqCount.get(i) > MINSUP)
			{
				resultTp.add(fqNode.get(i));
				fqInput.add(fqTemp.get(i));
			}
		}
		return resultTp;
	}
	
	/*
	 * 频繁Node节点的计数过程
	 * @fqNode：已记录的所有Node节点类型
	 * @fqCount：已记录的所有Node节点类型的次数
	 * @fqInput：input集
	 * @key：当前需要记录的Node节点类型
	 * @inputct：当前需要记录的Node节点类型对应的input
	 */
	public void countFqChild(List<Integer> fqNode, List<Integer> fqCount, List<List<Integer>> fqInput, Integer key, Integer inputct){ 
		if (fqNode.contains(key))
		{
			int i = fqNode.indexOf(key);
			fqCount.set(i, fqCount.get(i)+1);
			fqInput.get(i).add(inputct);
		}
		else
		{
			fqNode.add(key);
			fqCount.add(1);
			List<Integer> newInput = new ArrayList<Integer>();
			newInput.add(inputct);
			fqInput.add(newInput);
		}
	}
	
	/*
	 * 频繁father节点的计数过程（这个函数的命名有点问题。。。）
	 * @fqNode：已记录的所有father节点信息
	 * @fqCount：已记录的所有father节点信息的次数
	 * @fqInput：input集
	 * @key：当前需要记录的father节点信息
	 * @inputct：当前需要记录的father节点信息对应的input
	 */
	public void countFqChildList(List<TypePosi> fqNode, List<Integer> fqCount, List<List<Integer>> fqInput, TypePosi key, Integer inputct){
		int i = -1;
		for (int k = 0; k < fqNode.size(); k++)
		{
			if (fqNode.get(k).type == key.type && fqNode.get(k).posi == key.posi)
			{
				i = k;
				break;
			}	
		}
		if (i > -1)
		{
			fqCount.set(i, fqCount.get(i)+1);
			fqInput.get(i).add(inputct);
		}
		else
		{
			fqNode.add(key);
			fqCount.add(1);
			List<Integer> newInput = new ArrayList<Integer>();
			newInput.add(inputct);
			fqInput.add(newInput);
		}
	}
	
	/*
	 * 频繁Object节点的计数过程
	 * @fqNode：已记录的所有Object节点
	 * @fqCount：已记录的所有Object节点的次数
	 * @fqInput：input集
	 * @key：当前需要记录的Object节点
	 * @inputct：当前需要记录的Object节点对应的input
	 */
	public void countFqObject(List<Object> fqNode, List<Integer> fqCount, List<List<Integer>>fqInput, Object key, Integer inputct){
		if (fqNode.contains(key))
		{
			int i = fqNode.indexOf(key);
			fqCount.set(i, fqCount.get(i)+1);
			fqInput.get(i).add(inputct);
		}
		else
		{
			fqNode.add(key);
			fqCount.add(1);
			List<Integer> newInput = new ArrayList<Integer>();
			newInput.add(inputct);
			fqInput.add(newInput);
		}
	}
	
	/*
	 * 核心函数的调用以及频繁项的输出过程
	 */
	public void findFrequentTree(){
		List<AstMapTree> amt = new ArrayList<AstMapTree>();
		amt.add(fqTree);
		fqTree.setInput(allInput);
		apriori_gen(amt);
		
		List<String> allApi = new ArrayList<String>();
		allApi.add("listFiles");
		allApi.add("exists");
		allApi.add("isDirectory");
		//allApi.add("isDirectory");
		ResultScreen rs = new ResultScreen(allApi);
		
		for (AstMapTree a : amt)
		{
			a.root = a.root.getRoot();
			if (rs.screen(a.root))
			{
				System.out.println(a.root.getRoot());
				System.out.println("=================");
			}
		}
		System.out.println();
	}
	
	/*
	 * 程序主入口，在这里进行相关输入
	 */
	public static void main(String[] args)
	{
		String srcPath = "E://test//input//delete files and folders in a directory//code"; // java文件所在的文件夹
		String api = "delete"; //api字符串
		List<ASTNode> nodeList = AstProd.getAstTrees(srcPath);
		Generation g = new Generation();
		g.init(nodeList, api);
		g.findFrequentTree();
		System.out.println("enough");
	}

}
