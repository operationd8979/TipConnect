package Tip.Connect.repository;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.RequestAddFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RequestAddFriendRepository  extends JpaRepository<RequestAddFriend, Long> {
    Optional<RequestAddFriend> findByRequestID(Long id);
    Optional<RequestAddFriend> findByReceiver(AppUser receiver);
}


