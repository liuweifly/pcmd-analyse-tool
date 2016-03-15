package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class GeneratePNG {
	public static void DrawPNG() throws FileNotFoundException, IOException {
		System.out.println("DrawPNG()");
		//绘图
		int width = 9728;
		int height = 11264;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		// ---------- 增加下面的代码使得背景透明 ----------------- 
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); 
		g2d.dispose(); 
		g2d = image.createGraphics(); 
		// ---------- 背景透明代码结束 ----------------- 
		//ecio临界值
		float[] b = {6.86f,7.93f,8.951f,9.717f,10.437f,11.268f,12.23f}; 
		for(int i=0;i<b.length;i++){
			b[i] *= -1;
		}
		//读txt数据
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader("src/output/wuhan_outputPixelIntID.txt"));
			String[] token;
			String read ;
			PixelIntData pixelIntData = new PixelIntData();//定义像素对象
			while((read =reader.readLine())!=null){
				token = read.split(" ");
				pixelIntData.setEcio(Float.parseFloat(token[2]));
    			pixelIntData.setLongID(Integer.parseInt(token[0]));
    			pixelIntData.setLatID(Integer.parseInt(token[1]));
    			pixelIntData.setxSize(Integer.parseInt(token[4]));
    			pixelIntData.setySize(Integer.parseInt(token[5]));
				//对话单质量进行分级判断从而确定画笔颜色
				if(pixelIntData.getEcio()>=b[0]){
					g2d.setColor(new Color(0,139,0,100));
				}
				else if(pixelIntData.getEcio()>=b[1]){
					g2d.setColor(new Color(0,205,0,100));
				}
				else if(pixelIntData.getEcio()>=b[2]){
					g2d.setColor(new Color(124,252,0,100));
				}
				else if(pixelIntData.getEcio()>=b[3]){
					
					g2d.setColor(new Color(255,255,0,100));
				}
				else if(pixelIntData.getEcio()>=b[4]){
					g2d.setColor(new Color(255,165,0,100));
				}
				else if(pixelIntData.getEcio()>=b[5]){
					
					g2d.setColor(new Color(255,69,0,100));
				}
				else if(pixelIntData.getEcio()>=b[6]){
					g2d.setColor(new Color(255,0,0,100));
				}
				else {
					g2d.setColor(new Color(139,0,0,100));
				}
				//绘制栅格
				g2d.fillRect(pixelIntData.getLongID(),11264-pixelIntData.getLatID()-pixelIntData.getySize(),pixelIntData.getxSize(),pixelIntData.getySize());
			}
		}catch (IOException e) {
            e.printStackTrace();
        } 
		reader.close();
		g2d.dispose();
		ImageIO.write(image, "png", new FileOutputStream("src/output/wuhan_grid.png"));
	}
   //通过下面方法调用上边工具类
   /**
     * 切片
     * @param srcImageFile   图片资源文件
     * @param descDir    切图后存放位置
     * @param tileSize     切图大小
     */
    public static void beforeCut(String srcImageFile, String descDir, int tileSize) {
        //地图级别目录
        File desc = new File(descDir);
        if(!desc.exists()) {
            desc.mkdirs();
        }
        // 读取源图像
        BufferedImage bufferImage;
        try {
            bufferImage = ImageIO.read(new File(srcImageFile));
            int srcHeight = bufferImage.getHeight(); // 源图高度
            int srcWidth = bufferImage.getWidth(); // 源图宽度
            System.out.println("图片大小{宽：" + srcWidth + "，高：" + srcHeight + "}");
            Map<Integer, Integer[]> map = ImageUtils.getInstance().getImageMap(srcHeight, srcWidth, tileSize);
            for (Integer zoom : map.keySet()) {
                //取得不同级别下的图片大小
                Integer[] imageSize = map.get(zoom);
                //地图级别目录
                File zoomDir = new File(desc + "/" + zoom);
                if(!zoomDir.exists()) {
                    zoomDir.mkdirs();
                }
                //得到要切割的图片
                Image image = bufferImage.getScaledInstance(imageSize[0], imageSize[1], Image.SCALE_DEFAULT);
                //开始切图
                ImageUtils.getInstance().cutImage(image, zoomDir, tileSize, tileSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }	
}
