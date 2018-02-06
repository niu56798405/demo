package com.hoolai.action;

import java.util.Random;

import com.hoolai.net.cmd.Cmd;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.codec.Message;
import com.hoolai.net.session.Session;
import com.hoolai.proto.FightAction.ActionMove;
import com.hoolai.westworld.base.code.MsgCode;
@Cmd(MsgCode.RoomServer.ROOM_MSG_UPDATE)
public class SUpdateRoomMsg implements Command{
	
	@Override
	public void execute(Session session, IMessage req) throws Exception {				
		System.out.println("roomMsgUpdate： " + session.id());
		Random dom = new Random();
		
		ActionMove.Builder ab = ActionMove.newBuilder();
		ab.setAngle(0);
		
		ab.setTargetX(dom.nextInt(10));
		ab.setTargetZ(dom.nextInt(10));
		Message m = Message.build(MsgCode.RoomClient.ACTION_MOVE, ab.build()
				.toByteArray());
		m.setId(session.id());
		session.sendMessage(m);
	
				
	}

}
