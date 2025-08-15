package business.global.pk;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 麻将打牌操作
 */
@Data
@NoArgsConstructor
public class PKOpCard {
    /**
     * 操作值
     */
    private int opValue;
    /**
     * 操作牌列表
     */
    private List<Integer> cardList=new ArrayList<>();
    /**
     * 是否机器人或者托管出牌
     */
    private boolean isFlash = false;


    /**
     * 癞子代替列表
     */
    private List<Integer> substituteCard=new ArrayList<>();


    public PKOpCard(int opValue) {
        this.opValue = opValue;
    }

    public PKOpCard(List<Integer> cardList) {
        this.cardList = cardList;
    }

    public PKOpCard(int opValue, List<Integer> cardList, List<Integer> substituteCard) {
        this.opValue = opValue;
        this.cardList = cardList;
        this.substituteCard = substituteCard;
    }

    public PKOpCard(int opValue, List<Integer> cardList) {
        this.opValue = opValue;
        this.cardList = cardList;
    }
    public PKOpCard(PKOpCard opCard) {
        this.setSubstituteCard(new ArrayList<>(opCard.getSubstituteCard()));
        this.setOpValue(new Integer(opCard.getOpValue()));
        this.setCardList(new ArrayList<>(opCard.getCardList()));
    }
    public PKOpCard(int opValue, List<Integer> cardList,boolean isFlash) {
        this.opValue = opValue;
        this.cardList = cardList;
        this.isFlash = isFlash;
    }

    public static PKOpCard OpCard(int opValue){
        return new PKOpCard(opValue);
    }

    public static PKOpCard OpCard(List<Integer> cardList){
        return new PKOpCard(cardList);
    }

    public static PKOpCard OpCard(int opValue,List<Integer> cardList){
        return new PKOpCard(opValue,cardList);
    }
    public static PKOpCard OpCard(int opValue,List<Integer> cardList,List<Integer> substituteCard){
        return new PKOpCard(opValue,cardList,substituteCard);
    }
    public static PKOpCard OpCard(int opValue,List<Integer> cardList,boolean isFlash){
        return new PKOpCard(opValue,cardList,isFlash);
    }

    @Override
    public String toString() {
        return "PKOpCard{" +
                "opValue=" + opValue +
                ", cardList=" + cardList +
                '}';
    }
}
