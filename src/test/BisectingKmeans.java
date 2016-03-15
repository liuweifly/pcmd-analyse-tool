package test;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 二分k均值，实际上是对一个集合做多次的k=2的kmeans划分，
 * 每次划分后会对sse值较大的簇再进行二分。 最终使得或分出来的簇的个数为k个则停止
 */
public class BisectingKmeans {
	private int k;// 分成多少簇
	private ArrayList<float[]> dataSet;// 当前要被二分的簇
	private ArrayList<ClusterSet> cluster; // 簇

	public BisectingKmeans(int k) {
		// 比2还小有啥要划分的意义么
		if (k < 2) {
			k = 2;
		}
		this.k = k;
	}

	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */
	public void setDataFileSet(String srcFile,float threshold) throws FileNotFoundException, IOException {
		this.dataSet = CommonUtils.setDataFileSet(srcFile,threshold);
	}
	/**
	 * 获取中心
	 * 
	 * @return 中心集
	 */
	public ArrayList<float[]> getCenter() {
		ArrayList<float[]> center = new ArrayList<float[]>(); 
		for(int i=0;i<k;i++){
			center.add(cluster.get(i).getCenter());
		}
		return center;
	}
	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("BisectingKmeans begins");
		BiKmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("BisectingKmeans running time="
				+ (endTime - startTime) + "ms");
		System.out.println("BisectingKmeans ends");
		System.out.println();
	}

	/**
	 * 初始化
	 */
	private void init() {
		int dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
	}
	/**
	 * 计算误差平方和准则函数方法
	 */
	private float countRule( List<float[]> cluster,float[] center) {
		float jcF = 0;
		for (int j = 0; j < cluster.size(); j++) {
			jcF += CommonUtils.errorSquare(cluster.get(j), center);
		}		
	return  jcF;
	}
	/**
	 * Kmeans算法核心过程方法
	 */
	private void BiKmeans() {
		init();
		cluster = new ArrayList<ClusterSet>();
//		System.out.println(cluster.size());
		// 调用kmeans进行二分
		while (cluster.size() < k) {
			List<ClusterSet> clu = kmeans(dataSet);
			for (ClusterSet cl : clu) {
				cluster.add(cl);
			}
			if (cluster.size() == k)
				break;
			else// 顺序计算他们的误差平方和
			{
				// 顺序计算他们的误差平方和			
				float maxerro=0f;
				int maxclustersetindex=0;
				int i=0;
				System.out.println(cluster.size()-1);
				for (ClusterSet tt : cluster) {
					//计算误差平方和并得出误差平方和最大的簇
					float erroe = countRule(tt.getClu(), tt.getCenter());
					tt.setErro(erroe);
					if(maxerro<erroe)
					{
						maxerro=erroe;
						maxclustersetindex=i;
					}
					i++;
				}
				dataSet=cluster.get(maxclustersetindex).getClu();
				cluster.remove(maxclustersetindex);	
			}
		}
	}

	/**
	 * 调用kmeans得到两个簇。
	 * 
	 * @param dataSet
	 * @return
	 */
	private List<ClusterSet> kmeans(ArrayList<float[]> dataSet) {
		Kmeans k = new Kmeans(2);
		// 设置原始数据集
		k.setDataSet(dataSet);
		// 执行算法
		k.execute();
		// 得到聚类结果
		List<ArrayList<float[]>> clus = k.getCluster();
		List<ClusterSet> clusterset = new ArrayList<ClusterSet>();
		int i = 0;
		for (ArrayList<float[]> cl : clus) {
			ClusterSet cs = new ClusterSet();
			cs.setClu(cl);
			cs.setCenter(k.getCenter().get(i));
			clusterset.add(cs);
			i++;
		}
		return clusterset;
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
		ArrayList<float[]> temp = new ArrayList<float[]>();
		ArrayList<ArrayList<float[]>> newCluster = new ArrayList<ArrayList<float[]>>();
		for(int i=0;i<cluster.size();i++){
			temp =  cluster.get(i).getClu();
			newCluster.add(temp);
		}
		CommonUtils.printDataArray(destDir, newCluster);
	}
	class ClusterSet {
		private float erro;
		private ArrayList<float[]> clu;
		private float[] center;

		public float getErro() {
			return erro;
		}

		public void setErro(float erro) {
			this.erro = erro;
		}

		public ArrayList<float[]> getClu() {
			return clu;
		}

		public void setClu(ArrayList<float[]> clu) {
			this.clu = clu;
		}

		public float[] getCenter() {
			return center;
		}

		public void setCenter(float[] center) {
			this.center = center;
		}

	}
}