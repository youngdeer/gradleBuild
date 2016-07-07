import java.util.Date;

import redis.clients.jedis.JedisPubSub;


public class PrintListener extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message){
		Date time = new Date();  
        System.out.println("message receive:" + message + ",channel:" + channel + "..." + time.toString());  
        //此处我们可以取消订阅  
        if(message.equalsIgnoreCase("quit")){  
            this.unsubscribe(channel);  
        }
	}
}
