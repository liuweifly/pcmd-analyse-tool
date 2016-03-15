package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * K均值聚类算法
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int m;// 迭代次数
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private ArrayList<float[]> dataSet;// 数据集链表
	private ArrayList<float[]> center;// 中心链表
	private ArrayList<ArrayList<float[]>> cluster; // 簇
	private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;
	private ArrayList<float[]> callCenter;// 话务量聚集点

	/**
	 * 设置需分组的原始数据集,setDataFileSet从文件中获取数据，setDataSet直接获取传来的数据
	 * 
	 * @param dataSet
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */

	public void setDataFileSet(String srcFile,float threshold) throws FileNotFoundException, IOException {
		this.dataSet = CommonUtils.setDataFileSetNew(srcFile,threshold);
	}
	
    public void setDataSet(ArrayList<float[]> dataSet) {  
        this.dataSet = dataSet;  
    } 

	/**
	 * 获取结果分组
	 * 
	 * @return 结果集
	 */

	public ArrayList<ArrayList<float[]>> getCluster() {
		return cluster;
	}

	/**
	 * 获取中心
	 * 
	 * @return 中心集
	 */

	public ArrayList<float[]> getCenter() {
		ArrayList<float[]> newCenter = new ArrayList<float[]>();// 中心链表
		List<int[]> centerNum = new ArrayList<int[]>();
		
		for(int i=0;i<k;i++){
			int[] a = {0,0};
			centerNum.add(a);
			centerNum.get(i)[0] = i;
			int num=0;
			for(int j=0;j< cluster.get(i).size();j++){
				if(cluster.get(i).get(j)[2]<-11){
					num++;
				}
			}
			centerNum.get(i)[1] = num;
		} 
		//排序
	    int[] temp = {0,0}; // 记录临时中间值    
	    for (int i = 0; i < k - 1; i++) {   
	        for (int j = i + 1; j < k; j++) {   
	            if (centerNum.get(i)[1] < centerNum.get(j)[1]) { // 交换两数的位置   
	                temp[0] = centerNum.get(i)[0]; 
	                temp[1] = centerNum.get(i)[1]; 
	                centerNum.get(i)[0] = centerNum.get(j)[0]; 
	                centerNum.get(i)[1] = centerNum.get(j)[1];   
	                centerNum.get(j)[0] = temp[0];  
	                centerNum.get(j)[1] = temp[1];
	            }   
	        }   
	    }
	    for (int i = 0; i < k; i++) {
	    	newCenter.add(center.get(centerNum.get(i)[0]));
//	    	System.out.println("new:"+centerNum.get(i)[1]);
	    }	    
		return newCenter;
	}
	public ArrayList<float[]> getEcioCenter() {	
		//加入Ecio维度重新计算质心
		for (int i = 0; i < k; i++) {
			int n = cluster.get(i).size();
			
			float[] newCenter = { 0, 0 };
			float[] newCenterEcio = { 0, 0 };
			if (n != 0) {	
				//不考虑信号强度计算质心
				for (int j = 0; j < n; j++) {
					newCenter[0] += cluster.get(i).get(j)[0];
					newCenter[1] += cluster.get(i).get(j)[1];
				}
				// 设置一个平均值
				newCenter[0] = (float)Math.floor(newCenter[0] / n);
				newCenter[1] = (float)Math.floor(newCenter[1] / n);	
				
				//考虑信号强度计算质心
				int num = 0;				
				for (int j = 0; j < n; j++) {
					//只考虑信号强度低于-12的
					if(cluster.get(i).get(j)[2]<-12){
						num++;
						newCenterEcio[0] += cluster.get(i).get(j)[0];
						newCenterEcio[1] += cluster.get(i).get(j)[1];
					}
				}
				newCenterEcio[0] = (float)Math.floor(newCenterEcio[0] / num);
				newCenterEcio[1] = (float)Math.floor(newCenterEcio[1] / num);
				if(num > 10){
					newCenter[0] = (float)Math.floor((newCenter[0] + newCenterEcio[0]*3) / 4);
					newCenter[1] = (float)Math.floor((newCenter[1] + newCenterEcio[1]*3) / 4);
				}		
			}
			center.set(i, newCenter);
		}	
		
		//对质心按所对应的簇的大小排序
		ArrayList<float[]> newCenter = new ArrayList<float[]>();// 中心链表

		List<int[]> centerNum = new ArrayList<int[]>();

		for(int i=0;i<k;i++){
			int[] a = {0,0,0,0};//序号，簇中低Ec/io的栅格数目，簇的中心附近高话务量区域，综合评分
			centerNum.add(a);
			centerNum.get(i)[0] = i;
			int num=0;
			for(int j=0;j< cluster.get(i).size();j++){
				if(cluster.get(i).get(j)[2]<-11){
					num++;
				}
			}
			centerNum.get(i)[1] = num;
			int x = (int)center.get(i)[0];
			int y = (int)center.get(i)[1];
			HashMap<String, String> hm = new HashMap<String, String>();//hm 用来原始数据
			for(int j=0;j<callCenter.size();j++){
				int m = (int)callCenter.get(j)[0];
				int n = (int)callCenter.get(j)[1];
				String CallNum = Integer.toString((int)callCenter.get(j)[2]);
				hm.put(m+"^"+n,CallNum); //将二三点数据的栅格，经纬度、ecio载入hm中
			}
			centerNum.get(i)[2] = CommonUtils.CallStatistic(hm, new int[] {x,y}, 20);			
//			System.out.println(i+" "+num+" "+centerNum.get(i)[2]);
			if(centerNum.get(i)[2]<20){//如果该中心附近话务量少于20，则不予考虑建站
				centerNum.get(i)[3] = 0;
			}
			else{
				//建站评价方程
				centerNum.get(i)[3] = centerNum.get(i)[1] + centerNum.get(i)[2]/500;
			}
		} 
		//排序
	    int[] temp = {0,0,0,0}; // 记录临时中间值    
	    for (int i = 0; i < k - 1; i++) {   
	        for (int j = i + 1; j < k; j++) {   
	            if (centerNum.get(i)[3] < centerNum.get(j)[3]) { // 交换两数的位置   
//	                temp[0] = centerNum.get(i)[0]; 
//	                temp[1] = centerNum.get(i)[3]; 
//	                centerNum.get(i)[0] = centerNum.get(j)[0]; 
//	                centerNum.get(i)[3] = centerNum.get(j)[3];   
//	                centerNum.get(j)[0] = temp[0];  
//	                centerNum.get(j)[3] = temp[1];
//	            	temp = centerNum.get(i);
//	            	centerNum.get(i) = centerNum.get(j);
	            	Collections.swap(centerNum, i, j);
	            }   
	        }   
	    }
	    System.out.println("**********************************************");
	    for (int i = 0; i < k; i++) {
	    	if(centerNum.get(i)[3] != 0){
	    		newCenter.add(center.get(centerNum.get(i)[0]));
	    		System.out.println("station"+(i+1)+": "+center.get(centerNum.get(i)[0])[0]+","+
	    		    	center.get(centerNum.get(i)[0])[1]+" "+centerNum.get(i)[1]+","+
	    		    	centerNum.get(i)[2]+","+centerNum.get(i)[3]);
	    	}  	
	    }	
	    System.out.println("**********************************************");
		return newCenter;
	}
	/**
	 * 构造函数，传入需要分成的簇数量
	 * 
	 * @param k簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public Kmeans(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
	}

	/**
	 * 初始化
	 */
	private void init() {
		m = 0;
		random = new Random();
		if (dataSet == null || dataSet.size() == 0) {
//			initDataSet();
		}
		dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
		center = initCenters();
		cluster = initCluster();
		jc = new ArrayList<Float>();
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 * 
	 * @return 中心点集
	 */
	private ArrayList<float[]> initCenters() {
		
//********************************************************************//
		int callRadius = 3;
		int alertThreshold = 1000;
		callCenter = DataHandling.DataCallAlert("src/smooth/grid123CallSmooth.txt", callRadius, alertThreshold, 
				"src/draw/grid123CallAlert.png");//话务量大于阈值时报警
//*******************************************************************//		
		ArrayList<float[]> center = new ArrayList<float[]>();
		int[] randoms = new int[k];
		boolean flag;
		int length = callCenter.size();
//		int temp = random.nextInt(length);
		int temp = random.nextInt(dataSetLength);
		randoms[0] = temp;
		for (int i = 1; i < k; i++) {
			flag = true;
			while (flag) {
//				temp = random.nextInt(length);
				temp = random.nextInt(dataSetLength);
				int j = 0;
				while (j < i) {
					if (temp == randoms[j]) {
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}
		// System.out.println();
		for (int i = 0; i < k; i++) {
//			center.add(callCenter.get(randoms[i]));// 生成初始化中心链表
			center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
			System.out.println(center.get(i)[0]+",, "+center.get(i)[1]);
		}
		//设置固定中心，供评估使用
//		center.clear();
//        float[] a = {(float) 355.0,(float) 729.0};
//        float[] b = {(float) 410.0,(float) 704.0};
//        float[] c = {(float) 364.0,(float) 767.0};
//        center.add(a);
//		351.0,, 772.0
//		420.0,, 710.0
//		434.0,, 701.0
//        center.add(b);
//        center.add(c);

		return center;
	}

	/**
	 * 初始化簇集合
	 * 
	 * @return 一个分为k簇的空数据的簇集合
	 */
	private ArrayList<ArrayList<float[]>> initCluster() {
		ArrayList<ArrayList<float[]>> cluster = new ArrayList<ArrayList<float[]>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<float[]>());
		}

		return cluster;
	}

	/**
	 * 计算两个点之间的距离
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 距离
	 */
	private float distance(float[] element, float[] center) {
		float distance = 0.0f;
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		float a = x * x + y * y ;
		distance = (float) Math.sqrt(a);

		return distance;
	}

	/**
	 * 获取距离集合中最小距离的位置
	 * 
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(float[] distance) {
		float minDistance = distance[0];
		int minLocation = 0;
		for (int i = 1; i < distance.length; i++) {
			if (distance[i] < minDistance) {
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if (random.nextInt(10) < 5) {
					minLocation = i;
				}
			}
		}

		return minLocation;
	}

	/**
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 */
	private void clusterSet() {
		float[] distance = new float[k];
		for (int i = 0; i < dataSetLength; i++) {
			for (int j = 0; j < k; j++) {
				distance[j] = distance(dataSet.get(i), center.get(j));
				// System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);
			}
			int minLocation = minDistance(distance);
			// System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
			// System.out.println();
			cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中
		}
	}
	/**
	 * 计算误差平方和准则函数方法
	 */
	private void countRule() {
		float jcF = 0;
		for (int i = 0; i < cluster.size(); i++) {
			for (int j = 0; j < cluster.get(i).size(); j++) {
				jcF += CommonUtils.errorSquare(cluster.get(i).get(j), center.get(i));

			}
		}
		jc.add(jcF);
	}

	/**
	 * 设置新的簇中心方法
	 */
	private void setNewCenter() {			
		for (int i = 0; i < k; i++) {
			int n = cluster.get(i).size();
			
			float[] newCenter = { 0, 0 };
//			float[] newCenterEcio = { 0, 0 };
			if (n != 0) {	
				//不考虑信号强度计算质心
				for (int j = 0; j < n; j++) {
					newCenter[0] += cluster.get(i).get(j)[0];
					newCenter[1] += cluster.get(i).get(j)[1];
				}
				// 设置一个平均值
				newCenter[0] = (float)Math.floor(newCenter[0] / n);
				newCenter[1] = (float)Math.floor(newCenter[1] / n);	
				
//				//考虑信号强度计算质心
//				int num = 0;				
//				for (int j = 0; j < n; j++) {
//					//只考虑信号强度低于-12的
//					if(cluster.get(i).get(j)[2]<-12){
//						num++;
//						newCenterEcio[0] += cluster.get(i).get(j)[0];
//						newCenterEcio[1] += cluster.get(i).get(j)[1];
//					}
//				}
//
//				newCenterEcio[0] = (float)Math.floor(newCenterEcio[0] / num);
//				newCenterEcio[1] = (float)Math.floor(newCenterEcio[1] / num);
//				if(num > 10){
//					newCenter[0] = (float)Math.floor((newCenter[0] + newCenterEcio[0]) / 2);
//					newCenter[1] = (float)Math.floor((newCenter[1] + newCenterEcio[1]) / 2);
//				}		
			}
			center.set(i, newCenter);
		}			
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
	public void printDataArray(String destDir) throws IOException {
		CommonUtils.printDataArray(destDir, cluster);
	}

	/**
	 * Kmeans算法核心过程方法
	 */
	private void kmeans() {
		init();
		// 循环分组，直到误差不变为止
		while (true) {
			clusterSet();
			countRule();
			// System.out.println("count:"+"jc["+m+"]="+jc.get(m));
			// System.out.println();
			// 误差不变了，分组完成
			if (m != 0) {
				if (jc.get(m) - jc.get(m - 1) == 0) {
					break;
				}
			}
			setNewCenter();
			// printDataArray(center,"newCenter");
			m++;
			cluster.clear();
			cluster = initCluster();
		}
		System.out.println("note:the times of repeat:m="+m);//输出迭代次数
		for(int i=0;i<k;i++){
			System.out.println("center:"+center.get(i)[0]+" "+center.get(i)[1]);//输出中心
		}
	}

	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");
		kmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime)
				+ "ms");
		System.out.println("kmeans ends");
		System.out.println();
	}
}
class SortByNum implements Comparator {
	 public int compare(Object o1, Object o2) {
		 int[] s1 = (int[]) o1;
		 int[] s2 = (int[]) o2;
		  if (s1[1] > s2[1])
		   return 1;
		  return 0;
	}
}