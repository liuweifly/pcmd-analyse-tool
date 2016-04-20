package test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Station {
	public static void stationDisplay() throws FileNotFoundException, IOException{
		System.out.println("stationDisplay()");
		String JsonContext = CommonUtils.ReadJsonFile("src/input/station.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/initial/stationSift.txt"));
        JSONArray jsonArray = JSONArray.fromObject(JsonContext);
        int size = jsonArray.size();
        int num=0;
        double lng_origin = 113.690514, lat_origin = 29.961036;
        
        //把基站经纬度读到并转化为ID，ID变为像素，再调用DrawPNGMap显示
        for(int  i = 0; i < size; i++){
	        JSONObject jsonObject = jsonArray.getJSONObject(i);
	        double lng = (double) jsonObject.get("lng");
	        double lat = (double) jsonObject.get("lat");
	        int x = (int)Math.floor((lng - lng_origin)/0.00104)-7;
	        int y = (int)Math.floor((lat - lat_origin)/0.00090)-11;
//	        if(x>0&&y>0){
//	        	String id = x + " " + y +" "+-1603;
//	        	bw.write(id+"\r\n");
//	        }
	        if(x>=340&&x<=447){
	            if(y>=689&&y<=785){
	            	String in = x + " " + y+" "+-1603;
	                bw.write(in+"\r\n");
	                num++;
	            }
	        }
        }
        bw.close();
        System.out.println("StationSize: " + num);
//        GenerateMapPNG.DrawPNG("src/initial/station.txt","src/transform/stationID.txt","src/draw/station.png");
	}
	public static void stationTest() throws FileNotFoundException, IOException{
		
	}
}

