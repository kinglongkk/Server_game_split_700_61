package business.pdk.c2s.iclass;

public class SPDK_UserInfo {
    private String name = "";
    private long pid = 0;
    private long totalPoint = 0;

    public SPDK_UserInfo(String name, long pid, long totalPoint) {
        this.name = name;
        this.pid = pid;
        this.totalPoint = totalPoint;
    }
}