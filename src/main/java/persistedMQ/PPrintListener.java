package persistedMQ;

import java.util.Date;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class PPrintListener extends JedisPubSub{
	private String clientId;
	private PSubHandler handler;
	
	public PPrintListener(String clientId,Jedis jedis){
		this.clientId = clientId;
		handler = new PSubHandler(jedis);
	}
	
	@Override
	public void onMessage(String channel,String message){
		//�˴����ǿ���ȡ������
		if(message.equalsIgnoreCase("quit")){
			this.unsubscribe(channel);
		}
		handler.handle(channel, message);
	}
	
	private void message(String channel,String message){
		Date time = new Date();
		System.out.println("message receive:"+message+",channel:"+channel+"..."+time.toString());
	}
	
	@Override
	public void onPMessage(String pattern, String channel, String message){
		System.out.println("message receive:" + message + ",pattern channel:" + channel);  
	}

	@Override  
    public void onSubscribe(String channel, int subscribedChannels) {  
        handler.subscribe(channel);  
        System.out.println("subscribe:" + channel + ";total channels : " + subscribedChannels);  
          
    }  
  
    @Override  
    public void onUnsubscribe(String channel, int subscribedChannels) {  
        handler.unsubscribe(channel);  
        System.out.println("unsubscribe:" + channel + ";total channels : " + subscribedChannels);  
          
    }  
  
    @Override  
    public void onPUnsubscribe(String pattern, int subscribedChannels) {  
        System.out.println("unsubscribe pattern:" + pattern + ";total channels : " + subscribedChannels);  
          
    }  
  
    @Override  
    public void onPSubscribe(String pattern, int subscribedChannels) {  
        System.out.println("subscribe pattern:" + pattern + ";total channels : " + subscribedChannels);       
    }  
      
    @Override  
    public void unsubscribe(String... channels) {  
        super.unsubscribe(channels);  
        for(String channel : channels){  
            handler.unsubscribe(channel);  
        }  
    }  
	
	class PSubHandler {
		private Jedis jedis;
		PSubHandler(Jedis jedis){
			this.jedis = jedis;
		}
		public void handle(String channel,String message){
			int index = message.indexOf("/");
			if(index < 0){
				return;
			}
//			System.out.println("index:"+index+",message:"+message);
			Long txid = Long.valueOf(message.substring(0,index));
			String key = clientId + "/" + channel;
			while(true){
				String lm = jedis.lindex(key, 0);//��ȡ��һ����Ϣ
				if(lm == null){
					break;
				}
				int li = lm.indexOf("/");
				//�����Ϣ���Ϸ���ɾ��������
				if(li < 0){
					String result = jedis.lpop(key);//ɾ����ǰmessage
					//Ϊ��
					if(result == null){
						break;
					}
					message(channel,lm);
					continue;
				}
				Long lxid = Long.valueOf(lm.substring(0, li));//��ȡ��Ϣ��txid
				//ֱ������txid֮ǰ�Ĳ�����Ϣ
				if(txid >= lxid){
					jedis.lpop(key);//ɾ����ǰmessage
					message(channel,lm);
					continue;
				}else{
					break;
				}
			}
		}
		
	   public void subscribe(String channel){  
            String key = clientId + "/" + channel;  
            boolean exist = jedis.sismember("subscribe_center",key);  
            if(!exist){  
                jedis.sadd("subscribe_center", key);  
            }  
        }  
	          
        public void unsubscribe(String channel){  
            String key = clientId + "/" + channel;  
            jedis.srem("subscribe_center", key);//�ӡ���Ծ�����ߡ�������ɾ��  
            jedis.del(key);//ɾ������������Ϣ���С�  
        } 
	}
}
