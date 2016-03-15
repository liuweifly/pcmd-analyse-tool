/**
 * Author: Orisun
 * Date: Sep 13, 2011
 * FileName: chameleon.java
 * Function:
 */
package test;
 
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import test.DBSCAN.DataObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Vector;
 
public class Chameleon {
    float[][] W; // weight矩阵(方阵)
    byte[][] Conn; // 连接矩阵(方阵)
    Vector<Vector<Integer>> clusters;
    double MI; // 综合指数
    int k=2; // 2最邻近，这里面包括它自己
    private ArrayList<DataObject> dataSet;
 
    // 构造函数，初始化变量
    public Chameleon(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
    }
    
	public void setDataFileSet(String srcFile,float threshold) throws FileNotFoundException, IOException {
		ArrayList<float[]> fileSet = CommonUtils.setDataFileSet(srcFile,threshold);
		this.dataSet = new ArrayList<DataObject>();
		for(int i=0;i<fileSet.size();i++){
			DataObject temp = new DataObject(false,0,fileSet.get(i));//注意，temp初始化不能定义for循环外
//			System.out.println(i+": "+temp.getID()[0]+" "+temp.getID()[1]);
			this.dataSet.add(temp);
		}
	}	
	public void printDataArray(String destDir) throws IOException {
//		CommonUtils.printDataArray(destDir, clusters);
		System.out.println("==============================================");
		String in;
		int j=0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(destDir));//打开文件写		
        for (int i = 0; i < clusters.size(); i++) {
        	if(clusters.get(i).size()<35){
        		continue;
        	}
            Iterator<Integer> iter = clusters.get(i).iterator();
            while (iter.hasNext()) {
            	int pp = iter.next();
            	in =this.dataSet.get(pp).getID()[0]+ " " + this.dataSet.get(pp).getID()[1]+" "+(-12+j)+" "+i;
//            	System.out.println(in);
            	bw.write(in+"\r\n");
            }
            j++;
        }
		bw.close();
	}
	public void execute(){
		init(this.dataSet.size(),0.3);//综合指数为0.1
        buildWeightMatrix(this.dataSet);
        buildSmallCluster();
        System.out.println("==============第一阶段后的分类结果==============");
        printClusters();
        cluster();
        System.out.println("==============第二阶段后的分类结果==============");
        printClusters();
	}
	private void init(int datanum, double mi) {
		W = new float[datanum][];
        for (int i = 0; i < datanum; i++) {
            W[i] = new float[datanum];
        }
        Conn = new byte[datanum][];
        for (int i = 0; i < datanum; i++) {
            // 由于是无向图，连接矩阵是对称的，所以我们只用矩阵的下三角以节约空间
            Conn[i] = new byte[i + 1];
        }
        clusters = new Vector<Vector<Integer>>();
        MI = mi;
	}
 
    // 构造weight矩阵。根据两点间距离的倒数计算两点的相似度，作为连接权重
    private void buildWeightMatrix(ArrayList<DataObject> objects) {
        for (int i = 0; i < W.length; i++) {
            W[i][i] = 1.0f;
            for (int j = i + 1; j < W[i].length; j++) {
                float dist = CommonUtils.euraDist(this.dataSet.get(i).getID(), this.dataSet.get(j).getID());
                W[i][j] = W[j][i] = 1 / (1 + dist);
            }
        }
    }
    // CHAMELEON第一阶段，按照K最邻近建立较小的子簇
    private void buildSmallCluster() {
        PriorityQueue<Entry<Integer, Float>> pq = new PriorityQueue<Entry<Integer, Float>>(
                this.k, new Comparator<Map.Entry<Integer, Float>>() {
                    public int compare(Entry<Integer, Float> arg0,
                            Entry<Integer, Float> arg1) {
                        return arg0.getValue().compareTo(arg1.getValue());
                    }
                });
        for (int i = 0; i < W.length; i++) {
            pq.clear();
            int j = 0;
            HashMap<Integer, Float> map = new HashMap<Integer, Float>();
            // 找到与object距离最小（亦即相似度最大）的K个点
            for (; j < this.k; j++) {
                map.clear();
                map.put(j, W[i][j]);
                pq.add(map.entrySet().iterator().next());
            }
            for (; j < W[i].length; j++) {
                if (W[i][j] > pq.peek().getValue()) {
                    pq.poll();
                    map.clear();
                    map.put(j, W[i][j]);
                    pq.add(map.entrySet().iterator().next());
                }
            }
            for (j = 0; j < this.k; j++) {
                Entry<Integer, Float> entry = pq.poll();
                if (i > entry.getKey())
                    Conn[i][entry.getKey()] = 1;
                else if (i < entry.getKey())
                    Conn[entry.getKey()][i] = 1;
                // 对角线上的设为0
            }
        }
        // 根据连接矩阵构造连通子图
        boolean[] visited = new boolean[W.length];
        boolean allvisited = false;
        while (!allvisited) {
            allvisited = true;
            L1: for (int i = 0; i < W.length; i++) {
                if (visited[i])
                    continue;
                allvisited = false;
                // 搜寻第i列，以图发现新子图的第一个点
                for (int j = i + 1; j < W.length; j++) {
                    // 发现了新子图的第一个点
                    if (Conn[j][i] == 1) {
                        Vector<Integer> cluster = new Vector<Integer>();
                        Queue<Integer> queue = new LinkedList<Integer>();
                        queue.add(i);
                        queue.add(j);
                        while (!queue.isEmpty()) {
                            int ele = queue.poll();
                            cluster.add(ele);
                            visited[ele] = true;
                            // 遍历第ele列
                            for (int k = ele + 1; k < W.length; k++) {
                                if (visited[k])
                                    continue;
                                if (Conn[k][ele] == 1 && !queue.contains(k)) {
                                    queue.add(k);
                                }
                            }
                            // 遍历第ele行
                            for (int k = 0; k < ele; k++) {
                                if (visited[k])
                                    continue;
                                if (Conn[ele][k] == 1 && !queue.contains(k))
                                    queue.add(k);
                            }
                        }
                        clusters.add(cluster);
                        break L1;
                    }
                }
            }
        }
    }
 
    // 打印子簇
    private void printClusters() {
        for (int i = 0; i < clusters.size(); i++) {
        	if(clusters.get(i).size()<35){
        		continue;
        	}
            System.out.print("以下数据点属于第" + i + "簇：");
            Iterator<Integer> iter = clusters.get(i).iterator();
            while (iter.hasNext()) {
                System.out.print(iter.next() + ",");
            }
            System.out.println();
        }
    }
 
    // CHAMELEON第二阶段，合并相对互联度RI和相对紧密度RC都较高的簇
    private void cluster() {
        int len = clusters.size();
        float[] EC1 = new float[len];
        for (int i = 0; i < len; i++) {
            Vector<Integer> vec = clusters.get(i);
            for (int j = 0; j < vec.size(); j++) {
                for (int k = 0; k < vec.size(); k++) {
                    EC1[i] += W[vec.get(j)][vec.get(k)];
                }
            }
        }
        boolean end = true;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                Vector<Integer> vec1 = clusters.get(i);
                Vector<Integer> vec2 = clusters.get(j);
                float EC = 0.0f;
                float RI = 0.0f;
                float SEC = 0.0f;
                float RC = 0.0f;
                for (int k = 0; k < vec1.size(); k++) {
                    for (int m = 0; m < vec2.size(); m++) {
                        EC += W[vec1.get(k)][vec2.get(m)];
                    }
                }
                RI = 2 * EC / (EC1[i] + EC1[j]);
                RC = (vec1.size() + vec2.size()) * EC
                        / (vec2.size() * EC1[i] + vec1.size() * EC1[j]);
                // 以RI*RC作为综合指数
                if (RI * RC > MI) {
                    mergeClusters(i, j);
                    end = false;
                    break;
                }
            }
        }
        // 递归合并子簇
        if (!end)
            cluster();
    }
 
    // 把簇b合并到簇a里面去
    private void mergeClusters(int a, int b) {
        Iterator<Integer> iter = clusters.get(b).iterator();
        while (iter.hasNext()) {
            clusters.get(a).add(iter.next());
        }
        clusters.remove(b);
    }
}