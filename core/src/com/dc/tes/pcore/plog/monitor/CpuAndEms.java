package com.dc.tes.pcore.plog.monitor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.StringTokenizer;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class CpuAndEms {   

    static String osName = System.getProperty("os.name");        
         
    private static String linuxVersion = null;  
    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory     
    .getOperatingSystemMXBean();   
  

    public static void main(String []args) {
    	Date d = new Date();
    	for(int i = 0;i <100;i++){
        	System.out.println(CpuAndEms.getCPU());
        	System.out.println(CpuAndEms.getEMS());
    	}
     	Date d2 = new Date();
    	System.out.println(d2.getTime()-d.getTime());
    }

	public static int getCPU(){
		 int cpuRatio = 0;
		try {
			double a = new Sigar().getCpuPerc().getCombined();
			cpuRatio = (int)( a*100);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return cpuRatio;
	}
    
	public static double getEMS(){
		 // 总的物理内存     
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize();     
         // 已使用的物理内存     
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb     
                .getFreePhysicalMemorySize()); 
		return  usedMemory*100/totalMemorySize ;
	}
	   
}
