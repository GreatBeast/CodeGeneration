package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class VarVisitor extends ASTVisitor{

	
	
	public List<TypeName> tn;
	
	public VarVisitor(){
		tn = new ArrayList<TypeName>();
	}
	
	public boolean visit(VariableDeclarationStatement node){
		List<VariableDeclarationFragment> vdf = node.fragments();
		Type tp = node.getType();
		for (VariableDeclarationFragment v : vdf)
		{
			SimpleName sName = v.getName();
			String name = sName.getIdentifier();
			tn.add(new TypeName(typeToString(tp), name));
		}
		return true;
	}
	
	public String typeToString(Type type){
		if (type.isPrimitiveType())
		{
			PrimitiveType tp = (PrimitiveType)type;
			PrimitiveType.Code pc = tp.getPrimitiveTypeCode();
			return pc.toString();
		}
		else if (type.isSimpleType())
		{
			SimpleType tp = (SimpleType)type;
			Name nm = tp.getName();
			return nm.getFullyQualifiedName();
		}
		else if (type.isArrayType())
		{
			ArrayType tp = (ArrayType)type;
			Type nextTp = tp.getComponentType();
			return typeToString(nextTp) + "[]";
		}
		else if (type.isQualifiedType())
		{
			QualifiedType tp = (QualifiedType)type;
			Type nextTp = tp.getQualifier();
			SimpleName nm = tp.getName();
			return typeToString(nextTp) + "." + nm.getIdentifier();
		}
		else if (type.isParameterizedType())
		{
			ParameterizedType tp = (ParameterizedType)type;
			Type nextTp = tp.getType();
			List<Type> tpList = tp.typeArguments();
			String rStr = typeToString(nextTp) + "<";
			for (int i = 0; i < tpList.size(); i++)
			{
				Type t = tpList.get(i);
				if (i == 0)
					rStr += typeToString(t);
				else
					rStr += "," + typeToString(t);
			}
			rStr += ">";
			return rStr;
		}
		return null;
	}
	
}
