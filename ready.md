# roomSportsThreshold 双重限制问题修复

## 问题描述
当设置了 `roomSportsThreshold` 后，玩家输超或赢超阈值时，只扣除基础门票分和大赢家分，而不是按照正常的输赢分数进行扣除。

## 根本原因
存在**双重阈值限制**问题：

1. **第一重限制**：`AbsRoomPos.saveSportsPoint()` 方法在每局结算时会限制竞技点变化到阈值范围内
2. **第二重限制**：`PDKRoomSet.handleTwoPlayerRoomSportsThreshold()` 方法在房间结束时又会处理阈值逻辑
3. **结果**：当第二次处理时，竞技点已经被第一次限制了，导致只扣除了基础消耗

## 修复方案

### 1. 修改 AbsRoomPos.java
- 添加 `saveSportsPoint(sportsPointGame, sportsPointRoom, skipThresholdLimit)` 重载方法
- 添加 `updatePlayerRoomAloneBO(..., skipThresholdLimit)` 重载方法
- 支持在特定情况下跳过阈值限制

### 2. 修改 PDKRoomSet.java
- 在 `adjustPlayerPointAndRelatedData()` 方法中调用跳过阈值限制的版本
- 确保阈值逻辑只在 PDKRoomSet 中统一处理，避免双重限制

## 修复后的流程

### 场景1：房间没有结束但达到阈值
- roomSportsThreshold = 100
- 玩家A: 总积分 -120 (输超20)
- 玩家B: 总积分 220 (赢超20)

**修复后结果：**
- 玩家A: -100 (限制到阈值)
- 玩家B: +100 (限制到阈值)
- 房间立即结束

### 场景2：房间正常结束，剩余分数处理
- roomSportsThreshold = 100
- 玩家A: 总积分 120 (赢分最多)
- 玩家B: 总积分 -80 (输分最多，但未达到-100)

**修复后结果：**
- 玩家A: +100 (获得剩余20分)
- 玩家B: -100 (扣完剩余20分)

## 技术细节

### 关键修改点
1. **AbsRoomPos.saveSportsPoint()**: 添加 `skipThresholdLimit` 参数控制是否跳过阈值限制
2. **PDKRoomSet.adjustPlayerPointAndRelatedData()**: 调用 `saveSportsPoint(sportsPoint, roomSportsConsume, true)` 跳过阈值限制
3. **统一处理**: 确保阈值逻辑只在 `handleTwoPlayerRoomSportsThreshold()` 中处理

### 验证要点
- ✅ 避免双重阈值限制
- ✅ 确保完整分数变化生效
- ✅ 正确处理基础门票分和大赢家分
- ✅ 房间结束逻辑正确触发

## 相关文件
- `/gameServer/src/business/global/room/base/AbsRoomPos.java`
- `/PDK/src/business/global/pk/pdk/PDKRoomSet.java`
- `/PDK/test_threshold_fix.java` (测试验证文件)

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
- `checkYourSpecialCondition()`: 检查是否达到阈值触发房间结束
- `handleTwoPlayerRoomEnd()`: 处理2人场正常结束时的特殊情况
- `checkAndTruncatePointsToThreshold()`: 积分截断处理

### 示例场景
假设 roomSportsThreshold = 100：

**场景1**：玩家A输到-100分，房间立即结束
**场景2**：玩家B赢到100分，房间立即结束  
**场景3**：房间正常结束，玩家A积分-30，玩家B积分70，则玩家A直接扣光到-100分
