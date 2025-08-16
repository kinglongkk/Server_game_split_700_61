# roomSportsThreshold 双重限制问题修复 - 完整解决方案

## 问题描述
当设置了 `roomSportsThreshold = 100` 后，玩家可以连续多局都输100分，而不是只能输一次100分就结束房间。

**最新发现问题**：测试后发现每一局都扣设置分数，并不是正常游戏扣分的样子，是场景2的方法用到了场景1这边来了。

## 根本原因分析
存在**三个关键问题**：

### 1. 双重阈值限制问题
- **第一重限制**：`AbsRoomPos.saveSportsPoint()` 方法在每局结算时会限制竞技点变化到阈值范围内
- **第二重限制**：`PDKRoomSet.handleTwoPlayerRoomSportsThreshold()` 方法在房间结束时又会处理阈值逻辑
- **结果**：当第二次处理时，竞技点已经被第一次限制了，导致只扣除了基础消耗

### 2. 房间结束判断缺失
- `PDKRoomSet.isRoomEnd()` 方法没有检查 `room.isEnd` 标志
- 即使达到阈值设置了 `room.isEnd = true`，房间仍然可以继续下一局
- `startGameOtherCondition()` 方法没有检查玩家是否已达到阈值

### 3. 2人场阈值限制逻辑混乱 ⭐ **新修复**
- `AbsRoomPos.saveSportsPoint()` 中对2人场的处理逻辑不清晰
- 导致每局都被限制到设置分数，而不是让分数正常累积
- 场景2的处理方法错误应用到了场景1

## 完整修复方案

### 1. 修改 AbsRoomPos.java ⭐ **重要修复**
- ✅ 添加 `saveSportsPoint(sportsPointGame, sportsPointRoom, skipThresholdLimit)` 重载方法
- ✅ 添加 `updatePlayerRoomAloneBO(..., skipThresholdLimit)` 重载方法
- ✅ **关键修复**：2人场不在此处进行阈值限制，让分数正常累积
- ✅ 只有非2人场才在每局进行阈值限制

### 2. 修改 PDKRoomSet.java
- ✅ 在 `adjustPlayerPointAndRelatedData()` 方法中调用跳过阈值限制的版本
- ✅ 修改 `isRoomEnd()` 方法，检查 `room.isEnd` 标志
- ✅ 确保阈值逻辑只在 PDKRoomSet 中统一处理，避免双重限制

### 3. 修改 PDKRoom.java
- ✅ 重写 `startGameOtherCondition()` 方法
- ✅ 在游戏开始前检查玩家是否已达到 `roomSportsThreshold` 阈值
- ✅ 如果玩家已达到阈值，阻止开始新局

## 修复后的完整流程

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
玩家A: 计算后总积分 80 (赢分)
玩家B: 计算后总积分 -60 (输分，但未达到-100)

处理后：
玩家A: 120 (获得剩余40分)
玩家B: -100 (扣完剩余40分)
```

## 技术实现细节

### 关键修改点
1. **AbsRoomPos.saveSportsPoint()**: 
   - 添加 `skipThresholdLimit` 参数控制是否跳过阈值限制
   - **重要**：2人场不在此处进行阈值限制 `this.getRoom().getPlayerNum() != 2`
2. **PDKRoomSet.adjustPlayerPointAndRelatedData()**: 调用 `saveSportsPoint(sportsPoint, roomSportsConsume, true)` 跳过阈值限制
3. **PDKRoomSet.isRoomEnd()**: 检查 `room.isEnd` 标志，确保达到阈值时房间结束
4. **PDKRoom.startGameOtherCondition()**: 在游戏开始前检查玩家是否已达到阈值

### 防止重复输分的机制
1. **局结束时**: `handleTwoPlayerRoomSportsThreshold()` 检查并限制积分，设置 `room.isEnd = true`
2. **房间结束判断**: `isRoomEnd()` 检查 `room.isEnd` 标志
3. **下局开始前**: `startGameOtherCondition()` 检查玩家积分是否已达到阈值
4. **三重保护**: 确保玩家只能输一次设定的 `roomSportsThreshold` 分数

### 验证要点
- ✅ 避免双重阈值限制
- ✅ 确保完整分数变化生效
- ✅ 正确处理基础门票分和大赢家分
- ✅ 房间结束逻辑正确触发
- ✅ 防止玩家连续多局输设定分数
- ✅ 达到阈值后无法开始新局
- ✅ **新增**：每局分数正常扣除，不再每局都扣设置分数

## 相关文件
- `/gameServer/src/business/global/room/base/AbsRoomPos.java` ⭐ **重要修复**
- `/PDK/src/business/global/pk/pdk/PDKRoomSet.java`
- `/PDK/src/business/global/pk/pdk/PDKRoom.java`
- `/test_threshold_scenario_fix.md` (新增测试验证文件)

---

# 其他配置信息

roomSportsEveryoneConsume 每人付

winScore
sportsPoint
sportsPointCost

# kinglongkk-Server_game_pdk
客户端单局
lb_point-totalPointList
客户端总结算
lb_sportsPoint-sportsPoint

roomSportsThreshold
roomSportsEveryoneConsume

推广员
UIPromoterAllManager.js
活跃系数
UIUserSelfBaoMingFei.js
修改玩家活跃系数
UIUserSetBaoMingFei.js
固定值 PercentEditBox2-shareFixedValue
百分比 PercentEditBox-shareValue
区间

btn_detail_value    toggle2
btn_detail_percent  toggle1
btn_detail_section  toggle3

分数扣分不会那么刚好到达roomSportsThreshold这个值，最后输的人肯定会超扣，赢的人肯定会多加。
可是没间房间的输赢总分控制在roomSportsThreshold创建房间设置的这个值了。如果有人输完房间就结束，同时如果房间正常结束了还没有输完，2人场（单挑）情况下输多的人全扣光。

## 比赛分处理逻辑详解

### 第一种情况：达到阈值时房间结束
当玩家比赛分达到 `roomSportsThreshold` 阈值时，房间立即结束：

1. **输分达到阈值**：玩家积分 <= -roomSportsThreshold 时，房间结束
2. **赢分达到阈值**：玩家积分 >= roomSportsThreshold 时，房间结束
3. **积分截断**：确保玩家积分不会超过阈值范围

### 第二种情况：2人场正常结束时的处理
当房间正常结束（达到设定局数）且是2人场时：

1. **比较比赛分**：比较两个玩家的比赛分
2. **找到输分多的玩家**：比赛分少的玩家
3. **直接扣光**：如果该玩家还没有输光（积分 > -roomSportsThreshold），则直接扣光到 -roomSportsThreshold 值

### 实现方法
- `handleTwoPlayerRoomSportsThreshold()`: 处理2人场阈值逻辑
- `isRoomEnd()`: 检查房间是否结束（包括阈值检查）
- `startGameOtherCondition()`: 游戏开始前的条件检查

### 示例场景
假设 roomSportsThreshold = 100：

**场景1**：玩家A输到-100分，房间立即结束，无法开始下一局
**场景2**：玩家B赢到100分，房间立即结束，无法开始下一局
**场景3**：房间正常结束，玩家A积分-30，玩家B积分70，则玩家A直接扣光到-100分
