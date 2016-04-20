package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GenerateMapPNG {
	public static void DrawPNG(String srcFile, String transformDir, String PNGDir) throws FileNotFoundException, IOException {
		
		//绘图
//		int width = 9728;
//		int height = 11264;
		//ecio临界值
//		float[] b = {6.86f,7.93f,8.951f,9.717f,10.437f,11.268f,12.23f}; 
//		float[] b = TransformCoordinate.SortGridID(srcFile);
		float[] b = TransformCoordinate.SortGridID("src/initial/grid123.txt");
//		TransformCoordinate.TransformPixelID("src/initial/grid23.txt","src/transform/grid23ID.txt");
//		TransformCoordinate.TransformPixelID("src/initial/grid1.txt","src/transform/grid1ID.txt");
//		TransformCoordinate.TransformPixelID("src/initial/grid123.txt","src/transform/grid123ID.txt");
		TransformCoordinate.TransformPixelID(srcFile,transformDir);
		System.out.println("DrawMapPNG()");
		System.out.println(PNGDir);
//		 BufferedImage image = ImageIO.read(new FileInputStream("src/input/wuhan.png"));//调入武汉市的地图
//		BufferedImage image = ImageIO.read(new FileInputStream("src/input/wuhanCut.png"));//调入样本区域的地图
		BufferedImage image = ImageIO.read(new FileInputStream("src/draw/stationSort.png"));//调入样本区域的基站地图
//		BufferedImage image = ImageIO.read(new FileInputStream("src/draw/grid123.png"));//绘制信号质量预警用
//		BufferedImage image = ImageIO.read(new FileInputStream("src/draw/grid123Call.png"));//绘制高负载用
		BufferedImage stationMarker = ImageIO.read(new FileInputStream("src/input/20Grey.png")); 
		BufferedImage stationMarkerBlack = ImageIO.read(new FileInputStream("src/input/20Orange.png")); 
		Graphics2D g2d = image.createGraphics();
		//读txt数据
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(transformDir));
			String[] token;
			String read ;
			PixelIntData pixelIntData = new PixelIntData();//定义像素对象
			while((read =reader.readLine())!=null){
				token = read.split(" ");
				pixelIntData.setEcio(Float.parseFloat(token[2]));
    			pixelIntData.setLongID(Integer.parseInt(token[0]));
    			pixelIntData.setLatID(Integer.parseInt(token[1]));
    			pixelIntData.setxSize(Integer.parseInt(token[3]));
    			pixelIntData.setySize(Integer.parseInt(token[4]));
    			int alpha=100;
    			int delta=0;
    			int station_value=0;
				//对话单质量进行分级判断从而确定画笔颜色
				if(pixelIntData.getEcio()>=b[0]){
					g2d.setColor(new Color(0,139,0,alpha-delta));
//					g2d.setColor(new Color(0,139,0,255));//绘制聚类图专用
				}
				else if(pixelIntData.getEcio()>=b[1]){
					g2d.setColor(new Color(0,205,0,alpha-delta));
//					g2d.setColor(new Color(124,252,0,255));//绘制聚类图专用
//					g2d.setColor(new Color(255,0,0,255));
				}
				else if(pixelIntData.getEcio()>=b[2]){
					g2d.setColor(new Color(124,252,0,alpha-delta));
//					g2d.setColor(new Color(255,165,0,255));//绘制聚类图专用
				}
				else if(pixelIntData.getEcio()>=b[3]){
//					g2d.setColor(new Color(255,0,0,255));
					g2d.setColor(new Color(255,255,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[4]){
//					g2d.setColor(new Color(139,0,0,255));
					g2d.setColor(new Color(255,165,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[5]){
//					g2d.setColor(new Color(0,124,139,255));
					g2d.setColor(new Color(255,69,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[6]){
//					g2d.setColor(new Color(0,0,255,255));
					g2d.setColor(new Color(255,0,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()==-1602){
//					g2d.setColor(new Color(0,0,0,255));
					continue;
				}
				else if(pixelIntData.getEcio()<=-1603&&pixelIntData.getEcio()>=-1609){
					station_value = 1;
				}
				else if(pixelIntData.getEcio()<=-1610&&pixelIntData.getEcio()>=-1673){
					station_value = 2;
				}
				else {
					g2d.setColor(new Color(139,0,0,alpha+delta));
				}
				
				if(station_value==1){
					g2d.drawImage(stationMarker,pixelIntData.getLongID()-2482-2,694-(pixelIntData.getLatID()-5032)-18,16,18,null);
					station_value = 0;
				}
				else if(station_value==2){
					g2d.drawImage(stationMarkerBlack,pixelIntData.getLongID()-2482-2,694-(pixelIntData.getLatID()-5032)-18,16,18,null);
					station_value = 0;
				}
				else{
					//绘制栅格
//					 g2d.fillRect(pixelIntData.getLongID(),11264-pixelIntData.getLatID()-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
//					g2d.fillRect(pixelIntData.getLongID()-4869,1241-(pixelIntData.getLatID()-4350)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
					// g2d.fillRect(pixelIntData.getLongID()-4803,873-(pixelIntData.getLatID()-8792)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
					
					//年前测试的图片参数
//					g2d.fillRect(pixelIntData.getLongID()-4489,963-(pixelIntData.getLatID()-8446)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
					//实验图片参数
					g2d.fillRect(pixelIntData.getLongID()-2482,694-(pixelIntData.getLatID()-5032)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());					
				}
			}
		}catch (IOException e) {
            e.printStackTrace();
        } 
		reader.close();
		g2d.dispose();
		ImageIO.write(image, "png", new FileOutputStream(PNGDir));
		System.out.println("DrawMapPNG() Over");
	}
	public static void CutImage(int x,int y,int width,int height) throws IOException{  
        String sourcePath="src/input/wuhan.png";
        String targetPath="src/input/wuhanCut.png";
        BufferedImage image = ImageIO.read(new FileInputStream(sourcePath));
        image = image.getSubimage(x, y, width, height);  
        ImageIO.write(image, "png", new File(targetPath));  
    }
	public static void DrawCenter(ArrayList<float[]> center, String srcFile, String PNGDir) throws IOException{  
		try{
			System.out.println("DrawCenter()");
			System.out.println(PNGDir);
			BufferedImage image = ImageIO.read(new FileInputStream(srcFile));//调入武汉市的地图
			BufferedImage marker = ImageIO.read(new FileInputStream("src/input/markers.png")); 
			BufferedImage markerb = ImageIO.read(new FileInputStream("src/input/markerw.png")); 
			Graphics2D g2d = image.createGraphics();
			for(int i=0;i<center.size();i++){
	        	int x= (int)center.get(i)[0];
	        	int y= (int)center.get(i)[1];
	        	//坐标变换
				float scale  = 7.3f;
				int xSize,ySize;
				//类型转换
				xSize = (int)(Math.floor(x*scale) - Math.floor((x-1)*scale));
				x = (int)Math.floor((x-1)*scale);
				ySize = (int)(Math.floor(y*scale) - Math.floor((y-1)*scale));
				y = (int)Math.floor((y-1)*scale);
	            //在PNG图片上绘制点
				int color = 0;
				int index = srcFile.indexOf("grid123K.png");
				if(index!=-1){
					color = i*30;
				}
//	        	g2d.setColor(new Color(color,color,color));
				g2d.setColor(new Color(139,0,0,190));
				
//	        	System.out.println("center: "+x+","+y+"  "+xSize+","+ySize);
//	        	g2d.fillRect(x-4869,1241-(y-4350)-ySize,xSize,ySize);	
	        	// g2d.fillRect(x-4803,873-(y-8792)-ySize,xSize,ySize);
//	        	g2d.fillRect(x-4489,963-(y-8446)-ySize,xSize,ySize);  
//	        	g2d.fillRect(x-2482,694-(y-5032)-ySize,xSize,ySize);  
	        	if(i<3){
	        		g2d.drawImage(marker,x-2482-8,694-(y-5032)-25,39,25,null);
	        	}
	        	else{
	        		g2d.drawImage(markerb,x-2482-8,694-(y-5032)-25,39,25,null);
	        	}
	        } 
			g2d.dispose();
			ImageIO.write(image, "png", new FileOutputStream(PNGDir));
			System.out.println("DrawCenter() Over");
		}catch (IOException e) {
            e.printStackTrace();
        } 
    }
	public static void DrawCallPNG(String srcFile, String transformDir, String PNGDir) throws FileNotFoundException, IOException {

		float[] b = TransformCoordinate.SortGridID("src/initial/grid123CallSift.txt");
//		float[] b = TransformCoordinate.SortGridID("src/initial/grid123Call.txt");
		TransformCoordinate.TransformPixelID(srcFile,transformDir);
		System.out.println("DrawCalPNG()");
		System.out.println(PNGDir);
//		BufferedImage image = ImageIO.read(new FileInputStream("src/input/wuhan.png"));//调入武汉市的地图
		BufferedImage image = ImageIO.read(new FileInputStream("src/input/wuhanCut.png"));//调入武汉市的地图
		Graphics2D g2d = image.createGraphics();
		//读txt数据
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(transformDir));
			String[] token;
			String read ;
			PixelIntData pixelIntData = new PixelIntData();//定义像素对象
			while((read =reader.readLine())!=null){
				token = read.split(" ");
				pixelIntData.setEcio(Float.parseFloat(token[2]));
    			pixelIntData.setLongID(Integer.parseInt(token[0]));
    			pixelIntData.setLatID(Integer.parseInt(token[1]));
    			pixelIntData.setxSize(Integer.parseInt(token[3]));
    			pixelIntData.setySize(Integer.parseInt(token[4]));
    			int alpha=100;
    			int delta=50;
				//对话单质量进行分级判断从而确定画笔颜色
				if(pixelIntData.getEcio()>=b[0]){
//					g2d.setColor(new Color(0,139,0,alpha-delta));
					g2d.setColor(new Color(139,0,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[1]){
//					g2d.setColor(new Color(0,205,0,alpha-delta));
					g2d.setColor(new Color(255,0,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[2]){
//					g2d.setColor(new Color(124,252,0,alpha-delta));
					g2d.setColor(new Color(255,69,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[3]){					
//					g2d.setColor(new Color(255,255,0,alpha-delta));
					g2d.setColor(new Color(255,165,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[4]){
//					g2d.setColor(new Color(255,165,0,alpha+delta));
					g2d.setColor(new Color(255,255,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[5]){					
//					g2d.setColor(new Color(255,69,0,alpha+delta));
					g2d.setColor(new Color(124,252,0,alpha-delta));
				}
				else if(pixelIntData.getEcio()>=b[6]){
//					g2d.setColor(new Color(255,0,0,alpha+delta));
					g2d.setColor(new Color(0,205,0,alpha-delta));
				}
				else {
//					g2d.setColor(new Color(139,0,0,alpha+delta));
					g2d.setColor(new Color(0,139,0,alpha-delta));
				}
				//绘制栅格
//				g2d.fillRect(pixelIntData.getLongID(),11264-pixelIntData.getLatID()-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
//				g2d.fillRect(pixelIntData.getLongID()-4869,1241-(pixelIntData.getLatID()-4350)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
				// g2d.fillRect(pixelIntData.getLongID()-4803,873-(pixelIntData.getLatID()-8792)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
//				g2d.fillRect(pixelIntData.getLongID()-4489,963-(pixelIntData.getLatID()-8446)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
				g2d.fillRect(pixelIntData.getLongID()-2482,694-(pixelIntData.getLatID()-5032)-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
			}
		}catch (IOException e) {
            e.printStackTrace();
        } 
		reader.close();
		g2d.dispose();
		ImageIO.write(image, "png", new FileOutputStream(PNGDir));
		System.out.println("DrawMapPNG() Over");
	}
	public static void DrawColoSticker() throws FileNotFoundException, IOException {
		System.out.println("DrawColoSticker()");
		//绘图
		int width = 247;
		int height = 20;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		//设置白色背景
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0,0,width,height);
		int[] b = {0,1,2,3,4,5,6,7};
		// ---------- 增加下面的代码使得背景透明 ----------------- 
//		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); 
//		g2d.dispose(); 
//		g2d = image.createGraphics(); 
		// ---------- 背景透明代码结束 ----------------- 
		for(int i=0;i<8;i++){
			//对话单质量进行分级判断从而确定画笔颜色
			if(i<=b[0]){
				g2d.setColor(new Color(0,139,0,100));
			}
			else if(i<=b[1]){
				g2d.setColor(new Color(0,205,0,100));
			}
			else if(i<=b[2]){
				g2d.setColor(new Color(124,252,0,100));
			}
			else if(i<=b[3]){
				
				g2d.setColor(new Color(255,255,0,100));
			}
			else if(i<=b[4]){
				g2d.setColor(new Color(255,165,0,100));
			}
			else if(i<=b[5]){
				
				g2d.setColor(new Color(255,69,0,100));
			}
			else if(i<=b[6]){
				g2d.setColor(new Color(255,0,0,100));
			}
			else {
				g2d.setColor(new Color(139,0,0,100));
			}
			g2d.fillRect(i*31,0,30,20);	
		}
		ImageIO.write(image, "png", new FileOutputStream("src/output/colorSticker.png"));
		System.out.println("DrawColoSticker() Over");
	}
 
}
