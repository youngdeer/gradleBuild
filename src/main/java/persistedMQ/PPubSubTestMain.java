package persistedMQ;

public class PPubSubTestMain {

	public static void main(String[] args) throws Exception{  
        PPubClient pubClient = new PPubClient("127.0.0.1",6379);  
        final String channel = "pubsub-channel-p";  
        final PSubClient subClient = new PSubClient("127.0.0.1",6379,"subClient-1");  
        Thread subThread = new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
                System.out.println("----------subscribe operation begin-------");  
                //在API级别，此处为轮询操作，直到unsubscribe调用，才会返回  
                subClient.sub(channel);  
                System.out.println("----------subscribe operation end-------");  
                  
            }  
        });  
        subThread.setDaemon(true);  
        subThread.start();  
        int i = 0;  
        while(i < 2){  
        	String message = "message_"+i;
            pubClient.pub(channel, message);  
            i++;  
            Thread.sleep(1000);  
        }  
        subClient.unsubscribe(channel);  
    }  
}
