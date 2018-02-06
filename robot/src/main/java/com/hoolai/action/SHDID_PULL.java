package com.hoolai.action;

import com.hoolai.net.cmd.Cmd;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.codec.Message;
import com.hoolai.net.session.Session;
import com.hoolai.proto.FightAction.EnterRoom;
import com.hoolai.proto.FightAction.EnterRoom.Builder;
import com.hoolai.proto.FightEnum.RoomType;
import com.hoolai.proto.WestworldCheckMsg.BattleLoginCheck;
import com.hoolai.westworld.base.code.MsgCode;


@Cmd(MsgCode.RoomServer.HDID_PULL)
public class SHDID_PULL implements Command{
	@Override
	public void execute(Session session, IMessage req) throws Exception {	
			BattleLoginCheck blc = BattleLoginCheck.parseFrom(req.getBody());
			System.out.println("user登录成功 uid：" + blc.getHUid()+" ID: "+req.getId());
			long uid = Long.parseLong(blc.getHUid());
			session.bind(req.getId()); 			
			Builder builder = EnterRoom.newBuilder();
			builder.setHeroId(10001);
			builder.setRoomId(uid/10);
			builder.setRoomType(RoomType.Melee);
			Message msg = Message.build(MsgCode.RoomClient.ENTER_ROOM, builder.build().toByteArray());
			msg.setId(req.getId());
			session.sendMessage(msg);	
	}

}
