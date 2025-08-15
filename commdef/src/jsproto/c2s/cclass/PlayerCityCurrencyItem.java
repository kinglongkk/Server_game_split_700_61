package jsproto.c2s.cclass;

import lombok.Data;

@Data
public class PlayerCityCurrencyItem {
    /**
     * 城市id
     */
    private int cityId;
    /**
     * 值
     */
    private int value;
    /**
     * 标记 1:代理城市
     */
    private int sign;
    public PlayerCityCurrencyItem() {
    }

    public PlayerCityCurrencyItem(int cityId, int value,boolean sign) {
        this.cityId = cityId;
        this.value = value;
        this.sign = sign ? 1:0;
    }

    public static String getItemsName() {
        return "cityId,value";
    }


    @Override
    public String toString() {
        return "PlayerCityCurrencyItem{" +
                "cityId=" + cityId +
                ", value=" + value +
                ", sign=" + sign +
                '}';
    }
}
