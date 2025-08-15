package business.global.pk.nn;


import java.util.ArrayList;

public class NNResultSetInfo {
    private int cardType = 0;
    private ArrayList<Integer> privateCards = new ArrayList<>();
    private int bankerPoint=0;

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public void setPrivateCards(ArrayList<Integer> privateCards) {
        this.privateCards = privateCards;
    }

    public ArrayList<Integer> getPrivateCards() {
        return privateCards;
    }

    public int getCardType() {
        return cardType;
    }

    public void setBankerPoint(int bankerPoint) {
        this.bankerPoint = bankerPoint;
    }

    public int getBankerPoint() {
        return bankerPoint;
    }
}
