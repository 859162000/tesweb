package com.dc.tes.ui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	public static final int BUFFER = 1024 ; 
	
	@SuppressWarnings("rawtypes")
	public static void ZipFile(String baseDir,String fileName) throws Exception{  
	    List fileList=getSubFiles(new File(baseDir));  
	    ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(fileName));  
	    ZipEntry ze=null;  
	    byte[] buf=new byte[BUFFER];  
	    int readLen=0;  
	    for(int i = 0; i <fileList.size(); i++) {  
	        File f=(File)fileList.get(i);  
	        ze=new ZipEntry(getAbsFileName(baseDir, f));  
	        ze.setSize(f.length());
	        ze.setTime(f.lastModified());     
	        zos.putNextEntry(ze);  
	        InputStream is=new BufferedInputStream(new FileInputStream(f));  
	        while ((readLen=is.read(buf, 0, BUFFER))!=-1) {  
	            zos.write(buf, 0, readLen);  
	        }  
	        zos.setEncoding("GBK");
	        is.close();  
	    }  
	    zos.close();  
	} 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List getSubFiles(File baseDir){  
	    List ret=new ArrayList();  
	    File[] tmp=baseDir.listFiles();  
	    for (int i = 0; i <tmp.length; i++) {  
	        if(tmp[i].isFile())  
	            ret.add(tmp[i]);  
	        if(tmp[i].isDirectory())  
	            ret.addAll(getSubFiles(tmp[i]));  
	    }  
	    return ret;  
	} 
		
	private static String getAbsFileName(String baseDir, File realFileName){  
	    File real=realFileName;  
	    File base=new File(baseDir);  
	    String ret=real.getName();  
	    while (true) {  
	        real=real.getParentFile();  
	        if(real==null)   
	            break;  
	        if(real.equals(base))   
	            break;  
	        else 
	            ret=real.getName()+"/"+ret;  
	    }  
	    return ret;  
	}	
		
	public static void DeleteDirFile(String path){
	    File file=new File(path);
	    if(file.isDirectory()){       //如果是目录，先递归删除
	        String[] list=file.list();
	        for(int i=0;i<list.length;i++){
	         DeleteDirFile(path+"\\"+list[i]);  //先删除目录下的文件
	        }
	    }       
	    file.delete();
	}
}
