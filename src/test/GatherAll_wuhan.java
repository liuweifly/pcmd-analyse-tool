package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class GatherAll_wuhan {
	public static void main(String args[]) throws FileNotFoundException, IOException{
		System.out.println("main()");
//		Station.stationDisplay();

//		GenerateMapPNG.CutImage(4869, 5673,1014, 1241);
//		GenerateMapPNG.CutImage(4803, 1599,708, 870);
//      GenerateMapPNG.CutImage(4489, 1855,810, 963); //年前用的图片		
//      GenerateMapPNG.CutImage(2482, 5538,781, 694); //3.7裁切图片，用作论文实验
//		GenerateMapPNG.DrawColoSticker();
//***数据筛选、数据融合、格式转换**	
//      TransformCoordinate.SiftData("src/input/grid_data1.txt","src/input/grid1Sift.txt");//数据筛选
//      TransformCoordinate.SiftData("src/input/grid_data2.txt","src/input/grid23Sift.txt");//数据筛选
//      TransformCoordinate.ReadFile("src/input/grid1Sift.txt", "src/initial/grid1Sift.txt");//数据格式转换
//      TransformCoordinate.ReadFile("src/input/grid23Sift.txt", "src/initial/grid23Sift.txt");//数据格式转换
//      DataHandling.DataProcess("src/input/grid1Sift.txt","src/input/grid23Sift.txt","src/initial/grid123Sift.txt");//数据融合
//		DataHandling.DataEcioAlert("src/initial/grid123Sift.txt", -12, "src/draw/grid123EcioAlert.png");
//***123筛选点绘图**	
//		GenerateMapPNG.DrawPNG("src/initial/grid1Sift.txt","src/transform/grid1ID.txt","src/draw/grid1.png");
//		GenerateMapPNG.DrawPNG("src/initial/grid23Sift.txt","src/transform/grid23ID.txt","src/draw/grid23.png");
//		GenerateMapPNG.DrawPNG("src/initial/grid123Sift.txt","src/transform/grid123ID.txt","src/draw/grid123.png");
//***123原始点绘图**		
//		GenerateMapPNG.DrawPNG("src/initial/grid1.txt","src/transform/grid123ID.txt","src/draw/grid1.png");
//      GenerateMapPNG.DrawPNG("src/initial/grid23.txt","src/transform/grid123ID.txt","src/draw/grid23.png");
//      GenerateMapPNG.DrawPNG("src/initial/grid123.txt","src/transform/grid123ID.txt","src/draw/grid123.png");
//***数据统计**
//		Statistics.execute("src/initial/grid123Sift.txt");
//		Statistics.execute("src/smooth/grid123Smooth.txt");
//		Statistics.gridGapAmount("src/initial/grid12 3Sift.txt");
//		Statistics.gridGapAmount("src/process/grid123Process.txt");
//***绘制Ec/io图**
//		DataHandling.DataAdd("src/initial/grid123Sift.txt","src/process/grid123Process.txt");//数据增加，填补空白区域
//		DataHandling.DataSmooth("src/process/grid123Process.txt", "src/smooth/grid123Smooth.txt");//数据平滑，去除杂点
//		DataHandling.DataSmooth("src/smooth/grid123Smooth.txt", "src/smooth/grid123Smooth1.txt");//数据平滑，去除杂点
//		GenerateMapPNG.DrawPNG("src/process/grid123Process.txt","src/transform/grid123ID.txt","src/draw/grid123.png");
//***绘制话务量图**	
//		DataHandling.DataCallProcess("src/input/grid_data1.txt","src/input/grid_data2.txt","src/initial/grid123Call.txt");
//		GenerateMapPNG.DrawCallPNG("src/initial/grid123Call.txt","src/transform/grid123ID.txt","src/draw/grid123Call.png");
		
//		DataHandling.DataCallProcess("src/input/grid1Sift.txt","src/input/grid23Sift.txt","src/initial/grid123CallSift.txt");	
//		DataHandling.DataAdd("src/initial/grid123CallSift.txt","src/process/grid123CallProcess.txt");//数据增加，填补空白区域
//		GenerateMapPNG.DrawCallPNG("src/process/grid123CallProcess.txt","src/transform/grid123ID.txt","src/draw/grid123Call.png");

//		DataHandling.DataCallAlert("src/process/grid123CallProcess.txt", 3, 1000, "src/draw/grid123CallAlert.png");//话务量大于阈值时报警
//		DataHandling.DataCallAlert("src/initial/grid123Call.txt", 3, 2000, "src/draw/grid123CallAlert.png");//话务量大于阈值时报警
//***K均值算法**		
	   	//初始化一个Kmean对象，将k置为5，因为绘制栅格颜色的问题，小于8
//        Kmeans k=new Kmeans(7);  
//        //设置原始数据集  
//        k.setDataFileSet("src/initial/gridJointFind.txt",(float) -12);  
//        //执行算法  
//        k.execute();  
//        //输出结果
//        k.printDataArray("src/zcluster/grid123K.txt");
//		//得到质心
//        ArrayList<float[]> ecioCenter = k.getEcioCenter();
//        绘出聚类后的底图
//        GenerateMapPNG.DrawPNG("src/zcluster/grid123K.txt","src/transform/grid123IDK.txt","src/draw/grid123K.png");     
//        绘出质心     
//		GenerateMapPNG.DrawCenter(ecioCenter,"src/draw/gridJointFind.png","src/draw/grid123KEcioC.png");
//		评估效果
//        Evaluate.execute("src/process/grid123Process.txt", ecioCenter, (float)-2, 4,20,3,"src/evaluate/grid123KEcioE.txt");
//        GenerateMapPNG.DrawPNG("src/evaluate/grid123KEcioE.txt","src/transform/grid123IDKEcioE.txt","src/draw/grid123KEcioE.png");
 //***DBSCAN**
 		//初始化一个BisectingKmeans对象，将k置为5 
        DBSCAN ds = new DBSCAN(7);
 		// 设置原始数据集
        ds.setDataFileSet("src/initial/gridJointFind.txt",(float) -12);
 		// 执行算法
        ds.execute();
 		//输出结果
        ds.printDataArray("src/zcluster/grid123DBS.txt");
		//得到质心
        ArrayList<float[]> Center = ds.getCenter();
//        //绘图
        GenerateMapPNG.DrawPNG("src/zcluster/grid123DBS.txt","src/transform/grid123IDDBS.txt","src/draw/grid123DBS.png");
        GenerateMapPNG.DrawCenter(Center,"src/draw/grid123DBS.png","src/draw/grid123DBSC.png");
        Evaluate.execute("src/process/grid123Process.txt", Center, (float)-2, 4,20,3,"src/evaluate/grid123DBSE.txt");
        GenerateMapPNG.DrawPNG("src/evaluate/grid123DBSE.txt","src/transform/grid123IDDBSE.txt","src/draw/grid123DBSE.png");

		System.out.println("Over");
		System.exit(0);
	}
	
}
//***23点处理**		
//DataHandling.DataAdd("src/initial/grid23Sift.txt","src/process/grid23Process.txt");//数据增加，填补空白区域
//DataHandling.DataSmooth("src/process/grid23Process.txt", "src/smooth/grid23Smooth.txt");//数据平滑，去除杂点
//GenerateMapPNG.DrawPNG("src/smooth/grid23Smooth.txt","src/transform/grid23ID.txt","src/draw/grid23.png");
//***1点处理**			
//DataHandling.DataAdd("src/initial/grid1.txt","src/process/grid1Process.txt");
//GenerateMapPNG.DrawPNG("src/process/grid1Process.txt","src/transform/grid1ID.txt","src/draw/grid1.png");

