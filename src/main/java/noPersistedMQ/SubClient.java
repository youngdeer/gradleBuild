import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;


public class SubClient {

	private Jedis jedis;//  
    
    public SubClient(String host,int port){  
        jedis = new Jedis(host,port);  
    }  
      
    public void sub(JedisPubSub listener,String channel){  
        jedis.subscribe(listener, channel);  
        //�˴�������������client���뼶��ΪJedisPubSub�ڴ�����Ϣʱ�����ᡰ��ռ������  
        //���Ҳ�ȡ��whileѭ���ķ�ʽ���������ĵ���Ϣ  
    }  
}
