# roomSportsThreshold 修复验证文档

## 修改内容

### 问题描述
之前的实现中，1局游戏的特殊扣分逻辑影响了多局游戏，导致多局游戏中每局都按 `roomSportsThreshold` 固定扣分，而不是按实际分数扣分。

### 修改方案
在 `PDKRoomSet.java` 中的 `handleTwoPlayerRoomSportsThreshold()` 方法中添加了对1局游戏和多局游戏的区分处理：

1. **判断标准**: 使用 `room.getCount() == 1` 来判断是否为1局游戏
2. **1局游戏逻辑**: 调用 `handleOneRoundGameThreshold()` - 固定扣除 `roomSportsThreshold` 值
3. **多局游戏逻辑**: 调用 `handleMultiRoundGameThreshold()` - 按实际分数扣除，但有阈值保护

### 具体修改

#### 1. 主方法修改
```java
private void handleTwoPlayerRoomSportsThreshold() {
    // 判断是否为1局游戏
    boolean isOneRoundGame = (room.getCount() == 1);
    
    if (isOneRoundGame) {
        // 1局游戏：固定扣除roomSportsThreshold值
        handleOneRoundGameThreshold(roomSportsThreshold);
    } else {
        // 多局游戏：按实际分数扣除，但有阈值保护
        handleMultiRoundGameThreshold(roomSportsThreshold);
    }
}
```

#### 2. 新增1局游戏处理方法
```java
private void handleOneRoundGameThreshold(Double roomSportsThreshold) {
    // 1局游戏：输家固定扣除roomSportsThreshold值，赢家固定获得roomSportsThreshold值
    // 无论实际输赢多少，都按固定值结算
}
```

#### 3. 新增多局游戏处理方法
```java
private void handleMultiRoundGameThreshold(Double roomSportsThreshold) {
    // 多局游戏：只有达到阈值时才限制并结束房间
    // 每局按实际分数扣除，不进行固定值结算
}
```

#### 4. 删除不需要的方法
- 删除了 `handleTwoPlayerRemainingPoints()` 方法，因为多局游戏不需要这个逻辑

## 预期效果

### 1局游戏 (room.getCount() == 1)
- ✅ 无论实际输赢多少分，输家固定扣除 `roomSportsThreshold` 值
- ✅ 赢家固定获得 `roomSportsThreshold` 值
- ✅ 这是"1局定输赢"的机制

### 多局游戏 (room.getCount() > 1)
- ✅ 每局按实际输赢分数扣除/加分
- ✅ 当玩家累计输分超过 `roomSportsThreshold` 时，游戏结束
- ✅ 每个玩家最多只能输掉 `roomSportsThreshold` 值（保护机制）
- ✅ 不会每局都固定扣除 `roomSportsThreshold` 值

## 测试场景

### 场景1：1局游戏
- 房间设置：1局，roomSportsThreshold = 100
- 实际游戏结果：玩家A输30分，玩家B赢30分
- 预期结果：玩家A扣除100分，玩家B获得100分

### 场景2：多局游戏 - 正常情况
- 房间设置：3局，roomSportsThreshold = 100
- 第1局结果：玩家A输20分，玩家B赢20分
- 预期结果：玩家A扣除20分，玩家B获得20分（按实际分数）

### 场景3：多局游戏 - 达到阈值
- 房间设置：3局，roomSportsThreshold = 100
- 累计结果：玩家A累计输100分
- 预期结果：游戏结束，玩家A总扣除限制在100分

## 关键改进点

1. **严格分离逻辑**: 通过 `room.getCount() == 1` 严格区分1局和多局游戏
2. **1局游戏固定扣分**: 只在1局游戏时执行固定扣分逻辑
3. **多局游戏实际扣分**: 多局游戏按实际分数扣分，只在达到阈值时进行保护
4. **代码简化**: 删除了不必要的剩余分数处理逻辑

## 验证要点

1. 确认 `room.getCount()` 返回正确的局数设置
2. 1局游戏时，无论实际分数如何，都按固定值结算
3. 多局游戏时，每局按实际分数结算，不受1局游戏逻辑影响
4. 多局游戏达到阈值时正确结束房间
