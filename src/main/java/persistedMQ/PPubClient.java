package persistedMQ;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class PPubClient {

	 private Jedis jedis;//  
	    public PPubClient(String host,int port){  
	        jedis = new Jedis(host,port);  
	    }  
	      
	    /** 
	     * ������ÿ����Ϣ������Ҫ�ڡ���������Ϣ���С��г־� 
	     * @param message 
	     */  
	    private void put(String message){  
	        //����������ϲ�Ҫ̫��  
	        Set<String> subClients = jedis.smembers("subscribe_center");  
	        for(String clientKey : subClients){  
	            jedis.rpush(clientKey, message);  
	        }  
	    }  
	      
	    public void pub(String channel,String message){  
	        //ÿ����Ϣ�����о���һ��ȫ��Ψһ��id  
	        //txidΪ�˷�ֹ���Ķ������ݴ���ʱ�����򡱣����Ҫ��������Ҫ����message  
	        Long txid = jedis.incr("message_txid");  
	        String content = txid + "/" + message;  
	        //������  
	        this.put(content);  
	        jedis.publish(channel, content);//Ϊÿ����Ϣ�趨id��������Ϣ��ʽ1000/messageContent  
	          
	    }  
	      
	    public void close(String channel){  
	        jedis.publish(channel, "quit");  
	        jedis.del(channel);//ɾ��  
	    }  
	      
	    public void test(){  
	        jedis.set("pub-block", "15");  
	        String tmp = jedis.get("pub-block");  
	        System.out.println("TEST:" + tmp);  
	    }  
}
