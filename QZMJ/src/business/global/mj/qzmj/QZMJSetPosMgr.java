package business.global.mj.qzmj;

import business.global.mj.AbsMJSetPos;
import business.global.mj.AbsMJSetPosMgr;
import business.global.mj.AbsMJSetRoom;
import business.global.mj.manage.MJFactory;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJCfg;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJOpPoint;
import business.global.mj.qzmj.optype.QZMJBuHuaImpl;
import cenum.mj.*;
import jsproto.c2s.cclass.mj.BaseMJSet_Pos;
import jsproto.c2s.cclass.mj.NextOpType;
import jsproto.c2s.cclass.mj.OpTypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QZMJSetPosMgr extends AbsMJSetPosMgr {

    public QZMJSetPosMgr(AbsMJSetRoom set) {
        super(set);
    }

    /**
     * 检查动作类型。
     *
     * @param curOpPos  当前操作位置ID
     * @param curCardID 当前操作牌ID
     * @param opType    动作类型
     */
    @Override
    public void checkOpType(int curOpPos, int curCardID, OpType opType) {
        // 清空所有动作类型操作
        this.cleanAllOpType();
        switch (opType) {
            case Out:
                this.checkOutOpType(curOpPos, curCardID);
                break;
            case TianHu:
                this.checkAtFirstHu();
                break;
            case TianTing:
                this.checkTianTing(curOpPos);
                break;
            case Gang:
                checkOpTypeQGH(curOpPos, curCardID);
                break;
            default:
                break;
        }
    }

    /**
     * 下个动作类型。
     *
     * @param opType
     * @return
     */
    @Override
    public NextOpType exeCardAction(OpType opType) {
        NextOpType nOpType = null;
        switch (opType) {
            case Out:
            case TianHu:
            case Gang:
            case TianTing:
                nOpType = opAllMapOutCard();
                break;
            default:
                break;
        }
        return nOpType;
    }

    /**
     * 检测可以抢杠胡的操作者
     *
     * @param curOpPos  操作者位置
     * @param curCardID 操作牌
     * @return
     */
    @Override
    public void check_QiangGangHu(int curOpPos, int curCardID) {
        QZMJSetPos setPos = null;
        for (int i = 1; i < this.set.getRoom().getPlayerNum(); i++) {
            int nextPos = (curOpPos + i) % this.set.getRoom().getPlayerNum();
            setPos = (QZMJSetPos)this.set.getMJSetPos(nextPos);
            if(!setPos.checkKeXuanJin()) continue;
            setPos.clearMCardInit();
            if (setPos.checkOpType(curCardID, OpType.QiangGangHu)) {
                setPos.setHuOpType(OpType.QiangGangHu);
                this.addOpTypeInfo(nextPos, OpType.QiangGangHu);
            }
        }
    }

    /**
     * 检查是否存在一炮多响
     */
    @Override
    protected boolean checkExistYPDX() {
        return false;
    }

    /**
     * 检查是否存在吃牌
     */
    @Override
    protected boolean checkExistChi() {
        return true;
    }

    /**
     * 检查是否存在接杠补杠 T:接杠时可选择碰后再补杠。
     *
     * @return
     */
    @Override
    protected boolean checkExistJGBG() {
        return false;
    }

    /**
     * 检查是否存在平胡
     *
     * @return
     */
    @Override
    protected boolean checkExistPingHu() {
        return true;
    }

    /**
     * 开局补花
     */
    @Override
    public void startSetApplique() {
        // 标识是否有开局补花
        boolean isStartSetApplique = false;
        AbsMJSetPos mPos = null;
        for (int i = 0; i < this.set.getPlayerNum(); i++) {
            int index = (this.set.getDPos() + i) % this.set.getPlayerNum();
            mPos = this.set.getMJSetPos(index);
            if (null == mPos) {
                continue;
            }
            if (MJFactory.getOpCard(QZMJBuHuaImpl.class).checkOpCard(mPos, FlowerEnum.PRIVATE.ordinal())) {
                isStartSetApplique = true;
            }
        }
        if (isStartSetApplique) {
            // 存在开局补花。
            startSetApplique();
        } else {
            return;
        }
    }

    /**
     * 是否打出金
     *
     * @return
     */
    public boolean isAllPosOutJin() {
        return this.set.getPosDict().values().stream()
                .filter(k -> k.sizeOutCardIDs() > 0 && k.getOutJinInfo().isOutJinCard()).findAny().isPresent();
    }

    /**
     * 检查起手胡
     */
    private void checkAtFirstHu() {
        // 检查庄家胡
        checkDPosHu();
        // 检查闲家三金倒
        checkSanJinDao();
////        // 检查枪金:闲家抢金->庄家抢金
//        checkTianTing(this.set.getDPos());

    }

    /**
     * 检查庄家胡牌
     */
    private void checkDPosHu() {
        AbsMJSetPos mPos = this.set.getMJSetPos(this.set.getDPos());
        if (mPos.checkOpType(0, OpType.SanJinDao)) {
            this.addOpTypeInfo(this.set.getDPos(), OpType.SanJinDao);
            mPos.getPosOpRecord().addOpHuList(QZMJOpPoint.SanJinDao);
        } else if (mPos.checkOpType(0, OpType.TianHu)) {
            this.addOpTypeInfo(this.set.getDPos(), OpType.TianHu);
            mPos.getPosOpRecord().addOpHuList(QZMJOpPoint.TianHu);
        }
        mPos = null;
    }

    /**
     * 检查闲家三金倒
     */
    private void checkSanJinDao() {
        AbsMJSetPos mPos = null;
        for (int i = 1; i < this.set.getRoom().getPlayerNum(); i++) {
            int nextPos = (i + this.set.getDPos()) % this.set.getPlayerNum();
            mPos = this.set.getMJSetPos(nextPos);
            if (null == mPos) {
                continue;
            }
             if (mPos.checkOpType(0, OpType.SanJinDao)) {
                this.addOpTypeInfo(nextPos, OpType.SanJinDao);
                mPos.getPosOpRecord().addOpHuList(QZMJOpPoint.SanJinDao);
            }
        }
    }



    /**
     * 检查出牌后是否有人可以接手。
     *
     * @param curOpPos  当前操作位置ID
     * @param curCardID 当前操作牌ID
     */
    @Override
    protected void checkOutOpType(int curOpPos, int curCardID) {

        if (this.isAllPosOutJin()) {
            // 有玩家打出金牌
            return;
        }
        check_otherPingHu(curOpPos, curCardID);
        check_otherJieGang(curOpPos, curCardID);
        check_otherPeng(curOpPos, curCardID);
        check_LowerChi(curOpPos, curCardID);
    }

    /**
     * 检查天听 庄家打完牌的时候判断
     * @param curOpPos
     * @return
     */
    private void checkTianTing(int curOpPos) {
        AbsMJSetPos setPos = null;
        for (int i = 0; i < this.set.getRoom().getPlayerNum(); i++) {
            int nextPos = (curOpPos + i) % this.set.getRoom().getPlayerNum();
            setPos = this.set.getMJSetPos(nextPos);
            if(setPos.checkOpType(0, OpType.TianTing)) {//庄家判断天听
                this.addOpTypeInfo(nextPos, OpType.TianTing);
            }

        }
    }

    /**
     * 检测其他人是否可以平胡
     *
     * @param curOpPos  当前操作位置ID
     * @param curCardID 当前操作牌ID
     */
    protected void check_otherPingHu(int curOpPos, int curCardID) {
        // 只能自摸
        if (!this.checkExistPingHu()) {
            return;
        }
        QZMJSetPos setPos = null;
        for (int i = 1; i < this.set.getRoom().getPlayerNum(); i++) {
            int nextPos = (curOpPos + i) % this.set.getRoom().getPlayerNum();
            setPos = (QZMJSetPos)this.set.getMJSetPos(nextPos);
            if(!setPos.checkKeXuanJin()) continue;
            OpType oType = setPos.checkPingHu(curOpPos, curCardID);
            if (!OpType.Not.equals(oType)) {
                this.addOpTypeInfo(nextPos, oType);
            }
        }
    }

    /**
     * 获取所有玩家信息
     */
    public List<BaseMJSet_Pos> getAllNotify(int posID) {
        QZMJSetPos sendSetPos = (QZMJSetPos) this.set.getMJSetPos(posID);

        List<BaseMJSet_Pos> list = new ArrayList<>();
        for (int i = 0; i < this.set.getRoom().getPlayerNum(); i++) {
            QZMJSetPos setPos = (QZMJSetPos) this.set.getMJSetPos(i);
            if (null != setPos) {
                list.add(setPos.getNotify(posID == setPos.getPosID() || sendSetPos.isTing()));
            }
        }
        return list;
    }

    /**
     * 检测其他人是否可以碰
     *
     * @param curOpPos  当前操作位置ID
     * @param curCardID 当前操作牌ID
     */
    protected void check_otherPeng(int curOpPos, int curCardID) {
        AbsMJSetPos setPos = null;
        for (int i = 1; i < this.set.getRoom().getPlayerNum(); i++) {
            int nextPos = (curOpPos + i) % this.set.getRoom().getPlayerNum();
            setPos = this.set.getMJSetPos(nextPos);
            // 检查碰动作
            if (setPos.checkOpType(curCardID, OpType.Peng)) {
                this.addOpTypeInfo(nextPos, OpType.Peng);
                return;
            }
        }
    }
    /**
     * 添加动作信息
     *
     * @param posId  位置
     * @param opType 动作类型
     */
    protected void addOpTypeInfo(Integer posId, OpType opType) {
        if (null == this.opTypeInfoList) {
            this.opTypeInfoList = Collections.synchronizedList(new ArrayList<>());;
        }
        int count = this.opTypeInfoList.size();
        // 检查是否存在一炮多响
        if (this.checkExistYPDX()) {
            // 存在一炮多响
            if (!HuType.NotHu.equals(MJCEnum.OpHuType(opType))) {
                count = 0;
                this.huPosList.add(posId);
            }
        }
        //如果是天听的话
        if(opType.equals(OpType.TianTing)){
            count = 0;
            this.huPosList.add(posId);
        }
        // 添加动作信息
        this.opTypeInfoList.add(new OpTypeInfo(count + 1, posId, opType));
    }
    /**
     * 执行动作类型信息
     *
     * @return
     */
    public OpTypeInfo exeOpTypeInfo(Integer opPos, OpType opType) {
        // 检查是否有动作列表
        if (null == this.opTypeInfoList || this.opTypeInfoList.size() <= 0) {
            return null;
        }
        this.opTypeInfoList.sort((OpTypeInfo itme1, OpTypeInfo itme2) -> {
            if (itme1.getId() - itme2.getId() == 0) {
                // 排名一样，通过一样的位置排序(当前操作位置最前面)
                if (itme1.getPosId() == opPos) {
                    return -1;
                }
            }
            // 排名排序(123...456)  如果优先级一样的话 则为操作的提到前面来
            if(itme1.getId() ==itme2.getId()){
               return itme1.getType()-itme2.getType();
            }
            return itme1.getId() - itme2.getId();
        });
        // 移除对应的胡位置列表
        this.huPosList.remove(opPos);
        boolean isPass = false;
        // 动作信息
        OpTypeInfo oInfo = null;
        for (int i = 0, size = this.opTypeInfoList.size(); i < size; i++) {
            oInfo = this.opTypeInfoList.get(i);
            if (null == oInfo) {
                continue;
            }
            // 是否当前操作玩家
            if (oInfo.getPosId() == opPos) {
                // 如果是操作指定动作
                if (opType.equals(oInfo.getOpType())) {
                    // 设置类型
                    oInfo.setType(2);
                    this.opTypeInfoList.set(i, oInfo);
                } else {
                    // 否则,过动作
                    // 设置类型
                    oInfo.setType(1);
                    this.opTypeInfoList.set(i, oInfo);
                }
            }

            if (!isPass) {
                if (oInfo.getType() == 0) {
                    // 第一个没操作
                    isPass = true;
                } else if (oInfo.getType() == 2) {
                    // 操作信息
                    return oInfo;
                }
            }
        }
        if (!isPass) {
            // 全部玩家操作完-并且都点了过。
            return new OpTypeInfo(opPos, OpType.Pass);
        } else {
            // 排名前的玩家存在没操作的。
            return new OpTypeInfo(-1, OpType.Not);
        }
    }
}