//GenerateMapPNG.DrawCenter(k.getCenter(),"src/draw/grid123K.png","src/draw/grid123KC.png");
////改善后效果
////String srcFile,ArrayList<float[]> center,float coefficient,int initialValue,int radius, int num, String destDir
//Evaluate.execute("src/smooth/grid123Smooth.txt", k.getCenter(), (float)-2, 4,20,3,"src/evaluate/grid123KE.txt");
//GenerateMapPNG.DrawPNG("src/evaluate/grid123KE.txt","src/transform/grid123IDKE.txt","src/draw/grid123KE.png");

//***二分K均值**
////初始化一个BisectingKmeans对象，将k置为5 
//BisectingKmeans bkm = new BisectingKmeans(5);
//// 设置原始数据集
//bkm.setDataFileSet("src/smooth/grid123Smooth.txt",(float) - 10);
//// 执行算法
//bkm.execute();
////输出结果
//bkm.printDataArray("src/zcluster/grid123BK.txt");
////绘图
//GenerateMapPNG.DrawPNG("src/zcluster/grid123BK.txt","src/transform/grid123IDBK.txt","src/draw/grid123BK.png");
//GenerateMapPNG.DrawCenter(bkm.getCenter(),"src/draw/grid123BK.png","src/draw/grid123BKC.png")	

//***CHAMELEON算法**      
//// 2最邻近，这里面包括它自己
//Chameleon cham = new Chameleon(2);
//// 设置原始数据集
//cham.setDataFileSet("src/smooth/grid123Smooth.txt",(float) -10);
//// 执行算法
//cham.execute();
////输出结果
//cham.printDataArray("src/zcluster/grid123CHAM.txt");
////绘图
//GenerateMapPNG.DrawPNG("src/zcluster/grid123CHAM.txt","src/transform/grid123CHAM.txt","src/draw/grid123CHAM.png");

