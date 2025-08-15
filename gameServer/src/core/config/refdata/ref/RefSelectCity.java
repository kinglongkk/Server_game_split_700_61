package core.config.refdata.ref;

import com.ddm.server.common.data.RefContainer;
import com.ddm.server.common.data.RefField;

import core.config.refdata.RefDataMgr;
import lombok.Data;

import java.util.Objects;

@Data
public class RefSelectCity extends RefBaseGame {
	@RefField(iskey = true)
	public int Id;
	public int Type;
	public int Ascription;
	public String Name;
	public int Popular;
	public int DefaultCity;
	public String Game;


	@Override
	public long getId() {
		return Id;
	}
	@Override
	public boolean Assert() {
		return true;
	}

	@Override
	public boolean AssertAll(RefContainer<?> all) {
		return true;
	}

	/**
	 * 检查城市Id是否正确
	 * @param cityId
	 * @return
	 */
	public static boolean checkCityId(int cityId) {
		if (cityId <= 0) {
			return false;
		}
		RefSelectCity refSelectCity = RefDataMgr.get(RefSelectCity.class,cityId);
		// 3:代表县市
		return Objects.nonNull(refSelectCity) && refSelectCity.getType() == 3;
	}


}
