package jsproto.c2s.cclass;

import lombok.Data;

/**
 * 只查询Id
 */
@Data
public class CityGiveItem {
    private long id;

    private int cityId;

    private int state;

    public static String getItemsNameUid() {
        return "id,cityId,state";
    }


}
