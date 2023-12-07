package albumstore;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ChannelFactory extends BasePooledObjectFactory<Channel> {
  private final Connection connection;

  public ChannelFactory(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Channel create() throws Exception {
    return connection.createChannel();
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }

  @Override
  public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
    Channel channel = pooledObject.getObject();
    if (channel.isOpen()) {
      channel.close();
    }
    super.destroyObject(pooledObject);
  }
}
