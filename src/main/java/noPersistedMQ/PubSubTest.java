import redis.clients.jedis.JedisPubSub;


public class PubSubTest {

	public static void main(String[] args) throws Exception{
		PubClient pubClient = new PubClient("127.0.0.1",6379);
		final String channel = "pubsub-channel";
		pubClient.pub(channel, "before1");
		pubClient.pub(channel, "before2");
		Thread.sleep(2000);
		//��Ϣ�����ŷǳ����⣬��Ҫ��ռ���ӣ����������ҪΪ�������µ����ӣ�  
        //���⣬jedis�ͻ��˵�ʵ��Ҳ��֤�ˡ����Ӷ�ռ�������ԣ�sub������һֱ������  
        //ֱ������listener.unsubscribe����
		Thread subThread = new Thread(new Runnable(){

			@Override
			public void run() {
				SubClient subClient = new SubClient("127.0.0.1",6379);
				System.out.println("-------------subscribe operation begin-------------");
				JedisPubSub listener = new PrintListener();
				//��API���𣬴˴�Ϊ��ѯ������ֱ��unsubscribe���ã��Ż᷵��
				subClient.sub(listener, channel);
				System.out.println("-------------subscribe operation end-------------");
				
			}
			
		});
		subThread.start();
		int i = 0;
		while(i < 10){
			String message = "message_"+i;
			pubClient.pub(channel, message);
			i++;
			Thread.sleep(1000);
		}
		//�����ر�ָʾ�����ͨ���У���Ϣ������ȷ��ͨ����Ҫ�رգ���ô�ͷ���һ����quit��  
        //��ô��listener.onMessage()�н��յ���quit��ʱ����������client��ִ�С�unsubscribe��������
		pubClient.close(channel);
		//���⣬�㻹��������ȡ������  
        //listener.unsubscribe(channel);  
	}
}
