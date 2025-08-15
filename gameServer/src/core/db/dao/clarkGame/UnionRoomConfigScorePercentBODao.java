package core.db.dao.clarkGame;

import com.ddm.server.annotation.Dao;
import core.db.persistence.Repository;
import core.db.entity.clarkGame.UnionRoomConfigScorePercentBO;

@Dao(dataSource = "clark_game")
public interface UnionRoomConfigScorePercentBODao extends Repository<UnionRoomConfigScorePercentBO> {



}
