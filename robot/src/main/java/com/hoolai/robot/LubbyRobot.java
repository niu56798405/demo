package com.hoolai.robot;





import java.util.Scanner;

import com.hoolai.injection.ApplicationContext;
import com.hoolai.net.LifecycleListener;
import com.hoolai.net.NetClient;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.cmd.CommandContext;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.codec.Message;
import com.hoolai.net.session.Session;
import com.hoolai.proto.UserProtoMsg.Login;
import com.hoolai.westworld.base.code.MsgCode;

public class LubbyRobot {
    
	


    
    public static void main(String[] args) {
    	System.out.println(">>>>>>>>>.请输入机器人个数 ：");
    	Scanner sc = new Scanner(System.in);      	
    	int num = Integer.valueOf(sc.nextLine());
		ApplicationContext.initialize("*-SNAPSHOT.jar;com.hoolai.*", null);
		System.out.println(">>>>>>>>>.请输入ip ：");
		String ip =  sc.nextLine();    
//		System.out.println(">>>>>>>>>.请输入port ：");
		int port =  8000;
			
		for(long i = 100000; i < num+100000; i++){
			NetClient client = new NetClient(new RobotLister(),  ApplicationContext.fetchBean(CommandContext.class), MsgCode.UserClient.HEARTBEAT);
			Session session = client.connect(ip, port, i);
			Login.Builder builder = Login.newBuilder();
			builder.setHUid(""+ i);
			String str = "{\"affiliate\":\"WIFI\",\"ram\":2117672960,\"ratio\":\"720*1280\",\"phone_type\":\"Honor\",\"client_version_code\":\"1\",\"hoolai_channel_name\":\"taptap\",\"service\":\"中国移动\",\"phone_version\":19,\"CPU\":\"ARMv7 processor rev 1 (v7l)\",\"hoolai_channel_code\":\"c_1\",\"udid\":\"317069916507197\",\"client_version\":\"10102\"}";
			builder.setPhoneInfo(str);
			session.sendMessage(Message.build(MsgCode.UserClient.LOGIN , builder.build().toByteArray()));						
		
		}
		    	
	}
    
   public static class RobotLister implements LifecycleListener{

		@Override
		public void onSessionRegister(Session session) {
			System.out.println("session 已连接 ID: " + session.id());
		}

		@Override
		public void onMessageRecieve(Session session, IMessage message) {

			
		}

		@Override
		public void onMessageSending(Session session, IMessage message) {
		
			
		}

		@Override
		public void onSessionUnRegister(Session session) {
			System.out.println("session 已断开 ID: " + session.id());			
		}

		@Override
		public void onCmdException(Session session, Command cmd, IMessage req, Throwable ex) {

			
		}
    	
    }
    
}

	
	

