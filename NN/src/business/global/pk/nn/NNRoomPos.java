package business.global.pk.nn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.nn.c2s.cclass.NNRoom_PosEnd;
import business.nn.c2s.cclass.NN_define.NN_GameStatus;

public class NNRoomPos extends AbsRoomPos {

    public ArrayList<Integer> privateCards = new ArrayList<>(); // 手牌
    private int m_nWin = 0; // 赢场数
    private int m_nLose = 0; // 输场数
    private int m_nFlat = 0; // 平场数
    private ArrayList<Boolean> m_TuiZhuList;//上一句是否推注

    public NNRoomPos(int posID, AbsBaseRoom room) {
        super(posID, room);
        m_TuiZhuList = new ArrayList<Boolean>(Collections.nCopies(room.getCount(), false));
    }

    /**
     * 初始化手牌
     *
     * @param cards
     */
    public void init(List<Integer> cards) {
        this.privateCards = new ArrayList<>(cards);
    }

    /**
     * 作弊初始化
     * 暂时没用到。。。先留着
     *
     * @param publicCards
     * @param handCard
     */
    public void init(List<Integer> cards, List<List<Integer>> publicCards, int handCard) {
        this.privateCards = new ArrayList<>(cards);
    }


    /**
     * 获取牌组信息
     *
     * @return
     */
    public ArrayList<Integer> getNotifyCard(long pid) {
        boolean isSelf = pid == this.getPid();

        NNRoomSet roomSet = (NNRoomSet) this.getRoom().getCurSet();
        if (null != roomSet && roomSet.getStatus() == NN_GameStatus.NN_GAME_STATUS_RESULT) {
            isSelf = true;
        }

        ArrayList<Integer> sArrayList = new ArrayList<Integer>();
        // 是自己
        int length = privateCards.size();
        for (int i = 0; i < length; i++) {
            sArrayList.add(isSelf ? privateCards.get(i) : Integer.valueOf((Integer) 0x00));
        }
        return sArrayList;
    }


    public NNRoom_PosEnd calcPosEnd() {
        NNRoomSet set = (NNRoomSet) this.getRoom().getCurSet();
//        this.setPoint(this.getPoint() + set.pointList.get(this.getPosID()));
        this.calcRoomPoint(set.pointList.get(this.getPosID()));

        NNRoom_PosEnd posEnd = new NNRoom_PosEnd();
        int setPoint = set.pointList.get(this.getPosID());
        posEnd.addBet = set.betList.get(this.getPosID());
        posEnd.cardList = this.privateCards;
        posEnd.point = set.pointList.get(this.getPosID());
        posEnd.pos = this.getPosID();
        posEnd.pid = this.getPid();
        posEnd.isPlaying = set.playingList.get(this.getPosID());
        posEnd.isCallBacker = set.getBackerPos() == this.getPosID() ? true : false;
        if (posEnd.isCallBacker) posEnd.callBackerNum = set.getCallbackerNum(this.getPosID());
        posEnd.crawType = set.crawTypeList.get(this.getPosID());
        posEnd.sportsPoint = setSportsPoint(setPoint);
        return posEnd;
    }

    /**
     * @return privateCards
     */
    public ArrayList<Integer> getPrivateCards() {
        return privateCards;
    }

    /**
     * @return m_nWin
     */
    public int getWin() {
        return m_nWin;
    }

    /**
     *
     */
    public void addWin(int nWin) {
        this.m_nWin += nWin;
    }

    /**
     * @return m_nLose
     */
    public int getLose() {
        return m_nLose;
    }

    /**
     *
     */
    public void addLose(int nLose) {
        this.m_nLose += nLose;
    }

    /**
     * @return m_nFlat
     */
    public int getFlat() {
        return m_nFlat;
    }

    /**
     *
     */
    public void addFlat(int nFlat) {
        this.m_nFlat += nFlat;
    }

    /**
     * @return m_bTuiZhu
     */
    public boolean isTuiZhu(int curID) {
        boolean flag = false;
        if (curID < this.m_TuiZhuList.size() && curID >= 0) {
            flag = this.m_TuiZhuList.get(curID);
        }
        return flag;
    }

    /**
     *
     */
    public void setTuiZhu(int pos, boolean bTuiZhu) {
        this.m_TuiZhuList.set(pos, bTuiZhu);
    }


}
