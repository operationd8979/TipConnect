package Tip.Connect.repository;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FriendRequestRepository  extends JpaRepository<FriendRequest, String> {
    Optional<FriendRequest> findByRequestID(String id);

}


