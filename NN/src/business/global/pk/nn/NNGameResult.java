package business.global.pk.nn;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import business.nn.c2s.cclass.NN_define;
import cenum.PrizeType;


public class NNGameResult {
    public NNRoom room; //房间

    public final int timesList[][] = {{1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4}, {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3}}; //倍数
    public final int specialtimesList[] = {1, 1, 5, 6, 8}; //倍数
    public final int specialIndexList[] = {-1, -1, 0, 1, 2}; //倍数
    public final int endPointList[] = {1, 2, 4};//底分
    public final int goleTimesList[] = {1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4};//金币翻倍规则
    public final int specialGoldTimesList[] = {1, 1, 5, 6, 8}; //倍数

    public NNGameResult(NNRoom room) {
        this.room = room;
    }

    //计算分数 和庄家比
    @SuppressWarnings("unchecked")
    public void calcByCallBacker() {
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();
        int callBacketCardType = 0;
        ArrayList<Integer> callBackerCardList = new ArrayList<Integer>();
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!(boolean) set.playingList.get(i)) continue;
            NNRoomPos setpos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
//			CommLogD.info("calcByCallBacker setpos.privateCards:%s\n", setpos.privateCards.toString());
            int cardType = NNGameLogic.GetCardType(setpos.privateCards);
//			CommLogD.info(" calcByCallBacker  cardType:%d, setpos.privateCards:%s\n", cardType, setpos.privateCards.toString());
            if (i == set.getBackerPos()) {
                callBacketCardType = cardType;
                callBackerCardList = (ArrayList<Integer>) setpos.privateCards.clone();
            }
            set.crawTypeList.set(i, cardType);
        }
        ArrayList<Integer> tempPointList = (ArrayList<Integer>) set.pointList.clone();

        boolean isGold = PrizeType.Gold == this.room.getBaseRoomConfigure().getPrizeType() ? true : false;

        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!(boolean) set.playingList.get(i)) continue;
            if (i == set.getBackerPos()) continue;

            NNRoomPos setpos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            boolean callbackerWin = false;
            int cardType = set.crawTypeList.get(i);
            if (NNGameLogic.CompareCard(callBackerCardList, setpos.privateCards)) {
                callbackerWin = true;
                cardType = callBacketCardType;
            }
            int point = 0;
            if (isGold) {
                point = calcGold(cardType, i);
            } else {
                point = calcPoint(cardType, i);
            }

            if (callbackerWin) {
                set.pointList.set(i, -point);
                set.pointList.set(set.getBackerPos(), set.pointList.get(set.getBackerPos()) + point);
            } else {
                set.pointList.set(i, point);
                set.pointList.set(set.getBackerPos(), set.pointList.get(set.getBackerPos()) - point);
            }
        }

        this.setWinOrLose(set.pointList, tempPointList);
    }

    //计算分数 和所有玩家比
    @SuppressWarnings("unchecked")
    public void calcByAll() {
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();

        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!(boolean) set.playingList.get(i)) continue;
            NNRoomPos setpos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            int cardType = NNGameLogic.GetCardType(setpos.privateCards);

            set.crawTypeList.set(i, cardType);
        }

        ArrayList<Integer> tempPointList = (ArrayList<Integer>) set.pointList.clone();

        boolean isGold = PrizeType.Gold == this.room.getBaseRoomConfigure().getPrizeType() ? true : false;

        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!(boolean) set.playingList.get(i)) continue;
            NNRoomPos setpos_i = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);

            for (int j = i + 1; j < this.room.getMaxPlayerNum(); j++) {
                if (!(boolean) set.playingList.get(j)) continue;
                if (i == j) continue;

                NNRoomPos setpos_j = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(j);
                boolean isWin = false;
                int winCardType = set.crawTypeList.get(j);
                if (NNGameLogic.CompareCard(setpos_i.privateCards, setpos_j.privateCards)) {
                    isWin = true;
                    winCardType = set.crawTypeList.get(i);
                }
                int point = 0;

                if (isGold) {
                    point = calcGold(winCardType, isWin ? i : j);
                } else {
                    point = calcPoint(winCardType, isWin ? i : j);
                }

                if (!isWin) {
                    set.pointList.set(i, set.pointList.get(i) - point);
                    set.pointList.set(j, set.pointList.get(j) + point);

                } else {
                    set.pointList.set(i, set.pointList.get(i) + point);
                    set.pointList.set(j, set.pointList.get(j) - point);

                }
            }
        }

        this.setWinOrLose(set.pointList, tempPointList);
    }

    //设置输赢
    public void setWinOrLose(ArrayList<Integer> nowPointList, ArrayList<Integer> oldPointList) {
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!set.playingList.get(i)) continue;
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            if (nowPointList.get(i) > oldPointList.get(i)) {
                roomPos.addWin(1);
            } else if (nowPointList.get(i) == oldPointList.get(i)) {
                roomPos.addFlat(1);
            } else if (nowPointList.get(i) < oldPointList.get(i)) {
                roomPos.addLose(1);
            }
        }
    }


    //计算得分
    public int calcGold(int cardType, int pos) {
        int total = 0;
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();
        int num = set.getCallbackerNum(set.getBackerPos());
        num = num > 0 ? num : 1;
        total = this.room.getBaseMark() * num * set.betList.get(pos) * this.getGoldTimes(cardType);
        return total;
    }

    //计算得分
    public int calcPoint(int cardType, int pos) {
        int total = 0;
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();
        if (this.room.getRoomCfg().getSign() == NN_define.NN_GameType.NN_TBNN.value()) {
            total = this.getTimes(cardType) * endPointList[this.room.getRoomCfg().difen];
        } else {
            total = this.getTimes(cardType) * set.betList.get(pos);
        }

        if (this.room.getRoomCfg().getSign() == NN_define.NN_GameType.NN_MPQZ.value()) {
            int num = set.getCallbackerNum(set.getBackerPos());
            num = num > 0 ? num : 1;
            total = total * num;
        }
        return total;
    }


    //获取倍数
    public int getTimes(int cardType) {
        int times = 1;
        if (cardType > 10) {
            int idx = cardType - NNGameLogic.OX_THREE_SAME;
            if (this.getSpecialCardTypeSelected(this.specialIndexList[idx])) {
                times = this.specialtimesList[idx];
            } else {
                int DoubleRules = this.room.getRoomCfg().fanbeiguize;
                times = timesList[DoubleRules][10];
            }
        } else {
            int DoubleRules = this.room.getRoomCfg().fanbeiguize;
            times = timesList[DoubleRules][cardType];
        }
        return times;
    }

    //获取金币倍数
    public int getGoldTimes(int cardType) {
        int times = 1;
        if (cardType > 10) {
            int idx = cardType - NNGameLogic.OX_THREE_SAME;
            times = this.specialGoldTimesList[idx];
        } else {
            times = this.goleTimesList[cardType];
        }
        return times;
    }

    //特殊牌型
    public boolean getSpecialCardTypeSelected(int idx) {
        for (Integer index : this.room.getRoomCfg().teshupaixing) {
            if (idx == index) return true;
        }
        return false;
    }

    /**
     * 结算进墓园
     */
    public void calcJinMuYuan(){
        //墓园，先结算比庄家小的人，再结算大于庄家的
        NNRoomSet set = (NNRoomSet) this.room.getCurSet();
        int bankerPosIndex = set.getBackerPos();
        if(bankerPosIndex<0)return;
        //结算信息
        NNResultSetInfo bankerSetInfo = new NNResultSetInfo();
        NNRoomPos bankerPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(bankerPosIndex);
        //设置结算信息
        bankerSetInfo.setCardType(NNGameLogic.GetCardType(bankerPos.privateCards));
        bankerSetInfo.setPrivateCards(bankerPos.privateCards);
        bankerSetInfo.setBankerPoint(bankerPos.getPoint());
        //设置牌型
        IntStream.range(0,this.room.getMaxPlayerNum()).filter(i->set.playingList.get(i)).forEach(i->{
            NNRoomPos setPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            set.crawTypeList.set(i, NNGameLogic.GetCardType(setPos.privateCards));
        });
        //庄家分数
        ArrayList<Integer> oldPointList = (ArrayList<Integer>) set.pointList.clone();
        Map<Boolean, List<NNRoomPos>> winnerLoserList = IntStream.range(0, this.room.getMaxPlayerNum()).filter(i -> set.playingList.get(i) && i != set.getBackerPos()).mapToObj(i -> (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i)).collect(Collectors.groupingBy(n -> NNGameLogic.CompareCard(bankerSetInfo.getPrivateCards(), n.getPrivateCards())));
        //计算输庄家的人的钱
        if(winnerLoserList.containsKey(true)){
            winnerLoserList.get(true).forEach(n->{
                int point = calcPoint(bankerSetInfo.getCardType(), n.getPosID());
                set.pointList.set(n.getPosID(), -point);
                set.pointList.set(set.getBackerPos(), set.pointList.get(bankerPosIndex) + point);
                bankerSetInfo.setBankerPoint(bankerSetInfo.getBankerPoint()+point);
            });
        }
        //计算赢庄家的人的钱
        if(winnerLoserList.containsKey(false)){
            winnerLoserList.get(false).sort((o2,o1)->{
                if(NNGameLogic.CompareCard(o2.getPrivateCards(), o1.getPrivateCards())){
                    return -1;
                }
                return 0;
            });
            winnerLoserList.get(false).forEach(n->{
                int point = calcPoint(set.crawTypeList.get(n.getPosID()), n.getPosID());
                int lastPoint = bankerSetInfo.getBankerPoint() - point;
                if(lastPoint<=0){
                    point = bankerSetInfo.getBankerPoint();
                }
                set.pointList.set(n.getPosID(), point);
                set.pointList.set(set.getBackerPos(), set.pointList.get(bankerPosIndex) - point);
                bankerSetInfo.setBankerPoint(bankerSetInfo.getBankerPoint()-point);
            });
        }
        this.setWinOrLose(set.pointList, oldPointList);
    }

}
