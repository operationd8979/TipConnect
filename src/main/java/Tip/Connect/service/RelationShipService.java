package Tip.Connect.service;

import Tip.Connect.model.Relationship.RelationShip;
import Tip.Connect.repository.RelationShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationShipService {

    private final RelationShipRepository relationShipRepository;

    public RelationShip loadRelationShipById(String relationShipID){
        return relationShipRepository.findById(relationShipID).orElse(null);
    }

}
