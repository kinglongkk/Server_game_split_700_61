package core.dispatch.event.union;

import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.shareunion.ShareUnionListMgr;
import business.global.union.Union;
import business.global.union.UnionMgr;
import business.player.Player;
import business.player.PlayerMgr;
import cenum.ConstEnum;
import cenum.DispatcherComponentEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.utils.CommMath;
import com.ddm.server.dispatcher.executor.BaseExecutor;
import core.db.other.Restrictions;
import core.db.service.clarkGame.ClubMemberRelationBOService;
import core.db.service.clarkGame.PromotionLevelRoomConfigScorePercentBOService;
import core.db.service.clarkGame.UnionRoomConfigScorePercentBOService;
import core.ioc.ContainerMgr;
import core.logger.flow.FlowLogger;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.cclass.club.QueryUidOrPuidItem;
import jsproto.c2s.cclass.union.UnionDefine;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class UnionCloseCase implements BaseExecutor {

    /**
     * 赛事id
     */
    private long unionId;

    public UnionCloseCase(long unionId) {
        this.unionId = unionId;
    }

    @Override
    public void invoke() {
        Union union = UnionMgr.getInstance().getUnionListMgr().findUnion(this.getUnionId());
        List<Long> clubIdList=UnionMgr.getInstance().getUnionMemberMgr().getUnionMemberClubIdList(unionId);
        CommLogD.info("clubIdList:"+clubIdList.toString());
        for(Long clubID:clubIdList){
            List<ClubMember> clubMemberList=ClubMgr.getInstance().getClubMemberMgr().getClubMemberMap().values().stream().filter(k -> k.getClubID() == clubID && k.getStatus(Club_define.Club_Player_Status.PLAYER_JIARU.value()))
                    .collect(Collectors.toList());
            clubMemberList.stream().forEach(k->{
                Player player= PlayerMgr.getInstance().getPlayer(k.getClubMemberBO().getPlayerID());
                if(Objects.nonNull(player)){
                    k.getClubMemberBO().closeCaseSportsPoint(player,UnionDefine.UNION_EXEC_TYPE.UNION_CASE_SPORTS_POINT_CLOSE);
                }else {
                    CommLogD.info("playerid:"+k.getClubMemberBO().getPlayerID());
                }
            });
        }
        union.setCaseStatusChange(false);
    }


    @Override
    public int threadId() {
        return DispatcherComponentEnum.CASE_CLOSE.id();
    }

    @Override
    public int bufferSize() {
        return DispatcherComponentEnum.CASE_CLOSE.bufferSize();
    }


}
