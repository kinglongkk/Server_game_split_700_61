package jsproto.c2s.cclass.family;

import lombok.Data;

@Data
public class FamilyCityCurrencyItem {
    /**
     * 城市id
     */
    private int cityId;
    /**
     * 值
     */
    private int value;
    public FamilyCityCurrencyItem() {
    }

    public FamilyCityCurrencyItem(int cityId, int value) {
        this.cityId = cityId;
        this.value = value;
    }

    public static String getItemsName() {
        return "cityId,value";
    }


    @Override
    public String toString() {
        return "FamilyCityCurrencyItem{" +
                "cityId=" + cityId +
                ", value=" + value +
                '}';
    }
}
