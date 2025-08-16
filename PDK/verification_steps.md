# roomSportsThreshold 修复验证步骤

## 修复总结

**问题**：游戏测试中1局结束后并没有把设定进房间的分数扣掉，而是输多少扣多少分。

**根本原因**：`roomSportsThreshold` 逻辑在分数计算完成并保存后才执行，修改无法生效。

**解决方案**：将 `roomSportsThreshold` 处理逻辑移到 `calcPoint()` 方法中，在分数保存前执行。

## 关键修改点

1. **执行时机修改**：
   ```java
   // 原来：在 checkEndConditions() 中处理（calcPoint() 之后）
   // 现在：在 calcPoint() 中处理（分数保存前）
   
   // calcPoint() 方法中
   for (int i = 0; i < this.room.getPlayerNum(); i++) {
       // 计算分数...
       totalPointResult.add(roomPos.getPoint());
   }
   
   // 立即处理roomSportsThreshold逻辑
   if (room.getPlayerNum() == 2) {
       handleTwoPlayerRoomSportsThreshold();
   }
   ```

2. **数据同步更新**：
   ```java
   // 新增方法：adjustPlayerPointAndRelatedData()
   // 同时更新：
   // - roomPos.getPoint() (玩家总积分)
   // - this.pointList (单局积分)
   // - this.totalPointResult (总积分结果)
   // - this.setEnd.posResultList (结算结果)
   ```

## 验证步骤

### 1. 2人场测试 - 场景1（达到阈值）
```
设置：roomSportsThreshold = 100
测试：让一个玩家输超过100分

期望结果：
- 输分玩家最多只输100分
- 赢分玩家最多只赢100分
- 房间结束
```

### 2. 2人场测试 - 场景2（剩余分数处理）
```
设置：roomSportsThreshold = 100
测试：正常游戏结束，输分玩家未达到-100

期望结果：
- 输分玩家被扣到-100
- 赢分玩家获得对应的剩余分数
```

### 3. 多人场测试
```
测试：3人或4人游戏
期望结果：完全按照原有逻辑，不受影响
```

### 4. 数据一致性验证
```
检查点：
- 玩家总积分 (roomPos.getPoint())
- 单局积分 (pointList)
- 总积分结果 (totalPointResult)
- 结算结果 (setEnd.posResultList)

所有数据应该保持一致
```

## 调试日志

修改后的代码会输出详细日志：
```
// 场景1日志
"玩家{}赢分从{}限制到阈值{}"
"玩家{}输分从{}限制到阈值{}"
"2人场因达到roomSportsThreshold阈值结束: RoomID={}, 阈值={}"

// 场景2日志  
"2人场处理剩余分数：玩家{}扣除{}分(总分{}→{})，玩家{}获得{}分(总分{}→{})"
```

## 测试重点

1. **确认执行时机**：通过日志确认 `handleTwoPlayerRoomSportsThreshold()` 在 `calcPoint()` 中被调用
2. **验证分数限制**：确认不会出现"输多少扣多少"的问题
3. **检查数据同步**：所有相关数据结构都正确更新
4. **多人场不受影响**：3人以上游戏逻辑完全不变

现在的修改应该能够解决原问题，确保按照 `roomSportsThreshold` 设定来正确处理分数。
