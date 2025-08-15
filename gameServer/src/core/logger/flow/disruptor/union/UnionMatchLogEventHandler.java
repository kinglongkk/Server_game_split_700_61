package core.logger.flow.disruptor.union;

import BaseCommon.CommLog;
import business.global.club.ClubMgr;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import core.db.entity.BaseClarkLogEntity;
import core.db.other.DBFlowMgr;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UnionMatchLogEventHandler implements EventHandler<UnionMatchBuffer>{

    @Override
    public void onEvent(UnionMatchBuffer batchDbLogBuffer, long sequence, boolean endOfBatch)  {
        try {
            ClubMgr.getInstance().getClubMemberMgr().unionMatchLog(batchDbLogBuffer.getExecutor().getClubIdList(),batchDbLogBuffer.getExecutor().getOwnerId(),batchDbLogBuffer.getExecutor().getPrizeType(),batchDbLogBuffer.getExecutor().getRanking(),batchDbLogBuffer.getExecutor().getValue(),batchDbLogBuffer.getExecutor().getRoundId(),batchDbLogBuffer.getExecutor().getUnionId(),batchDbLogBuffer.getExecutor().getClubId());
        } catch (Exception e) {
            CommLog.error("UnionMatchLogEventHandler Exception ", e);
        } finally {
            batchDbLogBuffer.clear();
        }
    }

}
