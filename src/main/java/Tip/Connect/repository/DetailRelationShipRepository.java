package Tip.Connect.repository;

import Tip.Connect.model.Relationship.DetailRelationShip;
import Tip.Connect.model.Relationship.DetailRelationShipID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetailRelationShipRepository extends JpaRepository<DetailRelationShip, DetailRelationShipID> {

    @Override
    Optional<DetailRelationShip> findById(DetailRelationShipID id);

}
