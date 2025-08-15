package jsproto.c2s.iclass.club;

import java.util.ArrayList;
import java.util.List;

import cenum.VisitSignEnum;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.club.ClubInvitedInfo;

/**
 * 获取俱乐部邀请链表
 * @author zaf
 *
 */
public class SClub_InvitedList extends BaseSendMsg {

	public List<ClubInvitedInfo> invitedList;

    public static SClub_InvitedList make(List<ClubInvitedInfo> invitedList) {
        SClub_InvitedList ret = new SClub_InvitedList();
        ret.invitedList = invitedList;
        ret.setSignEnum(VisitSignEnum.CLUN_ROOM_MAIN);
        return ret;
    }
}