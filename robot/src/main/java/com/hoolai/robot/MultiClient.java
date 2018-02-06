package com.hoolai.robot;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hoolai.injection.ApplicationContext;
import com.hoolai.net.cmd.Command;
import com.hoolai.net.cmd.CommandContext;
import com.hoolai.net.codec.IMessage;
import com.hoolai.net.codec.Message;
import com.hoolai.net.codec.MessageUtil;
import com.hoolai.net.kcp.Kcp;
import com.hoolai.net.kcp.KcpOnUdp;
import com.hoolai.net.kcp.KcpSession4Server;
import com.hoolai.net.kcp.KcpThread;
import com.hoolai.net.kcp.MultiUdpClient;
import com.hoolai.net.kcp.Output;
import com.hoolai.net.session.Session;
import com.hoolai.proto.WestworldCheckMsg.BattleLoginCheck;
import com.hoolai.westworld.base.code.MsgCode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

public class MultiClient extends MultiUdpClient implements Output{
	 private Logger logger = LoggerFactory.getLogger(MultiClient.class);
	    
	 private CommandContext cmds;
	 
	 private KcpThread[] workers = new KcpThread[10];
	 InetSocketAddress server;
	 
	 AtomicInteger num = new AtomicInteger(0); 
	 
	 ReentrantLock lock = new ReentrantLock();
	 
	public MultiClient(InetSocketAddress server,int robotNum) {
		super(robotNum);
		this.cmds = ApplicationContext.fetchBean(CommandContext.class);
		this.server = server;
		start();
	
	}
	  /**
	   * 开始
	   */
	  public void start()
	  {
	 
	      for (int i = 0; i < this.workers.length; i++)
	      {
	        workers[i] = new KcpThread(this, this);
	        workers[i].setName("kcp thread " + i);
	        workers[i].wndSize(128, 128);
	        workers[i].noDelay(1, 10, 2, 1);
	        workers[i].setMtu(256);
	        workers[i].setTimeout(60 * 1000);
	        workers[i].setMinRto(10);
	        workers[i].setStream(false);
	        workers[i].start();
	      }
	    
	  }

	  @Override
	  public void handleReceive(ByteBuf bb, KcpOnUdp kcp){ 	   
		
		  exec(kcp.getKcpSession(), MessageUtil.decode(bb));
	  }

	  @Override
	  public void handleException(Throwable ex, KcpOnUdp kcp){
		ex.printStackTrace();  	
	  }

	  @Override
	  public void handleClose(KcpOnUdp kcp)
	  {
	
		 
	  }
	  

	  private void exec(Session session, IMessage req) {      
	      	      
	      short code = req.getCode();
	      Command cmd = cmds.get(code);      
	      if(cmd == null) {
	          return;
	      }
	      
	      try {
		        cmd.execute(session, req);
		    } catch (Throwable ex) {
		    }
	  }

	@Override
	protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new UdpHandler(server));
		
	}
	@Override
	public void out(ByteBuf msg, Kcp kcp, Object user) {
		Channel channel= getKcpThread((InetSocketAddress)user).
		getKcpOnUdp((InetSocketAddress)user).getKcpSession().channel();
		DatagramPacket temp = new DatagramPacket(msg, kcp.getLocal());
	    channel.writeAndFlush(temp);
	}
	
	 public KcpThread getKcpThread(InetSocketAddress sender){	  
	      int hash = Math.abs(sender.hashCode());
	      return this.workers[hash % workers.length];
	 }

	

	
	    /**
	     * handler
	     */
	    public class UdpHandler extends ChannelInboundHandlerAdapter
	    {
	      
	    	InetSocketAddress server;
	  	public UdpHandler(InetSocketAddress server) {
	  		super();
	  		this.server=server;
	  	}
	  		

	  	@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			lock.lock();
			 Thread.sleep(10);
	  	    InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
	        KcpThread thread = MultiClient.this.getKcpThread(address);   	    
	        KcpOnUdp  kcpOnUdp = thread.buildKcp(address,  server);
	      	kcpOnUdp.setKcpSession(new KcpSession4Server(ctx.channel(), kcpOnUdp));	 	  	
  		   BattleLoginCheck.Builder builder = BattleLoginCheck.newBuilder();
		   builder.setHUid(""+ num.incrementAndGet());		   
		   kcpOnUdp.getKcpSession().sendMessage(Message.build(MsgCode.RoomClient.HDID_PULL , builder.build().toByteArray()));
		   lock.unlock();
	  	
	  	}


		@Override
	      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	      {
	        DatagramPacket dp = (DatagramPacket) msg;
	        InetSocketAddress address = dp.recipient();
	        KcpThread thread = MultiClient.this.getKcpThread(address);   
	        KcpOnUdp kcpOnUdp = thread.getKcpOnUdp(address);
	        if(kcpOnUdp != null){
	        	kcpOnUdp.input(dp.content());
	        }else{
	        	dp.release();
	        }	        
	      }

		      
//	  	@Override
//	      public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//	  	    Session session = new Session4Server(ctx.channel(), listener);
//	  	    listener.onSessionRegister(session);
//	      }
	  //
//	      @Override
//	      public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//	          listener.onSessionUnRegister(Session.get(ctx));
//	      }
	         
	    }


	 
	 
	 
}
