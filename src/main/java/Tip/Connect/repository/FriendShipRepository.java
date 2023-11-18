package Tip.Connect.repository;

import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.Relationship.FriendShipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FriendShipRepository extends JpaRepository<FriendShip, FriendShipId> {
    Optional<FriendShip> findByFriendShipId(FriendShipId id);
}
