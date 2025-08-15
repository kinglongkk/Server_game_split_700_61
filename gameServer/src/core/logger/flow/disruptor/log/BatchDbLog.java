package core.logger.flow.disruptor.log;

import com.ddm.server.dispatcher.executor.BaseExecutor;
import core.db.entity.BaseClarkLogEntity;

/**
 * 派发器接口
 *
 */
public interface BatchDbLog {

    void publish(BaseClarkLogEntity executor);

    void init();

    void start();
}