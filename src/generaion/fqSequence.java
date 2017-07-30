/*
 * 主要是用来处理列表的频繁挖掘的类
 */
package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class fqSequence {

	public static final int MINSUP = 5; // 最小支持度
	
	/*
	 * 核心算法函数，用于生成频繁项
	 * @nodeList：输入的List集合
	 * 
	 * @返回值：频繁的List集合
	 */
	public static List<List<Integer>> frequent_Sequence(List<List<Integer>> nodeList){
		List<Integer> fq_1 = new ArrayList<Integer>();
		List<Integer> fq_count = new ArrayList<Integer>();
		List<Integer> fq_1node = new ArrayList<Integer>();
		List<List<Integer>> fq_nnode = new ArrayList<List<Integer>>();
		List<List<Integer>> newNodeList = new ArrayList<List<Integer>>();
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		List<List<Integer>> finalResult = new ArrayList<List<Integer>>();
		int count = 0;
		int loop = 1;
		
		for (List<Integer> list : nodeList)
		{
			for (Integer node : list)
			{
				countFqChild(fq_1, fq_count, node);
			}
		}
		for (int i = 0; i < fq_1.size(); i ++)
		{
			if (fq_count.get(i) > MINSUP)
			{
				List<Integer> fq_node = new ArrayList<Integer>();
				fq_node.add(fq_1.get(i));
				fq_nnode.add(fq_node);
			}
		} //生成频繁1项集
		result.addAll(fq_nnode);
		//finalResult.addAll(result);
		
		do {
			finalResult.addAll(result);
			fq_nnode.removeAll(fq_nnode);
			fq_nnode.addAll(result);
			result.removeAll(result);
			newNodeList.removeAll(newNodeList);
			count = fq_nnode.size();
			for (int i = 0; i < count; i++)
			{
				List<Integer> c1 = fq_nnode.get(i);
//				for (int j = 0; j < fq_1.size(); j++)
//				{
//					Integer c2 = fq_1.get(j);
//					for (int k = 0; k <= c1.size(); k++)
//					{
//						if (k < c1.size() && c2 == c1.get(k))
//							continue;
//						List<Integer> newlist = new ArrayList<Integer>();
//						newlist.addAll(c1);
//						newlist.add(k, c2);
//						boolean flag = false;
//						for (List<Integer> oldList : newNodeList)
//						{
//							for (int l = 0; l < oldList.size(); l++)
//							{
//								if (newlist.get(l) != oldList.get(l))
//									break;
//								else if (l == oldList.size() - 1 && newlist.get(l) == oldList.get(l))
//									flag = true;
//							}
//							if (flag == true)
//								break;
//						}
//						if (flag == false)
//							newNodeList.add(newlist);
//					}
//				}
				
				
				for (int j = i ; j < count; j++)
				{
					List<Integer> c2 = fq_nnode.get(j);
					
					List<Integer> copyList1 = new ArrayList<Integer>();
					List<Integer> copyList2 = new ArrayList<Integer>();
					copyList1.addAll(c1.subList(0, c1.size() - 1));
					copyList2.addAll(c2.subList(1, c2.size()));
					
					boolean flag = true;
					for (int k = 0; k < copyList1.size(); k++)
					{
						if (copyList1.get(k) != copyList2.get(k))
						{
							flag = false;
							break;
						}
					}
					if (flag == false)
						continue;
					List<Integer> newlist = new ArrayList<Integer>();
					newlist.add(c1.get(0));
					newlist.addAll(c2);
					newNodeList.add(newlist);
				}
			}
			result = frequentSearch(nodeList, newNodeList);
			loop++;
		} while(result.size() > 0);
		
		return finalResult;
	}
	
	/*
	 * 从候选集中搜索频繁List项集
	 * @input：所有input集
	 * @newNodeList：候选项集
	 * 
	 * @返回值：频繁项集
	 */
	public static List<List<Integer>> frequentSearch(List<List<Integer>> input, List<List<Integer>> newNodeList) {
		List<Integer> fqCount = new ArrayList<Integer>();
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for (int i = 0; i < newNodeList.size(); i++)
		{
			fqCount.add(0);
		}
		for (int i = 0; i < newNodeList.size(); i++)
		{
			List<Integer> nodeList = newNodeList.get(i);
			for (List<Integer> parentList : input)
			{
//				boolean flag = true;
//				int start = -1;
//				for (Integer nodenum : nodeList)
//				{
//					int posi = parentList.indexOf(nodenum);
//					if (posi > start)
//						start = posi;
//					else
//					{
//						flag = false;
//						break;
//					}
//				}
				int cnt1 = 0, cnt2 = 0;
				while(cnt1 < parentList.size() && cnt2 < nodeList.size())
				{
					if (parentList.get(cnt1) == nodeList.get(cnt2))
					{
						cnt1++;
						cnt2++;
					}
					else
					{
						cnt1++;
					}
				}
				if (cnt2 == nodeList.size())
					fqCount.set(i, fqCount.get(i)+1);
			}
		}
		for (int i = 0; i < newNodeList.size(); i++)
		{
			if (fqCount.get(i) > MINSUP)
				result.add(newNodeList.get(i));
		}
		return result;
	}
	
	
	/*
	 * 频繁1项集的计数过程
	 * @fqNode：已记录的所有Node节点类型
	 * @fqCount：已记录的所有Node节点类型的次数
	 * @key：当前需要记录的Node节点类型
	 */
	public static void countFqChild(List<Integer> fqNode, List<Integer> fqCount, Integer key){ 
		if (fqNode.contains(key))
		{
			int i = fqNode.indexOf(key);
			fqCount.set(i, fqCount.get(i)+1);
		}
		else
		{
			fqNode.add(key);
			fqCount.add(1);
		}
	}
	
	/*
	 * 对于一个频繁List项集搜索input集
	 */
	public static void searchInput(List<Integer> result, List<List<Integer>> nodeSeq, List<AstMapTree> amTree, String parentStr, int index, String str, List<Integer> fqInput){
		for (int i = 0; i < nodeSeq.size(); i++)
		{
			List<Integer> seq = nodeSeq.get(i);
			int cnt1 = 0, cnt2 = 0;
			while(cnt1 < seq.size() && cnt2 < result.size())
			{
				if (seq.get(cnt1) == result.get(cnt2))
				{
					cnt1++;
					cnt2++;
				}
				else
				{
					cnt1++;
				}
			}
			if (cnt2 == result.size())
			{
				fqInput.add(i);
//				int cnt3 = 0, cnt4 = 0;
//				AstMapTree amt = amTree.get(i);
//				ASTNode parentNode = amt.getNode(parentStr);
//				List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
//				List<ASTNode> sonNode = (List<ASTNode>)parentNode.getStructuralProperty(parentSpd.get(index));
//				while(cnt3 < seq.size() && cnt4 < result.size())
//				{
//					if (seq.get(cnt3) == result.get(cnt4))
//					{
//						char ch = (char)(65 + cnt4);
//						amt.setPosi(sonNode.get(cnt3), str + ch);
//						cnt3++;
//						cnt4++;
//					}
//					else
//					{
//						cnt3++;
//					}
//				}
			}
		}
	}
	
	/*
	 * 对于原List中无已存在的节点的频繁List项集设置input集的对应路径
	 */
	public static void setInputPosi(List<ASTNode> result, List<AstMapTree> input, String parentStr, int index, String str){
		for (AstMapTree amt : input)
		{
			int cnt3 = 0, cnt4 = 0;
			ASTNode parentNode = amt.getNode(parentStr);
			List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
			List<ASTNode> sonNode = (List<ASTNode>)parentNode.getStructuralProperty(parentSpd.get(index));
			while(cnt3 < sonNode.size() && cnt4 < result.size())
			{
				if (sonNode.get(cnt3).getNodeType() == result.get(cnt4).getNodeType())
				{
					char ch = (char)(65 + cnt4);
					amt.setPosi(sonNode.get(cnt3), str + "0" + ch);
					cnt3++;
					cnt4++;
				}
				else
				{
					cnt3++;
				}
			}
		}
	}
	
	/*
	 * 对于原List中有已存在的节点的频繁List项集设置input集的对应路径
	 */
	public static void setInputPosi1(List<ASTNode> result, List<ASTNode> result1, List<AstMapTree> input, String parentStr, int index, String str, String existedStr){
		for (AstMapTree amt : input)
		{
			ASTNode parentNode = amt.getNode(parentStr);
			ASTNode existedNode = amt.getNode(existedStr);
			List<StructuralPropertyDescriptor> parentSpd = parentNode.structuralPropertiesForType();
			List<ASTNode> sonNode = (List<ASTNode>)parentNode.getStructuralProperty(parentSpd.get(index));
			int posi = sonNode.indexOf(existedNode);
			int cnt3 = 0, cnt4 = 0;
			while(cnt3 < posi && cnt4 < result.size())
			{
				if (sonNode.get(cnt3).getNodeType() == result.get(cnt4).getNodeType())
				{
					char ch = (char)(65 + cnt4);
					amt.setPosi(sonNode.get(cnt3), str + "0" + ch);
					cnt3++;
					cnt4++;
				}
				else
				{
					cnt3++;
				}
			}
			cnt3 = posi + 1;
			cnt4 = 0;
			int cnt = result.size() + 1;
			while(cnt3 < sonNode.size() && cnt4 < result1.size())
			{
				if (sonNode.get(cnt3).getNodeType() == result1.get(cnt4).getNodeType())
				{
					
					char ch = (char)(65 + cnt4 + cnt);
					amt.setPosi(sonNode.get(cnt3), str + "0" + ch);
					cnt3++;
					cnt4++;
				}
				else
				{
					cnt3++;
				}
			}
		}
	}
}
