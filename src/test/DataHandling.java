package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DataHandling {
	public static void DataProcess(String srcFile1, String srcFile2, String destDir) { //数据融合函数
		System.out.println("DataProcess()");	
		HashMap<String, String> hm = new HashMap<String, String>();//hm 用来处理二三点数据
		HashMap<String, String> hm1 = new HashMap<String, String>();//hm1 用来处理一点数据
	    try
	    {
	   	  BufferedReader in = new BufferedReader(new FileReader(srcFile2));//文件路径为二三点数据路径，已排除基站相离及相含的情况
	   	  BufferedReader in1 = new BufferedReader(new FileReader(srcFile1));//此文件路径为一点数据路径
	   	  String str;
	   	  String str1;
	   	  while((str = in.readLine())!=null){
	   		  String[]  tokens = str.split("\t");
	   		  hm.put(tokens[0],tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[4]); //将二三点数据的栅格，经纬度、ecio载入hm中
	   	  }
	   	  while((str1 = in1.readLine())!=null){
	   		  String[]  tokens1 = str1.split("\t");
	   		  hm1.put(tokens1[0],tokens1[1]+","+tokens1[2]+","+tokens1[3]+","+tokens1[4]);//将一点数据的栅格，经纬度、ecio载入hm1中
	   	  }
	   	  in.close();
	   	  in1.close();
	     }catch(IOException ioe)
	     {
	        ioe.printStackTrace(); 
	     }

	     Set set = hm.entrySet();
	     Set set1 = hm1.entrySet();
	     Iterator iterator = set.iterator();
	     Iterator iterator1 = set1.iterator();
	     
	     try{     
	         File writename1 = new File(destDir); // 处理后的一点以及两三点的所有数据
	         writename1.createNewFile(); // 创建新文件  
	         PrintWriter out1 = new PrintWriter(new FileWriter(writename1));
	       
	         while(iterator1.hasNext()){ //既含有两三点话单质量，又含有一点定位话单质量的栅格。
		    	  Map.Entry Point1 = (Map.Entry)iterator1.next();
			      String point1 = (String) Point1.getKey();
			      String cellinfo1 = (String) Point1.getValue();
		          // System.out.println(point1+" "+cellinfo1);
			      int t  = point1.indexOf("^");
			      int leth = point1.length();
			      int x = Integer.valueOf(point1.substring(0,t));
			      int y = Integer.valueOf(point1.substring(t+1,leth));
			      float stren =Float.valueOf(cellinfo1.split(",")[2]) ;
			        // System.out.println(point+" "+stren);
			      if(hm.containsKey(point1)){  //一点与两三点数据同时出现在同一栅格。取1倍的两三点话单质量，0倍的一点话单质量
			    	  String cellinfo2 = (String)hm.get(point1);
			    	  float streng = Float.valueOf(cellinfo2.split(",")[2]) ;
			    	  float stre = streng; 
//			    	  float stre = (float) (streng*(0.9)+stren*(0.1)); //取两三点话单质量占90%，一点话单质量占10%进行加权求和。
			    	  out1.println(x+" "+y+" "+stre);
			    	  // out2.println(point1+"\t"+stre);
			      }
			     else if(!(hm.containsKey(point1))){//某栅格仅有一点数据时的情况
			    	 // System.out.println("found");
			    	  int line = 0;
			    	  float streng1=0;
			    	  for(int i = x-3;i<x+4;i++){//读取以该栅格为中心49个栅格的数据
		         		   for(int j = y-3;j<y+4;j++){
		         			   String p = i+"^"+j;
		         			   //System.out.println(p);
		         			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
		         				  //System.out.println("ok");
		         				  String cellinfo2 = (String)hm.get(p); 
		         				  float streng = Float.valueOf(cellinfo2.split(",")[2]) ;
		         				  line++;//得到两三点存在的栅格个数
		         				  streng1 = streng1+streng; //对两三点的数据进行求和
		         				  //System.out.println(streng1); 
		         			   }   
		         		   }
		         	   }
			    	  //System.out.println(streng1);
		         	  float strength = streng1/line ; //对之前两三点求和得到的数据求平均
		         	  if(line==0){
		         		  out1.println(x+" "+y+" "+stren);
		         	  }
		         	  else if(line<9){ //若只存在一点数据的栅格周围仅有不超过9个存在两三点数据的话单，
		         		  float strength1 = (float) (strength*(0.7)+stren*(0.3));//两三点数据占70%，该栅格的一点数据占30%进行加权求和。
		         		  out1.println(x+" "+y+" "+strength1);
//		         			 System.out.println("000:"+strength+" "+line);
		         	  }
		         	  else{
		         		  out1.println(x+" "+y+" "+strength);//取平均ecio
		         	  }
			      }
		     }
	       
	         while(iterator.hasNext()){//某栅格仅存在两三点数据,直接取两三点的ecio,不对其进行变化。
		    	  Map.Entry Point = (Map.Entry)iterator.next();
			      String point = (String) Point.getKey();
			      int t  = point.indexOf("^");
			      int leth = point.length();
			      int x = Integer.valueOf(point.substring(0,t));
			      int y = Integer.valueOf(point.substring(t+1,leth));
			      String cellinfo = (String) Point.getValue();
			      float stren1 =Float.valueOf(cellinfo.split(",")[2]) ;
			      if(!(hm1.containsKey(point))){
			    	  out1.println(x+" "+y+" "+stren1);
			      }
	         }
	         out1.close(); 
	     }catch (FileNotFoundException e) {  
	 	    e.printStackTrace();  
	 	  } catch (IOException e) {  
	 	    e.printStackTrace();   
	 	  }		  
	}
	public static void DataAdd(String srcFile, String processDir) { 
		System.out.println("DataAdd()");	
		try{    
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//文件路径为二三点数据路径，已排除基站相离及相含的情况
			BufferedWriter bw = new BufferedWriter(new FileWriter(processDir));//打开文件写
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据

			String in;
			int xMax=0,yMax=0;
			int xMin=1000000,yMin=1000000;
			
			while((in=bf.readLine()) != null){
				int x,y;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
	            
	            if(xMax <= x){xMax = x;}
	            if(yMax <= y){yMax = y;}
	            if(xMin > x){xMin = x;}
	            if(yMin > y){yMin = y;}
			 	hm.put(token[0]+"^"+token[1],token[2]); //将二三点数据的栅格，经纬度、ecio载入hm中
            }
			GridData gridData = new GridData();//定义栅格对象.
		 	int i,j;
			for(i=xMin;i<=xMax;i++){
				for(j=yMin;j<=yMax;j++){
					if(hm.containsKey(i+"^"+j)) {
		               gridData.setEcio(Float.parseFloat(hm.get(i+"^"+j)));
		               gridData.setLongID(i);
		               gridData.setLatID(j);
		               bw.write(gridData.toString()+"\r\n");
					}
					else{
						float ecioSum = 0;
						int num = 0;
						int parameter = 7;
						int para = (parameter-1)/2;
						for(int m=i-para;m<i+para+1;m++){//读取以该栅格为中心49个栅格的数据
							if(m<xMin||m>xMax){continue;}
		         		   	for(int n=j-para;n<j+para+1;n++){
		         		   	   if(n<yMin||n>yMax){continue;}
		         			   String p = m+"^"+n;
		         			   //System.out.println(p);
		         			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
		         				  float ecioTemp = Float.parseFloat(hm.get(p)); 
		         				  num++;//得到两三点存在的栅格个数
		         				  ecioSum = ecioSum + ecioTemp; //对两三点的数据进行求和
		         			   }   
		         		   	}
			         	 }
						if(num>9){
							float ecioNew = ecioSum/num;
//							System.out.println(i+","+j+" "+ecioNew);
							gridData.setEcio(ecioNew);
				            gridData.setLongID(i);
				            gridData.setLatID(j);
				            bw.write(gridData.toString()+"\r\n");
						}
					}
				}
			}
            bf.close();
            bw.close();
    		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
    		System.out.println("yMax:"+yMax+",yMin:"+yMin);	
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }	
	}
	public static void DataSmooth(String srcFile, String processDir) { 
		System.out.println("DataSmooth()");	
		try{    
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//文件路径为二三点数据路径，已排除基站相离及相含的情况
			BufferedWriter bw = new BufferedWriter(new FileWriter(processDir));//打开文件写
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据

			String in;
			int xMax=0,yMax=0;
			int xMin=1000000,yMin=1000000;
			float coefficient=0.7f;//参考比例
			int parameter = 7;
			int para = (parameter-1)/2;
			int gap=0,noGap=0;
			
			while((in=bf.readLine()) != null){
				int x,y;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
	            
	            if(xMax <= x){xMax = x;}
	            if(yMax <= y){yMax = y;}
	            if(xMin > x){xMin = x;}
	            if(yMin > y){yMin = y;}
			 	hm.put(token[0]+"^"+token[1],token[2]); //将二三点数据的栅格，经纬度、ecio载入hm中
            }
			GridData gridData = new GridData();//定义栅格对象.
		 	int i,j;
			for(i=xMin;i<=xMax;i++){
				for(j=yMin;j<=yMax;j++){
					if(hm.containsKey(i+"^"+j)) {
						float ecio = Float.parseFloat(hm.get(i+"^"+j)); 
						float ecioSum = 0;
						int num = 0;
						for(int m=i-para;m<i+para+1;m++){//读取以该栅格为中心49个栅格的数据
//							if(m<xMin||m>xMax){continue;}
		         		   	for(int n=j-para;n<j+para+1;n++){
//		         		   	   if(n<yMin||n>yMax){continue;}
		         			   String p = m+"^"+n;
		         			   //System.out.println(p);
		         			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
		         				  float ecioTemp = Float.parseFloat(hm.get(p)); 
		         				  num++;//得到两三点存在的栅格个数
		         				  ecioSum = ecioSum + ecioTemp; //对两三点的数据进行求和
		         			   }   
		         		   	}
			         	 }
						float ecioAverage = ecioSum/num;
						float ecioGap = Math.abs(ecioAverage - ecio) ;

						if(ecioGap>3){
							float ecioNew = ecioAverage*(coefficient)+ecio*(1-coefficient);
//							System.out.println(i+","+j+" "+ecioNew);
							gridData.setEcio(ecioNew);
				            gridData.setLongID(i);
				            gridData.setLatID(j);
				            bw.write(gridData.toString()+"\r\n");
				            gap++;
						}
						else{
							gridData.setEcio(ecio);
				            gridData.setLongID(i);
				            gridData.setLatID(j);
				            bw.write(gridData.toString()+"\r\n");
							noGap++;
						}
					}						
				}
			}
            bf.close();
            bw.close();
    		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
    		System.out.println("yMax:"+yMax+",yMin:"+yMin);
    		System.out.println("Gap>4:"+gap);
    		System.out.println("Gap<4:"+noGap);
//    		System.out.println("yMax:"+yMax+",yMin:"+yMin);
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }	
	}
	//将之前的信号质量融合变成话务量融合
	public static void DataCallProcess(String srcFile1, String srcFile2, String destDir) { //数据融合函数
		System.out.println("DataCallProcess()");	
		HashMap<String, String> hm = new HashMap<String, String>();//hm 用来处理二三点数据
		HashMap<String, String> hm1 = new HashMap<String, String>();//hm1 用来处理一点数据
	    try
	    {
	   	  BufferedReader in = new BufferedReader(new FileReader(srcFile2));//文件路径为二三点数据路径，已排除基站相离及相含的情况
	   	  BufferedReader in1 = new BufferedReader(new FileReader(srcFile1));//此文件路径为一点数据路径
	   	  String str;
	   	  String str1;
	   	  while((str = in.readLine())!=null){
	   		  String[]  tokens = str.split("\t");
	   		  hm.put(tokens[0],tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[4]); //将二三点数据的栅格，经纬度、ecio载入hm中
	   	  }
	   	  while((str1 = in1.readLine())!=null){
	   		  String[]  tokens1 = str1.split("\t");
	   		  hm1.put(tokens1[0],tokens1[1]+","+tokens1[2]+","+tokens1[3]+","+tokens1[4]);//将一点数据的栅格，经纬度、ecio载入hm1中
	   	  }
	   	  in.close();
	   	  in1.close();
	     }catch(IOException ioe)
	     {
	        ioe.printStackTrace(); 
	     }

	     Set set = hm.entrySet();
	     Set set1 = hm1.entrySet();
	     Iterator iterator = set.iterator();
	     Iterator iterator1 = set1.iterator();
	     
	     try{     
	         File writename1 = new File(destDir); // 处理后的一点以及两三点的所有数据
	         writename1.createNewFile(); // 创建新文件  
	         PrintWriter out1 = new PrintWriter(new FileWriter(writename1));
	       
	         while(iterator1.hasNext()){ //既含有两三点话单质量，又含有一点定位话单质量的栅格。
		    	  Map.Entry Point1 = (Map.Entry)iterator1.next();
			      String point1 = (String) Point1.getKey();
			      String cellinfo1 = (String) Point1.getValue();
		          // System.out.println(point1+" "+cellinfo1);
			      int t  = point1.indexOf("^");
			      int leth = point1.length();
			      int x = Integer.valueOf(point1.substring(0,t));
			      int y = Integer.valueOf(point1.substring(t+1,leth));
			      float stren =Float.valueOf(cellinfo1.split(",")[3]) ;
			        // System.out.println(point+" "+stren);
			      if(hm.containsKey(point1)){  //一点与两三点数据同时出现在同一栅格。取1倍的两三点话单质量，0倍的一点话单质量
			    	  String cellinfo2 = (String)hm.get(point1);
			    	  float streng = Float.valueOf(cellinfo2.split(",")[3]) ;
			    	  float stre = streng; 
//			    	  float stre = (float) (streng*(0.9)+stren*(0.1)); //取两三点话单质量占90%，一点话单质量占10%进行加权求和。
			    	  out1.println(x+" "+y+" "+stre);
			    	  // out2.println(point1+"\t"+stre);
			      }
			     else if(!(hm.containsKey(point1))){//某栅格仅有一点数据时的情况
			    	 // System.out.println("found");
			    	  int line = 0;
			    	  float streng1=0;
			    	  for(int i = x-3;i<x+4;i++){//读取以该栅格为中心49个栅格的数据
		         		   for(int j = y-3;j<y+4;j++){
		         			   String p = i+"^"+j;
		         			   //System.out.println(p);
		         			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
		         				  //System.out.println("ok");
		         				  String cellinfo2 = (String)hm.get(p); 
		         				  float streng = Float.valueOf(cellinfo2.split(",")[3]) ;
		         				  line++;//得到两三点存在的栅格个数
		         				  streng1 = streng1+streng; //对两三点的数据进行求和
		         				  //System.out.println(streng1); 
		         			   }   
		         		   }
		         	   }
			    	  //System.out.println(streng1);
		         	  float strength = (float)Math.floor(streng1/line); //对之前两三点求和得到的数据求平均
		         	  if(line==0){
		         		  out1.println(x+" "+y+" "+stren);
		         	  }
		         	  else if(line<9){ //若只存在一点数据的栅格周围仅有不超过9个存在两三点数据的话单，
		         		  float strength1 = (float) Math.floor(strength*(0.7)+stren*(0.3));//两三点数据占70%，该栅格的一点数据占30%进行加权求和。
		         		  out1.println(x+" "+y+" "+strength1);
//		         			 System.out.println("000:"+strength+" "+line);
		         	  }
		         	  else{
		         		  out1.println(x+" "+y+" "+strength);//取平均ecio
		         	  }
			      }
		     }
	       
	         while(iterator.hasNext()){//某栅格仅存在两三点数据,直接取两三点的ecio,不对其进行变化。
		    	  Map.Entry Point = (Map.Entry)iterator.next();
			      String point = (String) Point.getKey();
			      int t  = point.indexOf("^");
			      int leth = point.length();
			      int x = Integer.valueOf(point.substring(0,t));
			      int y = Integer.valueOf(point.substring(t+1,leth));
			      String cellinfo = (String) Point.getValue();
			      float stren1 =Float.valueOf(cellinfo.split(",")[3]) ;
			      if(!(hm1.containsKey(point))){
			    	  out1.println(x+" "+y+" "+stren1);
			      }
	         }
	         out1.close(); 
	     }catch (FileNotFoundException e) {  
	 	    e.printStackTrace();  
	 	  } catch (IOException e) {  
	 	    e.printStackTrace();   
	 	  }		  
	}
	public static ArrayList<float[]> DataCallAlert(String srcFile, int radius, int alertThreshold, String PNGDir) { 
		System.out.println("DataCallAlert()");	
		ArrayList<float[]> center = new ArrayList<float[]>();
		try{    
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//话务量数据
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据			

			String in;
			int xMax=0,yMax=0;
			int xMin=1000000,yMin=1000000;
			
			while((in=bf.readLine()) != null){
				int x,y;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
    			int callsNum = Math.round(Float.parseFloat(token[2]));
    			String s = Integer.toString(callsNum);
	            
	            if(xMax <= x){xMax = x;}
	            if(yMax <= y){yMax = y;}
	            if(xMin > x){xMin = x;}
	            if(yMin > y){yMin = y;}
			 	hm.put(token[0]+"^"+token[1],s); //将二三点数据的栅格，经纬度、ecio载入hm中
            }
			for(int i=xMin;i<=xMax;i++){
				for(int j=yMin;j<=yMax;j++){
				 	int num = CommonUtils.CallStatistic(hm, new int[] {i,j}, radius);
				 	if(num>alertThreshold){
				 		float[] temp = {i,j,num};
				 		center.add(temp);
//				 		System.out.println(i+","+j+","+num);	
				 	}
				}
			}
//			GenerateMapPNG.DrawCenter(center, "src/input/wuhanCut.png", PNGDir);
			GenerateMapPNG.DrawCenter(center, "src/draw/grid123Call.png", PNGDir);
            bf.close();
//    		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
//    		System.out.println("yMax:"+yMax+",yMin:"+yMin);		
    		
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
		return center;		
	}
	public static ArrayList<float[]> DataEcioAlert(String srcFile, int alertThreshold, String PNGDir) { 
		System.out.println("DataEcioAlert()");	
		ArrayList<float[]> center = new ArrayList<float[]>();
		try{    
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//Ec/io数据
						

			String in;
			while((in=bf.readLine()) != null){
				int x,y;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
    			int ecio = Math.round(Float.parseFloat(token[2]));
    			if(ecio<alertThreshold){
			 		float[] temp = {x,y,ecio};
			 		center.add(temp);
//			 		System.out.println(i+","+j+","+num);	
			 	}
            }
			GenerateMapPNG.DrawCenter(center, "src/input/wuhanCut.png", PNGDir);
            bf.close();
//    		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
//    		System.out.println("yMax:"+yMax+",yMin:"+yMin);		
    		
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
		return center;		
	}
}