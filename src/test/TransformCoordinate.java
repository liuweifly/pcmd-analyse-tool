package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransformCoordinate{
	public static void SiftData(String srcFile, String destDir) throws FileNotFoundException, IOException {
		System.out.println("SiftData()");
		try
	    {
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//文件路径为二三点数据路径，已排除基站相离及相含的情况
			BufferedWriter bw = new BufferedWriter(new FileWriter(destDir));//打开文件写
			
			String in;
			while((in=bf.readLine()) != null){
	         //    String[] token = in.split("\t");
        		// float longitude = Float.parseFloat(token[1]);
        		// float latitude = Float.parseFloat(token[2]);

                String[] token = in.split("\t");
                String[] tokens = new String[2];
                float ecio;     
                int x,y;
                //获得经纬度栅格ID
                int t  = token[0].indexOf("^");
                int len = token[0].length();
                tokens[0] = token[0].substring(0,t);
                tokens[1] = token[0].substring(t+1,len);
                //类型转换
                ecio = Float.parseFloat(token[3]);//话单质量ecio
                x = Integer.parseInt(tokens[0]);//经度栅格ID
                y = Integer.parseInt(tokens[1]);//纬度栅格ID

//    			if(latitude>30.4917&&latitude<30.647){
//    				if(longitude>114.3806&&longitude<114.5262){
////    					System.out.println("1");
//    					bw.write(in+"\r\n");
//    				}
//    			}
    // 			if(latitude>31.04&&latitude<31.15){
				// 	if(longitude>114.37&&longitude<114.48){
				// 		bw.write(in+"\r\n");
				// 	}
				// }
                //这是年前版本的栅格ID范围
//                if(x>=615&&x<=726){
//                    if(y>=1157&&y<=1289){
//                        bw.write(in+"\r\n");
//                    }
//                }
                //这是实验版本的栅格ID范围
                if(x>=340&&x<=447){
                    if(y>=689&&y<=785){
                        bw.write(in+"\r\n");
                    }
                }
			}
            bf.close();
            bw.close();
						
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }	
	}
	//读取融合之后的数据文件，并将栅格ID分开	
	public static void ReadFile(String srcFile, String destDir) throws FileNotFoundException, IOException {
		System.out.println("ReadFile()");
		try
	    {
			BufferedReader bf = new BufferedReader(new FileReader(srcFile));//文件路径为二三点数据路径，已排除基站相离及相含的情况
			BufferedWriter bw = new BufferedWriter(new FileWriter(destDir));//打开文件写
			
			String in;
			while((in=bf.readLine()) != null){
	            String[] token = in.split("\t");
            	String[] tokens = new String[2];
            	float ecio;		
        		int x,y;
        		//获得经纬度栅格ID
    			int t  = token[0].indexOf("^");
    			int len = token[0].length();
    			tokens[0] = token[0].substring(0,t);
    			tokens[1] = token[0].substring(t+1,len);
    			//类型转换
    			ecio = Float.parseFloat(token[3]);//话单质量ecio
    			x = Integer.parseInt(tokens[0]);//经度栅格ID
    			y = Integer.parseInt(tokens[1]);//纬度栅格ID
                GridData gridData = new GridData();//定义栅格对象.
                gridData.setEcio(ecio);
                gridData.setLongID(x);
                gridData.setLatID(y);
                bw.write(gridData.toString()+"\r\n");
            }
            bf.close();
            bw.close();			
	    }catch(IOException ioe)
	    {
	        ioe.printStackTrace(); 
	    }
	}	
	//将栅格ID进行排序
	public static float[] SortGridID(String srcFile) throws FileNotFoundException, IOException {
		System.out.println("SortGridID()");
		System.out.println(srcFile);
		float[] ecioArray = new float[7];
		try{
            BufferedReader bf = new BufferedReader(new FileReader(srcFile));//打开文件读
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/sort/grid123Sort.txt"));//打开文件写
            String in = "";
            List<GridData> list = new ArrayList<GridData>();//产生容器对象
//            in = bf.readLine();//读第一行
//            bw.write(in + "\r\n");//写第一行
            //循环一行一行读
            while((in=bf.readLine()) != null){
            	String[] token = in.split(" ");
            	float ecio;		
        		int x,y;
    			//类型转换
    			ecio = Float.valueOf(token[2]);//话单质量ecio
    			x = (int) Math.floor(Float.valueOf(token[0]));//经度栅格ID
    			y = (int) Math.floor(Float.valueOf(token[1]));//纬度栅格ID
                GridData gridData = new GridData();//定义栅格对象.
                gridData.setEcio(ecio);
                gridData.setLongID(x);
                gridData.setLatID(y);
                list.add(gridData);//存入容器.
            }
            Collections.sort(list);//排序
            int number = list.size();
            ecioArray[0] = list.get((int) (number*0.1)).getEcio();
            ecioArray[1] = list.get((int) (number*0.2)).getEcio();
            ecioArray[2] = list.get((int) (number*0.35)).getEcio();
            ecioArray[3] = list.get((int) (number*0.5)).getEcio();
            ecioArray[4] = list.get((int) (number*0.65)).getEcio();
            ecioArray[5] = list.get((int) (number*0.8)).getEcio();
            ecioArray[6] = list.get((int) (number*0.9)).getEcio();
			for(int j = 0;j<7;j++){
				ecioArray[j] =  (float)(Math.round(ecioArray[j]*100))/100;
				System.out.println(ecioArray[j]);
			}   
//            将排序好的内容写入文件
            for(GridData gridData:list){
                bw.write(gridData.toString()+"\r\n");
            }
            bf.close();
            bw.close();  
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
		System.out.println("SortGridID() Over");
		return ecioArray;
	}
	//将栅格ID统一转换为像素ID，并生成图片
	public static void TransformPixelID(String sourceFile, String destDir) throws FileNotFoundException, IOException {
		System.out.println("TransformPixelID()");
		try{
            BufferedReader bf = new BufferedReader(new FileReader(sourceFile));//打开文件读
//            BufferedWriter bw = new BufferedWriter(new FileWriter("src/output/outputPixelFloatID.txt"));//打开文件写
            BufferedWriter bw_final = new BufferedWriter(new FileWriter(destDir));//打开文件写
            String in = "";
            while((in=bf.readLine()) != null){
            	String[] token = in.split(" ");
            	float ecio; 
            	float scale  = 7.3f;
            	int x,y,xSize,ySize;
    			//类型转换
    			ecio = Float.valueOf(token[2]);//话单质量ecio
    			x = (int) Math.floor(Float.valueOf(token[0]));//经度栅格ID
    			xSize = (int)(Math.floor(x*scale) - Math.floor((x-1)*scale));
    			x = (int)Math.floor((x-1)*scale);
    			y = (int) Math.floor(Float.valueOf(token[1]));//纬度栅格ID
    			ySize = (int)(Math.floor(y*scale) - Math.floor((y-1)*scale));
    			y = (int)Math.floor((y-1)*scale);

    			//输出像素的整数坐标
    			PixelIntData pixelIntData = new PixelIntData();//定义像素对象
    			pixelIntData.setEcio(ecio);
    			pixelIntData.setLongID(x);
    			pixelIntData.setLatID(y);
    			pixelIntData.setxSize(xSize);
    			pixelIntData.setySize(ySize);
    			bw_final.write(pixelIntData.toString()+"\r\n");
            }
            bf.close();
            bw_final.close();
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
		System.out.println("TransformPixelID() Over");
	}
	//将栅格ID进行排序
	public static float[] SortCalID(String srcFile) throws FileNotFoundException, IOException {
		System.out.println("SortCalID()");
		System.out.println(srcFile);
		float[] ecioArray = new float[7];
		try{
            BufferedReader bf = new BufferedReader(new FileReader(srcFile));//打开文件读
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/sort/grid123Sort.txt"));//打开文件写
            String in = "";
            List<GridData> list = new ArrayList<GridData>();//产生容器对象
            //循环一行一行读
            while((in=bf.readLine()) != null){
            	String[] token = in.split(" ");
            	float ecio;		
        		int x,y;
    			//类型转换
    			ecio = Float.valueOf(token[2]);//话单质量ecio
    			x = (int) Math.floor(Float.valueOf(token[0]));//经度栅格ID
    			y = (int) Math.floor(Float.valueOf(token[1]));//纬度栅格ID
                GridData gridData = new GridData();//定义栅格对象.
                gridData.setEcio(ecio);
                gridData.setLongID(x);
                gridData.setLatID(y);
                list.add(gridData);//存入容器.
            }
            Collections.sort(list);//排序
            int number = list.size();
            ecioArray[0] = list.get((int) (number*0.1)).getEcio();
            ecioArray[1] = list.get((int) (number*0.2)).getEcio();
            ecioArray[2] = list.get((int) (number*0.35)).getEcio();
            ecioArray[3] = list.get((int) (number*0.5)).getEcio();
            ecioArray[4] = list.get((int) (number*0.65)).getEcio();
            ecioArray[5] = list.get((int) (number*0.8)).getEcio();
            ecioArray[6] = list.get((int) (number*0.9)).getEcio();
			for(int j = 0;j<7;j++){
				ecioArray[j] =  (float)(Math.round(ecioArray[j]*100))/100;
				System.out.println(ecioArray[j]);
			}   
//            将排序好的内容写入文件
            for(GridData gridData:list){
                bw.write(gridData.toString()+"\r\n");
            }
            bf.close();
            bw.close();  
        }
        catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
		return ecioArray;
	}
}
class GridData implements Comparable<GridData>{
    private float ecio;    //ecio
    private int longID;	   //经度栅格ID
    private int latID;	   //纬度栅格ID
    public GridData(){};

    public float getEcio() {
		return ecio;
	}
	public void setEcio(float ecio) {
		this.ecio = ecio;
	}
	public int getLongID() {
		return longID;
	}
	public void setLongID(int longID) {
		this.longID = longID;
	}
	public int getLatID() {
		return latID;
	}
	public void setLatID(int latID) {
		this.latID = latID;
	}
    public String toString(){
        return longID + " " + latID + " " + ecio;
    }
    public int compareTo(GridData other){
//    	if(this.longID!=other.longID){
//    		return  this.longID - other.longID;//升序
//    	}
//    	else{
//    		return  this.latID - other.latID;//升序
//    	}
//   	System.out.println((int) (other.ecio - this.ecio));
//   	return 1;

    	if(other.ecio>this.ecio){
    		return 1;
    	}
    	else if (other.ecio<this.ecio){
    		return -1;
    	}else{
    		return 0;
    	}
    }
}
class PixelIntData {
    private float ecio;    //ecio
    private int longID;	   //经度栅格ID
    private int latID;	   //纬度栅格ID
    private int xSize;      //确定像素的大小
    private int ySize;      //确定像素的大小
    
	public float getEcio() {
		return ecio;
	}

	public void setEcio(float ecio) {
		this.ecio = ecio;
	}

	public int getLongID() {
		return longID;
	}

	public void setLongID(int longID) {
		this.longID = longID;
	}

	public int getLatID() {
		return latID;
	}

	public void setLatID(int latID) {
		this.latID = latID;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	public String toString(){
        return longID + " " + latID + " " + ecio + " " + xSize + " " + ySize;
    }
    
}