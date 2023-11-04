package Tip.Connect.repository;

import Tip.Connect.model.AppUser;
import Tip.Connect.model.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    Optional<FriendShip> findByFriendShipId(Long id);
}
