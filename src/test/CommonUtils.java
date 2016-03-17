package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 聚类通用工具集
 */
public class CommonUtils {
	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */
	public static ArrayList<float[]> setDataFileSet(String srcFile,float threshold) throws FileNotFoundException, IOException {
		ArrayList<float[]> dataSet=new ArrayList<float[]>(); 
		try
	    {			
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));
			String in;
			while((in=bf.readLine()) != null){
				int x,y;
				float ecio;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
				y = Integer.parseInt(token[1]);//纬度栅格ID
				ecio = Float.parseFloat(token[2]);//话单质量ecio
				if(ecio<threshold){
					dataSet.add(new float[]{x,y}); 
				}
	        }	
			System.out.println(dataSet.size());
			bf.close();			
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
		return dataSet;	
	}
	public static ArrayList<float[]> setDataFileSetNew(String srcFile,float threshold) throws FileNotFoundException, IOException {
		ArrayList<float[]> dataSet=new ArrayList<float[]>(); 
		try
	    {			
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));
			String in;
			while((in=bf.readLine()) != null){
				int x,y;
				float ecio;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
				y = Integer.parseInt(token[1]);//纬度栅格ID
				ecio = Float.parseFloat(token[2]);//话单质量ecio
				if(ecio<threshold){
					dataSet.add(new float[]{x,y,ecio}); 
				}
	        }	
			System.out.println("超过threshold："+dataSet.size());
			bf.close();			
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
		return dataSet;	
	}
	public static HashMap<String, String> setCallsHm(String srcFile) throws FileNotFoundException, IOException {
		HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据	
		try
	    {			
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));
			String in;
			while((in=bf.readLine()) != null){
				int x,y;
				String calls;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
				y = Integer.parseInt(token[1]);//纬度栅格ID
				calls = token[2];//话单质量ecio		
				hm.put(token[0]+"^"+token[1],calls); 
	        }	
			bf.close();			
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
		return hm;	
	}
	public static HashMap<String, Float> setGridData(String srcFile) throws FileNotFoundException, IOException {	
		HashMap<String, Float> hm = new HashMap<String, Float>();
		try
	    {				
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));
			String in;
			while((in=bf.readLine()) != null){
//				GridData gridData = new GridData();
				int x,y;
				float ecio;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
				y = Integer.parseInt(token[1]);//纬度栅格ID
				ecio = Float.valueOf(token[2]);//话单质量ecio		
				String coordinate = x+"^"+y;
				hm.put(coordinate,ecio);
//				System.out.println(coordinate);
	        }	
//			for (Map.Entry<String, Float> entry : hm.entrySet()) {  	  
//			    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  		  
//			} 
			bf.close();			
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }	
		return hm;
	}

	/**
	 * 求两点误差平方的方法
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	public static  float errorSquare(float[] element, float[] center) {
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		float errSquare = x * x + y * y;
		return errSquare;
	}
	
	public static  float errorCube(float[] element, float[] center) {
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		float z = element[2] - center[2];
		float errorCube = x * x + y * y + z * z;
		return errorCube;
	}
	
	/**
	 * 求两点误差平方的方法
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	public static  float euraDist(float[] element, float[] center) {
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		double errSquare = x * x + y * y;
		float dist = (float) Math.sqrt(errSquare);
		return dist;
	}

	/**
	 * 打印数据，测试用
	 * 
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 * @throws IOException 
	 */
	public static void printDataArray(String destDir,ArrayList<ArrayList<float[]>> cluster) throws IOException {
		String in;
//		ArrayList<ArrayList<float[]>> cluster=this.getCluster();  
		BufferedWriter bw = new BufferedWriter(new FileWriter(destDir));//打开文件写
		float[] b = TransformCoordinate.SortGridID("src/initial/grid123.txt");
		for(int i=0;i<cluster.size();i++){  
			ArrayList<float[]> dataArray = cluster.get(i);
			for (int j = 0; j < dataArray.size(); j++) {	 
				in= dataArray.get(j)[0] + " " + dataArray.get(j)[1]+" "+b[i]+" "+i;
				bw.write(in+"\r\n");
			}
      	} 		
		bw.close();
	}
	public static void printGridData(String destDir,HashMap<String,Float> hm) throws IOException {
		File writename = new File(destDir); // 处理后的一点以及两三点的所有数据
        writename.createNewFile(); // 创建新文件  
        PrintWriter out = new PrintWriter(new FileWriter(writename));
		Set set = hm.entrySet();
		Iterator iterator = set.iterator();
        while(iterator.hasNext()){ //既含有两三点话单质量，又含有一点定位话单质量的栅格。
	    	  Map.Entry Point = (Map.Entry)iterator.next();
		      String point = (String) Point.getKey();
		      Float ecio = (Float)Point.getValue();
		      int t  = point.indexOf("^");
		      int leth = point.length();
		      int x = Integer.valueOf(point.substring(0,t));
		      int y = Integer.valueOf(point.substring(t+1,leth));
		      out.println(x+" "+y+" "+ecio);
        }
//		for (Map.Entry<String, Float> entry : hm.entrySet()) {  	  
//		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  		  
//		} 
        out.close(); 
	}
	public static int CallStatistic(HashMap<String, String> hm, int[] center, int radius) { 
//		System.out.println("CallStatistic()");	 
		int x = center[0];
		int y = center[1];
		int count = 0;
		int num = 0;	
		for(int m=x-radius;m<x+radius+1;m++){//读取以该栅格为中心49个栅格的数据			
			if(m<0){continue;}
 		   	for(int n=y-radius;n<y+radius+1;n++){
 		   	   if(n<0){continue;}
     		   float distance = euraDist(new float[]{x,y}, new float[]{m,n});
     		   if(distance>radius){
     			   continue;
     		   }
 			   String p = m+"^"+n;
// 			  System.out.println(p);
 			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
 				  num += Float.valueOf(hm.get(p));
 				  count++;
 			   }   
 		   	}
     	 }
//		 System.out.println(count);
		 int average = num / count;
		 return average;		
	}
	public static int PointStatistic(ArrayList<float[]> dataSet, int[] center, int radius) { 
//		System.out.println("CallStatistic()");
		HashMap<String, Integer> hm = new HashMap<String, Integer>();//hm 用来原始数据
		int x = center[0];
		int y = center[1];
//		 System.out.println("size"+dataSet.size()+" "+x+","+y);
		for(int i=0;i<dataSet.size();i++){
			int p = (int) dataSet.get(i)[0];
			int q = (int) dataSet.get(i)[1];
			hm.put(p+"^"+q,1);
		}		
		int count = 0;
		int num = 0;	
		for(int m=x-radius;m<x+radius+1;m++){//读取以该栅格为中心49个栅格的数据			
			if(m<0){continue;}
 		   	for(int n=y-radius;n<y+radius+1;n++){
 		   	   if(n<0){continue;}
     		   float distance = euraDist(new float[]{x,y}, new float[]{m,n});
     		   if(distance>radius){
     			   continue;
     		   }
 			   String p = m+"^"+n;
// 			  System.out.println(p);
 			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
 				  num += Float.valueOf(hm.get(p));
 				  count++;
 			   }   
 		   	}
     	 }
//		 System.out.println("c:"+count);
//		 System.out.println("n:"+num);
		 return count;		
	}
	public static String ReadJsonFile(String path) {  
        File file = new File(path);  
        BufferedReader reader = null;  
        String laststr = ""; 
        try {  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            while ((tempString = reader.readLine()) != null) {  
                laststr = laststr + tempString;  
            }
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }   
        return laststr;  
    } 
}