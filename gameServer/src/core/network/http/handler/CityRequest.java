package core.network.http.handler;

import business.global.config.GameListConfigMgr;
import com.ddm.server.common.utils.StringUtil;
import com.ddm.server.http.annotation.RequestMapping;
import com.ddm.server.http.server.HttpRequest;
import com.ddm.server.http.server.HttpResponse;
import com.ddm.server.http.server.HttpUtils;
import com.ddm.server.websocket.def.ErrorCode;
import com.google.gson.JsonObject;
import core.network.http.proto.ZleData_Result;
import jsproto.c2s.cclass.config.CityInfo;

import java.util.List;
import java.util.Optional;

public class CityRequest {

    private Object Lock = new Object();

    @RequestMapping(uri = "/city/getCity")
    public void getCity(HttpRequest request, HttpResponse response) throws Exception {
        String jsonPost = StringUtil.GetStringBody(request.getRequestBody());
        JsonObject dataJson = HttpUtils.abstractGMParams(jsonPost, HttpUtils.ZLE_KEY_RECHARGE);
        //取得参数，参数如果恶意，抛出异常
        int cityId = HttpUtils.getInt(dataJson, "cityId");
        //城市管理器查询该城市
        Optional<CityInfo> cityInfo =  GameListConfigMgr.getInstance().findCityInfo(cityId);
        //城市管理器查询结果如果存在就返回信息
        if(cityInfo.isPresent()){
            response.response(ZleData_Result.make(ErrorCode.Success,cityInfo.get()));
            return;
        }
        //城市管理器查询结果如果不存在就返回信息
        response.response(ZleData_Result.make(ErrorCode.CITY_NOT_FIND,"City information not found"));
    }

    @RequestMapping(uri = "/city/listCity")
    public void listCity(HttpRequest request, HttpResponse response) throws Exception {
        String jsonPost = StringUtil.GetStringBody(request.getRequestBody());
        JsonObject dataJson = HttpUtils.abstractGMParams(jsonPost, HttpUtils.ZLE_KEY_RECHARGE);
        synchronized (Lock) {
            //城市管理器查询城市列表
            Optional<List<CityInfo>> cityInfoList =  GameListConfigMgr.getInstance().getCityInfoList();
            //城市管理器查询结果如果存在就返回信息
            if(cityInfoList.isPresent()){
                response.response(ZleData_Result.make(ErrorCode.Success,cityInfoList.get()));
                return;
            }
            //城市管理器查询结果如果不存在就返回信息
            response.response(ZleData_Result.make(ErrorCode.CITY_LIST_EMPTY,"The city list is empty"));
        }
    }
}
