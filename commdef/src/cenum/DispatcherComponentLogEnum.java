package cenum;

/**
 * 调度程序组件枚举
 */


public enum DispatcherComponentLogEnum {
    /**
     * 批量日志插入
     */
    BATCH_BD_LOG(0,16384),
    /**
     * 联赛、亲友圈事件管理器
     */
    CLUB_LEVEL_ROOM(1,32768),
    /**
     * 其他日志插入
     */
    OTHER_BD_LOG(2,16384);
    ;

    private int value;

    private int bufferSize;

    DispatcherComponentLogEnum(int value, int bufferSize) {
        this.value = value;
        this.bufferSize = bufferSize;
    }

    public int id() {
        return this.value;
    }

    public int bufferSize() { return bufferSize; }
}
