package com.hoolai.action;

import com.hoolai.net.cmd.Cmd;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.codec.Message;
import com.hoolai.net.session.Session;
import com.hoolai.proto.FightModel.RoomData;
import com.hoolai.westworld.base.code.MsgCode;
@Cmd(MsgCode.RoomServer.ROOM_DATA)
public class SRoomData implements Command{
	
	@Override
	public void execute(Session session, IMessage req) throws Exception {	
		RoomData data = RoomData.parseFrom(req.getBody());
		Message m= Message.build(MsgCode.RoomClient.PLAYER_ENTERD);
		m.setId(session.id());
		session.sendMessage(m);		
		System.out.println("玩家进入房间 uid： " + session.id() + " room ID :" + data.getId());		
	}

}
