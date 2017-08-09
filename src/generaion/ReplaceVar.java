package generaion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class ReplaceVar {
	public static void replaceVariable(List<TypeName> userInput, List<TypeName> existedVar){
		for (TypeName tn : userInput)
		{
			String iptType = tn.type;
			int minValue = 99;
			String result = null;
			for (TypeName ext : existedVar)
			{
				if (ext.type.equals(iptType))
				{
					int dis = getEditDistance(tn.name, ext.name);
					if (dis < minValue)
					{
						minValue = dis;
						result = ext.name;
					}
				}
			}
			//TODO
			if (result == null)
			{
				System.out.println("\"" + tn.name + "\"" + "has no appropriate replacement!");
			}
			else
			{
				System.out.println("\"" + tn.name + "\" ---> " + result);
			}
		}
	}
	
	private static int Minimum(int a, int b, int c) {  
        int im =  a<b ? a : b;  
        return im<c ? im : c;  
    }  
     
    public static int getEditDistance(String s, String t) {  
        int d[][]; // matrix  
        int n; // length of s  
        int m; // length of t  
        int i; // iterates through s  
        int j; // iterates through t  
        char s_i; // ith character of s  
        char t_j; // jth character of t  
        int cost; // cost  
          
        n = s.length();  
        m = t.length();  
        if (n == 0) {  
            return m;  
        }  
        if (m == 0) {  
            return n;  
        }  
        d = new int[n + 1][m + 1];  
           
        for (i = 0; i <= n; i++) {  
            d[i][0] = i;  
        }  
        for (j = 0; j <= m; j++) {  
            d[0][j] = j;  
        }  
          
        for (i = 1; i <= n; i++) {  
            s_i = s.charAt(i - 1);   
            for (j = 1; j <= m; j++) {  
                t_j = t.charAt(j - 1);  
                cost = (s_i == t_j || Character.toUpperCase(s_i) == Character.toUpperCase(t_j) || Character.toLowerCase(s_i) == Character.toLowerCase(t_j)) ? 0 : 1;  
                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,  
                        d[i - 1][j - 1] + cost);  
            }  
        }  
        return d[n][m];  
    } 
	
}
