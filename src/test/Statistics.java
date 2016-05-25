package test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics {

	public static void execute(String srcFile) throws FileNotFoundException, IOException {
		 System.out.println("Statistics.execute()");
		 BufferedReader in = new BufferedReader(new FileReader(srcFile));
		 BufferedReader in1 = new BufferedReader(new FileReader("src/initial/grid123Call.txt"));
		 HashMap<String, String> hm1 = new HashMap<String, String>();//hm 
		 
		 File writename1 = new File("src/initial/gridJointFind.txt"); // 处理后的一点以及两三点的所有数据
	     writename1.createNewFile(); // 创建新文件  
	     PrintWriter out1 = new PrintWriter(new FileWriter(writename1));
		 String str,str1;
	   	 while((str1 = in1.readLine())!=null){
	   		  String[]  tokens1 = str1.split(" ");
//	   		System.out.println(tokens1[2]);
	   		  hm1.put(tokens1[0]+"^"+tokens1[1],tokens1[2]);//将一点数据的栅格，经纬度、ecio载入hm1中
	   	 }
	   	 int a[] = {0,0,0,0,0,0,0,0};
	   	 int b[] = {0,0,0,0,0,0};
	   	 int num=0;
	   	 while((str = in.readLine())!=null){
	   		 String[]  tokens = str.split(" ");
	         int x = Integer.parseInt(tokens[0]);//经度栅格ID
			 int y = Integer.parseInt(tokens[1]);//纬度栅格ID
	   		 float ecio = Float.valueOf(tokens[2]);
//	   		 System.out.println(tokens[2]);
	   		 if(ecio>-8){a[0]++;}
//	   		 else if(ecio>-7){a[1]++;}
//	   		 else if(ecio>-8){a[2]++;}
	   		 else if(ecio>-9){a[3]++;}
	   		 else if(ecio>-10){a[4]++;}
	   		 else if(ecio>-11){a[5]++;}
	   		 else if(ecio>-12){a[6]++;}
	   		 else {
	   			 a[7]++;
	   			 String point = x+"^"+y;
//	   			 System.out.println(hm1.get(point));
	   			 int calls = (int) Math.floor(Float.valueOf(hm1.get(point)));
//	   			 System.out.println(calls);
	   			 if(calls<10){b[0]++;}
	   			 else if(calls<25){b[1]++;}
	   			 else if(calls<50){b[2]++;}
	   			 else if(calls<100){b[3]++;}
	   			 else if(calls<200){b[4]++;}
	   			 else {b[5]++;}
	   			 if(calls>=10){
	   				out1.println(x+" "+y+" "+-1602);
	   				num++;
	   			 }
	   		 }
	   	  }
	   	in.close();
	   	in1.close();
	   	out1.close();
	   	GenerateMapPNG.DrawPNG("src/initial/gridJointFind.txt","src/transform/grid123ID.txt","src/draw/gridJointFind.png");
	   	for(int i=0;i<a.length;i++){
	   		 System.out.println("a"+i+":"+a[i]);
	   	}
	   	for(int i=0;i<b.length;i++){
	   		 System.out.println("b"+i+":"+b[i]);
	   	}
	    System.out.println("num:"+num);
	}
	public static void pointClassify() throws FileNotFoundException, IOException {
		System.out.println("pointClassify()");	
		try{    
			BufferedReader bf = new BufferedReader(new FileReader("src/initial/gridJointFind.txt"));//联合统计得到的点
			BufferedReader bf1 = new BufferedReader(new FileReader("src/initial/station.txt"));//基站数据
			PrintWriter out1 = new PrintWriter(new FileWriter("src/initial/pointClassify.txt"));
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据			

			String in;
			String in1;
			int radius=30;
			int count=0;
			while((in1=bf1.readLine()) != null){
	            String[] token = in1.split(" ");
			 	hm.put(token[0]+"^"+token[1],token[2]); //将基站的经纬度、标记值载入hm中
            }
			while((in=bf.readLine()) != null){
				int x,y;
	            String[] token = in.split(" ");
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
    			
    			//判断每个点在distance距离范围内是否存在基站
    			//每存在一个基站，则标记值+1，无基站-1602，有一个基站-1601，有两个基站-1600
    			//基站每有一个点，则标记值-1/stationCount 
    			int n = CommonUtils.StationStatistic(hm, new int[] {x,y}, radius);	  
    			if(n!=0)count ++;
    			int m = -1602 + n;
    			out1.println(x+" "+y+" "+m);
			}
		   	bf.close();
		   	bf1.close();
		   	out1.close();
		   	System.out.println(count);
			GenerateMapPNG.DrawPNG("src/initial/pointClassify.txt","src/transform/grid123ID.txt","src/draw/pointClassify.png");
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }		
	}
	public static void stationSort() throws FileNotFoundException, IOException {
		System.out.println("stationSort()");	
		try{    
			BufferedReader bf = new BufferedReader(new FileReader("src/initial/pointClassify.txt"));//联合统计得到的点
			BufferedReader bf1 = new BufferedReader(new FileReader("src/initial/stationSift.txt"));//基站数据
			PrintWriter out1 = new PrintWriter(new FileWriter("src/initial/stationSort.txt"));
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据
			List<StationData> list = new ArrayList<StationData>();//产生容器对象

			String in;
			String in1;
			int radius=30;
			float count=0;
			while((in=bf.readLine()) != null){
	            String[] token = in.split(" ");
			 	hm.put(token[0]+"^"+token[1],token[2]); //将基站的经纬度、标记值载入hm中
            }
			while((in1=bf1.readLine()) != null){
				int x,y;
	            String[] token = in1.split(" ");
	            StationData stationData = new StationData();//定义栅格对象.
	            x = Integer.parseInt(token[0]);//经度栅格ID
    			y = Integer.parseInt(token[1]);//纬度栅格ID
    			
    			//判断每个点在distance距离范围内是否存在基站
    			//每存在一个基站，则标记值+1，无基站-1602，有一个基站-1601，有两个基站-1600
    			//基站每有一个点，则标记值-1/stationCount 
    			float n = CommonUtils.PointStatistic(hm, new int[] {x,y}, radius);	
    			count += n;
    			float m = -1603 - n;
                stationData.setX(x);
                stationData.setY(y);
                stationData.setValue(m);
                list.add(stationData);//存入容器.
//    			out1.println(x+" "+y+" "+m);
			}
            Collections.sort(list);//排序
            for(StationData stationData:list){
            	out1.println(stationData.toString());
            }
		   	bf.close();
		   	bf1.close();
		   	out1.close();
		    System.out.println(count);
			GenerateMapPNG.DrawPNG("src/initial/stationSort.txt","src/transform/grid123ID.txt","src/draw/stationSort.png");
		}catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }		
	}
	public static void gridGapAmount(String srcFile) throws FileNotFoundException, IOException {
   		System.out.println("gridGapAmount()");	  
		BufferedReader bf = new BufferedReader(new FileReader(srcFile));
		HashMap<String, String> hm = new HashMap<String, String>();

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
		 	hm.put(token[0]+"^"+token[1],token[2]); 
        }
		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
		System.out.println("yMax:"+yMax+",yMin:"+yMin);	 
		int[] yMinArray=new int[xMax+1],yMaxArray=new int[xMax+1];
		for(int i=xMin;i<=xMax;i++){
			yMinArray[i] = 100000;
			yMaxArray[i] = 0;
		}
		bf.close();
		bf = new BufferedReader(new FileReader(srcFile));
		while((in=bf.readLine()) != null){
			int x,y;
            String[] token = in.split(" ");
            x = Integer.parseInt(token[0]);//经度栅格ID
			y = Integer.parseInt(token[1]);//纬度栅格ID
			if(yMaxArray[x] <= y){yMaxArray[x] = y;}
//    			System.out.println("1111");	
//    			System.out.println("1111");	
//    			System.out.println("1111");	
			if(yMinArray[x] > y){yMinArray[x] = y;}
		}
		File writename1 = new File("src/initial/gridBorder.txt"); // 处理后的一点以及两三点的所有数据
        writename1.createNewFile(); // 创建新文件  
        PrintWriter out1 = new PrintWriter(new FileWriter(writename1));
        
	 	int m=0,n=0;
		for(int i=xMin;i<=xMax;i++){
//			out1.println(i+" "+(yMinArray[i]+1)+" "+-16);
//			out1.println(i+" "+(yMaxArray[i]+0)+" "+-6);
			for(int j=yMin;j<=yMax;j++){
				if(hm.containsKey(i+"^"+j)) {
					m++;
				}
				else{
					n++;
				}
			}
		}
        bf.close();
        out1.close(); 
		
		System.out.println("GridValue:"+m);	
		System.out.println("NoGridValue:"+n);
