package Tip.Connect.repository;

import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.Relationship.RelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RelationShipRepository extends JpaRepository<RelationShip, String> {

    @Override
    Optional<RelationShip> findById(String id);
}
