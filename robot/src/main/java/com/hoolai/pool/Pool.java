package com.hoolai.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

import com.hoolai.net.kcp.Segment;
  
public class Pool {  
  
    public static void main(String[] args) throws Exception {  
        long st = System.currentTimeMillis();
    	ObjectPool<Segment> pool = new StackObjectPool<Segment>(new UserFactory());  
        for(int i =0; i< 100000000;i++){
        	Segment u = pool.borrowObject(); // 从池中借出一个对象      
//        	Segment u = new Segment(128); // 从池中借出一个对象      
//        	u.release();
            pool.returnObject(u); // 归还对象              
        }
        long end = System.currentTimeMillis();
        System.out.println("time:"+(end - st));
        
    }  
    
   private static ObjectPool<Segment> pool = new StackObjectPool<Segment>(new UserFactory());  

   public static Segment borrowObject(){      
	try {		
		return pool.borrowObject();
	} catch (Exception e) {
		e.printStackTrace();
	}       
       return null;
   }
   
   public static void returnObject(Segment u){
       try {
    	u.release();   
		pool.returnObject(u);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // 归还对象  

   }
   
   public void close(){
	   try {
		pool.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   
    
    static class UserFactory extends BasePoolableObjectFactory<Segment> {  
        /** 
         * 产生一个新对象 
         */  
        public Segment makeObject() {  
            return new Segment(128);  
        }  
  
        /** 
         * 还原对象状态 
         */  
        public void passivateObject(Segment obj) {  
        	obj.release();  
        }  
    }  
   
}  