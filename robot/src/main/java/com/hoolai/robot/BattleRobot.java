package com.hoolai.robot;

import java.net.InetSocketAddress;
import java.util.Scanner;

import com.hoolai.injection.ApplicationContext;

public class BattleRobot {
	   public static void main(String[] args) {
	        try {
	        	System.out.println(">>>>>>>>>.请输入机器人个数 ：");
	        	Scanner sc = new Scanner(System.in);      	
	        	int num = Integer.valueOf(sc.nextLine());
	    		ApplicationContext.initialize("*-SNAPSHOT.jar;com.hoolai.*", null);
	    		System.out.println(">>>>>>>>>.请输入ip ：");
	    		String ip =  "119.29.104.252";       	
	    	   	MultiClient client = new MultiClient(new InetSocketAddress(ip, 2018),num);
	    	   	
	    	   	client.connect(); 
	            System.out.println("client:" + num);
	        	        	
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}	