//		GenerateMapPNG.DrawPNG("src/initial/gridBorder.txt","src/transform/grid123ID.txt","src/draw/gridBorder.png");
	} 
	public static void noisePointAmount(String srcFile) throws FileNotFoundException, IOException {
   		System.out.println("noisePointAmount()");	  
   		BufferedReader bf = new BufferedReader(new FileReader(srcFile));
		HashMap<String, String> hm = new HashMap<String, String>();

		String in;
		int xMax=0,yMax=0;
		int xMin=1000000,yMin=1000000;
//		float coefficient=0.7f;
		int parameter = 7;
		int para = (parameter-1)/2;
		
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
//						if(m<xMin||m>xMax){continue;}
	         		   	for(int n=j-para;n<j+para+1;n++){
//	         		   	   if(n<yMin||n>yMax){continue;}
	         			   String p = m+"^"+n;
	         			   //System.out.println(p);
	         			   if(hm.containsKey(p)){//该栅格周围有两三点的数据存在
	         				  float ecioTemp = Float.parseFloat(hm.get(p)); 
	         				  num++;//得到两三点存在的栅格个数
	         				  ecioSum = ecioSum + ecioTemp; //对两三点的数据进行求和
	         			   }   
	         		   	}
		         	 }
//					if(num>9){
//						float ecioNew = (ecioSum/num)*(1-coefficient)+ecio*coefficient;
////						System.out.println(i+","+j+" "+ecioNew);
//						gridData.setEcio(ecioNew);
//			            gridData.setLongID(i);
//			            gridData.setLatID(j);
//			            bw.write(gridData.toString()+"\r\n");
//					}
				}						
			}
		}
        bf.close();
		System.out.println("xMax:"+xMax+",xMin:"+xMin);	
		System.out.println("yMax:"+yMax+",yMin:"+yMin);
	}
	public static void statisticFinal(String srcFile) throws FileNotFoundException, IOException {
		 System.out.println("Statistics.statisticFinal()");
	   	 System.out.println(srcFile);
		 BufferedReader in = new BufferedReader(new FileReader(srcFile));
		 BufferedReader in1 = new BufferedReader(new FileReader("src/process/grid123CallProcess.txt"));
		 HashMap<String, String> hm1 = new HashMap<String, String>();//hm 
		 String str,str1;
	   	 while((str1 = in1.readLine())!=null){
	   		  String[]  tokens1 = str1.split(" ");
	   		  hm1.put(tokens1[0]+"^"+tokens1[1],tokens1[2]);//将一点数据的栅格，经纬度、ecio载入hm1中
	   	 }
	   	 int a[] = {0,0,0,0,0,0,0,0};
	   	 int b[] = {0,0,0,0,0,0};
	   	 int num=0;
	   	 while((str = in.readLine())!=null){
	   		 String[]  tokens = str.split(" ");
	         int x = Integer.parseInt(tokens[0]);//经度栅格ID
			 int y = Integer.parseInt(tokens[1]);//纬度栅格ID
	   		 float ecio = Float.valueOf(tokens[2]);
	   		 String point = x+"^"+y;
   			 int calls = (int) Math.floor(Float.valueOf(hm1.get(point)));
//	   		 System.out.println(tokens[2]);
   			 if(calls>0){}
   			 else{System.out.println("!!!  "+calls);}
	   		 if(ecio>-8){a[0]+=calls;}
//	   		 else if(ecio>-7){a[1]++;}
//	   		 else if(ecio>-8){a[2]++;}
	   		 else if(ecio>-9){a[3]+=calls;}
	   		 else if(ecio>-10){a[4]+=calls;}
	   		 else if(ecio>-11){a[5]+=calls;}
	   		 else if(ecio>-12){a[6]+=calls;}
	   		 else {
	   			 a[7]+=calls;
	   		 }
	   		 num++;
	   	  }
	   	in.close();
	   	in1.close();
	   	for(int i=0;i<a.length;i++){
	   		 System.out.println("a"+i+":"+a[i]);
	   	}
	    System.out.println("num:"+num);
	}
}
class StationData implements Comparable<StationData>{
    public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	private int x;    //x
    private int y;	   //y
    private float value;	   //value
    public StationData(){};

    public String toString(){
        return x + " " + y + " " + new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_UP);
    }
	public int compareTo(StationData other){
    	if(other.value>this.value){
    		return 1;
    	}
    	else if (other.value<this.value){
    		return -1;
    	}else{
    		return 0;
    	}
    }
}