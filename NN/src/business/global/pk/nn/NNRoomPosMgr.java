package business.global.pk.nn;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import com.ddm.server.common.utils.CommTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NNRoomPosMgr extends AbsRoomPosMgr {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public NNRoomPosMgr(AbsBaseRoom room) {
        super(room);
    }

    public int getAllPlayerNum() {
        int count = 0;
        for (AbsRoomPos pos : this.posList) {
            if (pos.getPid() <= 0) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * 是否所有玩家准备
     *
     * @return
     */
    @Override
    public boolean isAllReady() {

        if(this.room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()==this.room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum()){
            if (null == this.getPosList() || this.getPosList().size() <= 1) {
                // 玩家信息列表没数据
                return false;
            }
            AbsRoomPos result = this.getPosList().stream().filter((x) -> !x.isReady()).findAny().orElse(null);
            if (null != result) {
                return false;
            }
            return true;
        }else {
            List<AbsRoomPos> result = this.getPosList().stream().filter((x) -> x.getPid()!=0).collect(Collectors.toList());
            if (null == this.getPosList() || this.getPosList().size() <= 1) {
                // 玩家信息列表没数据 人数少于两个无法开始
                return false;
            }
            if(null==result||result.size()<2){
                // 房间玩家 人数少于两个无法开始
                return false;
            }
            for(AbsRoomPos con:result){
                if(!con.isReady()){
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void initPosList() {
        // 初始化房间位置
        for (int posID = 0; posID < this.getPlayerNum(); posID++) {
            this.posList.add(new NNRoomPos(posID, room));
        }
    }

    //获取玩过这个游戏的人数
    public int getPlayTheGameNum() {
        int count = 0;
        for (AbsRoomPos roomPosDelegateAbstract : posList) {
            NNRoomPos roomPos = (NNRoomPos) roomPosDelegateAbstract;
            if (roomPos.isPlayTheGame())
                count++;
        }
        return count;
    }

    /**
     * 当所有人取消所有托管状态
     */
    public boolean notAllTrusteeship() {
        for (int i = 0; i < getPlayerNum(); i++) {
            AbsRoomPos pos = posList.get(i);
            if (null != pos) {
                if (pos.isTrusteeship()) {
                    return false;
                }
                if(pos.isRobot()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取所有玩家的牌
     *
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<Integer>> getAllPlayBackNotify() {
        return (ArrayList<ArrayList<Integer>>) this.getPosList().stream()
                .map(k -> (ArrayList<Integer>) ((NNRoomPos) k).getPrivateCards().clone()).collect(Collectors.toList());
    }

    /**
     * 是否所有玩家继续下一局
     *
     * @return
     */
    @Override
    public boolean isAllContinue() {
        if (null == this.getPosList() || this.getPosList().size() <= 1) {
            // 玩家信息列表没数据
            return false;
        }
        if(this.room.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(2)){
            //        //超时继续，萍乡
            this.getPosList().stream().forEach(k -> {
                if (k.getPid() > 0 && !k.isGameReady() && k.getTimeSec() > 0 && CommTime.nowSecond()- k.getTimeSec() >= 10) {
                    getRoom().continueGame(k.getPid());
                }
            });
        }
        // 玩家在游戏中并且没有准备。
        return this.getPosList().stream().allMatch(k ->k.getPid()<=0L|| (k.getPid() > 0L && k.isGameReady()));
    }
}
