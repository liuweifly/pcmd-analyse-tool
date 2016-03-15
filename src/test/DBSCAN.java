package test;
 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
 
public class DBSCAN  {
 
    double Eps=4;   //区域半径 2.5
    int MinPts=4;   //密度
    private int k;  //最后Top多少簇
    private int clusterNum=0; //DBSCAN分成多少簇 
    private ArrayList<DataObject> dataSet;
    private ArrayList<ArrayList<float[]>> newCluster;
	/**
	 * 构造函数，传入需要分成的簇数量
	 * 
	 * @param k簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public DBSCAN(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
	}

	/**
	 * 设置需分组的原始数据集
	 * @param dataSet
	 */
	public void setDataFileSet(String srcFile,float threshold) throws FileNotFoundException, IOException {
		ArrayList<float[]> fileSet = CommonUtils.setDataFileSet(srcFile,threshold);
		this.dataSet = new ArrayList<DataObject>();
		for(int i=0;i<fileSet.size();i++){
			DataObject temp = new DataObject(false,0,fileSet.get(i));//注意，temp初始化不能定义for循环外
//			System.out.println(i+": "+temp.getID()[0]+" "+temp.getID()[1]);
			this.dataSet.add(temp);
		}
	}	
	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("DBSCAN begins");
		dbscan();
		long endTime = System.currentTimeMillis();
		System.out.println("DBSCAN running time="+ (endTime - startTime) + "ms");
		System.out.println("DBSCAN ends");
		System.out.println();
	}
     
    private void dbscan(){    	
        boolean AllVisited=false;
        while(!AllVisited){
            Iterator<DataObject> iter=this.dataSet.iterator();
            while(iter.hasNext()){
                DataObject p=iter.next();
                if(p.isVisited())
                    continue;
                AllVisited=false;
                p.setVisited(true);     //设为visited后就已经确定了它是核心点还是边界点
                ArrayList<DataObject> neighbors=getNeighbors(p,this.dataSet);
//                System.out.println(neighbors.size());
                if(neighbors.size()<MinPts){
                    if(p.getCid()<=0)
                        p.setCid(-1);       //cid初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声。
                }else{
                    if(p.getCid()<=0){
                    	this.clusterNum++;
                        expandCluster(p,neighbors,this.clusterNum,this.dataSet);
                    }else{
                        int iid=p.getCid();
                        expandCluster(p,neighbors,iid,this.dataSet);
                    }
                }
                AllVisited=true;
            }
        }
        System.out.println("Num of cluster: "+this.clusterNum);
    }
    
   //由于自己到自己的距离是0,所以自己也是自己的neighbor
   private ArrayList<DataObject> getNeighbors(DataObject p,ArrayList<DataObject> cluster){
   	ArrayList<DataObject> neighbors=new ArrayList<DataObject>();
       for(int i=0;i<cluster.size();i++){
       	if(CommonUtils.errorSquare(this.dataSet.get(i).getID(), p.getID())<=(Eps*Eps)){ 
       		neighbors.add(cluster.get(i));
       	}      	
       }
       return neighbors;
   }
    private void expandCluster(DataObject p, ArrayList<DataObject> neighbors,
            int clusterID,ArrayList<DataObject> objects) {
        p.setCid(clusterID);
        Iterator<DataObject> iter=neighbors.iterator();
        while(iter.hasNext()){
            DataObject q=iter.next();
            if(!q.isVisited()){
                q.setVisited(true);
                ArrayList<DataObject> qneighbors=getNeighbors(q,objects);
                if(qneighbors.size()>=MinPts){
                    Iterator<DataObject> it=qneighbors.iterator();
                    while(it.hasNext()){
                        DataObject no=it.next();
                        if(no.getCid()<=0)
                            no.setCid(clusterID);
                    }
                }
            }
            if(q.getCid()<=0){       //q不是任何簇的成员
                q.setCid(clusterID);
            }
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
		int[] CidTop = new int[this.clusterNum];
		int[] IDNum = new int[this.clusterNum];
		int[] CallsNum = new int[this.clusterNum];
		int[] GridsNum = new int[this.clusterNum];
		HashMap<String, String> hm = CommonUtils.setCallsHm("src/smooth/grid123CallSmooth.txt");
		for(int i=0;i<this.clusterNum;i++){
			IDNum[i] = 0;
			CallsNum[i] = 0;
			GridsNum[i] = 0;			
		}
		newCluster = new ArrayList<ArrayList<float[]>>();
		for(int j=0;j<this.dataSet.size();j++){
			int id = this.dataSet.get(j).getCid();
			if(id>0){
				GridsNum[id-1]++;					
				float[] ID = this.dataSet.get(j).getID();
				int x = (int)ID[0];
				int y = (int)ID[1];
				String point = x + "^" + y;
				if(hm.containsKey(point)){  //一点与两三点数据同时出现在同一栅格。取1倍的两三点话单质量，0倍的一点话单质量
			    	  int calls = (int)Float.parseFloat(hm.get(point));
			    	  CallsNum[id-1] += calls;
	//		    	  System.out.println(point+","+calls);
				}
			}
		}
		for(int i=0;i<this.clusterNum;i++){
			IDNum[i] = GridsNum[i] + CallsNum[i]/500;
			System.out.println(i+1+","+GridsNum[i]+","+CallsNum[i]);			
		}
//		for(int i=0;i<this.clusterNum;i++){
//			System.out.println((i+1)+" "+IDNum[i]);
//		}
		CidTop = order(GridsNum);	//对簇按数量排序，取前top k的Cid
//		System.out.println("ddd"+CidTop[10]);
		for(int i=0;i<this.k;i++){
			ArrayList<float[]> temp = new ArrayList<float[]>();
			for(int j=0;j<this.dataSet.size();j++){
				if(this.dataSet.get(j).getCid()==(CidTop[i]+1)){
					temp.add(this.dataSet.get(j).getID());
				}
			}			
			newCluster.add(temp);
		}
		CommonUtils.printDataArray(destDir, newCluster);
		System.out.println();
//		for(int i=0;i<this.dataSet.size();i++){
//			System.out.println(this.dataSet.get(i).getID()[0]+" "+this.dataSet.get(i).getID()[1]
//					+" "+this.dataSet.get(i).getCid()+" "+this.dataSet.get(i).isVisited());
//		}
	}
    public ArrayList<float[]> getCenter() {
    	ArrayList<float[]> center = new ArrayList<float[]>();// 中心链表
    	float[] temp={0,0};
    	for(int i=0;i<this.k;i++){
    		int l = newCluster.get(i).size();
    		float xNum=0,yNum=0;
    		for(int j=0;j<l;j++){
    			float[] a = newCluster.get(i).get(j);
//    			System.out.println(a[0]+",,"+a[1]);
    			xNum += a[0];
    			yNum += a[1];
    		}
    		temp[0] = (float)Math.floor(xNum / l);
    		temp[1] = (float)Math.floor(yNum / l);
    		System.out.println("center:"+temp[0]+","+temp[1]);
    		center.add(new float[]{temp[0],temp[1]});
    	}
    	return center;
    }
    public int[] order(int[] a) {
        //冒泡排序
    	int[] CidTop = new int[this.k];
    	List<OrderObject> orderArray = new ArrayList<OrderObject>();
    	for(int k=0;k<a.length;k++){
    		OrderObject temp = new OrderObject();
    		temp.setCid(k);
    		temp.setNum(a[k]);
    		orderArray.add(temp);
    	}
//    	for(int i=0;i<orderArray.size();i++){
//    		System.out.println(i+"::: "+orderArray.get(i).getNum()+" "+orderArray.get(i).getCid());
//    	}
    	System.out.println("num:"+orderArray.size());
    	Collections.sort(orderArray);//排序
    	for(int i=0;i<this.k;i++){
    		System.out.println(i+": "+orderArray.get(i).getNum()+" "+(orderArray.get(i).getCid()+1));
    		CidTop[i] = orderArray.get(i).getCid();
    	}
        return CidTop;
    }	

    class OrderObject implements Comparable<OrderObject>{
    	private int Num;
    	private int Cid;
    	public OrderObject(){};
		public int getNum() {
			return Num;
		}
		public void setNum(int num) {
			Num = num;
		}
		public int getCid() {
			return Cid;
		}
		public void setCid(int cid) {
			Cid = cid;
		}
		public int compareTo(OrderObject other){
	    	if(other.Num>this.Num){
	    		return 1;
	    	}
	    	else if (other.Num<this.Num){
	    		return -1;
	    	}else{
	    		return 0;
	    	}
	    }	
    }
    public static class DataObject {
    	private boolean visited;
		private int Cid;
		private float[] ID;
		
		public DataObject(boolean visited,int Cid,float[] ID){
			this.visited = visited;
			this.Cid = Cid;
			this.ID = ID;
		}
		public boolean isVisited() {
			return visited;
		}
		public void setVisited(boolean visited) {
			this.visited = visited;
		}
		public int getCid() {
			return Cid;
		}
		public void setCid(int cid) {
			Cid = cid;
		}
		public float[] getID() {
			return ID;
		}
		public void setID(float[] ID) {
			this.ID = ID;
		}
		
	}
}