package generaion;

import java.util.ArrayList;
import java.util.List;
import java.io.File; 
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.jdt.core.dom.*;

public class AstProd {

	public static List<ASTNode> getAstTrees(String dirpath){
		List<ASTNode> result = new ArrayList<ASTNode>();
		File file=new File(dirpath);
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++)
		{
			File f = fileList[i];
			String path = f.getAbsolutePath();
			ASTNode node = AstProd.getAstTree(path);
			if (node != null)
				result.add(node);
		}
		return result;
	}
	
	public static ASTNode getAstTree(String path){
		byte[] input = null;  
        try {  
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));  
            input = new byte[bufferedInputStream.available()];  
                bufferedInputStream.read(input);  
                bufferedInputStream.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
        
        ASTParser astP = ASTParser.newParser(AST.JLS8);
        astP.setSource(new String(input).toCharArray());  
        astP.setKind(ASTParser.K_STATEMENTS);
        
        ASTNode result = (ASTNode) (astP.createAST(null));
        
        return result;
	}
	
}
