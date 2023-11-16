package Tip.Connect.repository;

import Tip.Connect.model.Chat.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ChatRepository extends JpaRepository<Record,String> {

    Optional<Record> findByRecordID(String recordID);
}
