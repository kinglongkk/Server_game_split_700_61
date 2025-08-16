# 2人场 roomSportsThreshold 修复验证

## 问题描述
设置 `roomSportsThreshold = 100` 后，发现每一局都扣设置分数，而不是正常游戏扣分的样子。这是因为场景2的方法用到了场景1。

## 根本原因
1. **双重阈值限制**：`AbsRoomPos.saveSportsPoint()` 和 `PDKRoomSet.handleTwoPlayerRoomSportsThreshold()` 都在处理阈值逻辑
2. **2人场特殊处理混乱**：每局都被限制到设置分数，而不是让分数正常累积

## 修复方案

### 核心修改
1. **AbsRoomPos.saveSportsPoint()** - 2人场不在此处进行阈值限制
2. **PDKRoomSet.handleTwoPlayerRoomSportsThreshold()** - 统一处理2人场阈值逻辑
3. **PDKRoom.startGameOtherCondition()** - 防止达到阈值后开始新局

### 修复逻辑
```java
// AbsRoomPos.saveSportsPoint() 中的关键修改
if (!skipThresholdLimit && roomSportsThreshold > 0D && this.getRoom().getPlayerNum() != 2) {
    // 只对非2人场进行阈值限制
    // 2人场由PDKRoomSet统一处理
}
```

## 测试场景

### 场景1：达到阈值时限制分数并结束房间
```
设置：roomSportsThreshold = 100
第1局：玩家A -10, 玩家B +10 (正常扣分)
第2局：玩家A -20, 玩家B +20 (正常扣分，累计A:-30, B:+30)
第3局：玩家A -80, 玩家B +80 (正常扣分，累计A:-110, B:+110)

处理后：
玩家A: -100 (限制到阈值，多输的10分不扣除)
玩家B: +100 (限制到阈值，多赢的10分不增加)
房间立即结束，无法开始下一局
```

### 场景2：房间正常结束，剩余分数处理
```
设置：roomSportsThreshold = 100
房间结束时：玩家A +80, 玩家B -60 (都未达到阈值)

处理后：
玩家A: +120 (获得剩余40分)
玩家B: -100 (扣完剩余40分)
```

## 验证要点

### ✅ 修复前问题
- 每局都扣设置分数 (100分)
- 不是正常游戏扣分
- 场景2逻辑错误应用到场景1

### ✅ 修复后预期
- 每局正常扣分 (实际游戏输赢分数)
- 只有达到阈值时才限制并结束房间
- 房间正常结束时才处理剩余分数
- 达到阈值后无法开始新局

## 关键代码修改

### 1. AbsRoomPos.java
```java
// 2人场不在这里进行阈值限制，让分数正常累积
if (!skipThresholdLimit && roomSportsThreshold > 0D && this.getRoom().getPlayerNum() != 2) {
    // 只对非2人场进行阈值限制
}
```

### 2. PDKRoomSet.java
```java
// 在calcPoint()中调用，统一处理2人场阈值逻辑
if (room.getPlayerNum() == 2) {
    handleTwoPlayerRoomSportsThreshold();
}
```

### 3. PDKRoom.java
```java
// 检查是否达到阈值，防止开始新局
if (playerTotalPoint <= -roomSportsThreshold || playerTotalPoint >= roomSportsThreshold) {
    return false; // 不能开始新局
}
```

## 测试步骤
1. 创建2人场房间，设置 roomSportsThreshold = 100
2. 进行多局游戏，验证每局分数正常扣除
3. 验证达到阈值时房间正确结束
4. 验证无法开始新局
5. 验证房间正常结束时的剩余分数处理

## 预期结果
- ✅ 每局分数正常扣除（不再每局都扣100分）
- ✅ 达到阈值时正确限制并结束房间
- ✅ 房间正常结束时正确处理剩余分数
- ✅ 达到阈值后无法开始新局
- ✅ 避免双重阈值限制问题

---

**修复完成时间**: $(date)
**修复状态**: ✅ 已修复2人场阈值限制逻辑混乱问题
