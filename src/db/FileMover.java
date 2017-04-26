package db;

import java.io.File;

public class FileMover {
	
	public static void main(String[] args) {
		
		File directory = new File("E:\\eclipse_bugreport\\data");
		
		File[] files = directory.listFiles();
		
		System.out.println(files.length);

		for(int i = 0 ; i<files.length; i++){
			if(i%1000 == 0)
				System.out.println((i*1.0)/files.length);
			File file = files[i];
			int bugID = Integer.parseInt(file.getName().toLowerCase().replace("bug", "").replace("html", "").replace(".", ""));
			if(bugID <=50000){
				File newDir = new File("E:\\eclipse_bugreport\\1-50000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=100000){
				File newDir = new File("E:\\eclipse_bugreport\\50001-100000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=150000){
				File newDir = new File("E:\\eclipse_bugreport\\100001-150000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=200000){
				File newDir = new File("E:\\eclipse_bugreport\\150001-200000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=250000){
				File newDir = new File("E:\\eclipse_bugreport\\200001-250000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=300000){
				File newDir = new File("E:\\eclipse_bugreport\\250001-300000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=350000){
				File newDir = new File("E:\\eclipse_bugreport\\300001-350000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=400000){
				File newDir = new File("E:\\eclipse_bugreport\\350001-400000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=450000){
				File newDir = new File("E:\\eclipse_bugreport\\400001-450000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else if(bugID <=500000){
				File newDir = new File("E:\\eclipse_bugreport\\450001-500000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}else{
				File newDir = new File("E:\\eclipse_bugreport\\500001-600000\\");
				newDir.mkdir();
				File reFile = new File(newDir, bugID+".html");
				file.renameTo(reFile);
			}
		}
			
		
	}

}
