package Tip.Connect.repository;

import Tip.Connect.model.Chat.GifItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface GifItemRepository extends JpaRepository<GifItem,Integer> {

    @Override
    Optional<GifItem> findById(Integer integer);
}
