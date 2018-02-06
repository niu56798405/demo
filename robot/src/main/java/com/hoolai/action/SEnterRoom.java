package com.hoolai.action;

import com.hoolai.net.cmd.Cmd;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.session.Session;
import com.hoolai.westworld.base.code.MsgCode;
@Cmd(MsgCode.RoomServer.ENTER_ROOM)
public class SEnterRoom implements Command{
	
	@Override
	public void execute(Session session, IMessage req) throws Exception {				
		System.out.println("玩家进入房间 uid： " + session.id());		
	}

}
