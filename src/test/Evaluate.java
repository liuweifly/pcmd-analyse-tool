package test;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Evaluate {

	public static void execute(String srcFile,ArrayList<float[]> center,
			float coefficient,int initialValue,int radius, int num, String destDir) throws FileNotFoundException, IOException {
		//导入数据
		HashMap<String, Float> hm = CommonUtils.setGridData(srcFile);
		CommonUtils.printGridData("src/evaluate/grid123KEsss.txt", hm);
		if(center.size()<num){
			num = center.size();
		}	
		for(int i=0;i<num;i++){
//			System.out.println(center.get(i)[0]+","+center.get(i)[1]);
		}
		System.out.println("Station Number: "+num);
		for(int i=0;i<num;i++){
			int centerXMin = (int)center.get(i)[0]-radius;
			if(centerXMin<0) centerXMin=0;
			for(int x= centerXMin;x<(int)(center.get(i)[0]+radius);x++){
				int centerYMin = (int)center.get(i)[1]-radius;
				if(centerYMin<0) centerYMin=0;
				for(int y=centerYMin;y<(int)(center.get(i)[1]+radius);y++){
					float distance = CommonUtils.euraDist(center.get(i), new float[]{x,y});
					if(distance>radius){
						continue;
					}
					float improveValue = (float) (initialValue + coefficient*distance*0.1);
					if(improveValue<0){
						improveValue = 0;
					}
//					if(improveValue)
					String p =x+"^"+y;
					if(hm.containsKey(p)){
						float j = hm.get(p)+improveValue;
//						System.out.println(p+" "+improveValue);
						hm.put(p, j);
					}
				}
			}
		}
		//输出结果
		CommonUtils.printGridData(destDir, hm);
	}
}