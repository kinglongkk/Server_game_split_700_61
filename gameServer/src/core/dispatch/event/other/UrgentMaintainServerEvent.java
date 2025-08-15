package core.dispatch.event.other;

import business.global.GM.MaintainServerMgr;
import business.global.club.ClubMgr;
import business.global.union.UnionMgr;
import cenum.DispatcherComponentEnum;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.dispatcher.executor.BaseExecutor;
import lombok.Data;


@Data
public class UrgentMaintainServerEvent implements BaseExecutor {

    @Override
    public void invoke() {
        MaintainServerMgr.getInstance().urgentMaintainServer();
    }

    @Override
    public int threadId() {
        return DispatcherComponentEnum.MAINTAIN.id();
    }

    @Override
    public int bufferSize() {
        return DispatcherComponentEnum.MAINTAIN.bufferSize();
    }
}
