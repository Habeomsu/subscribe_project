package main.FcmWIthAuth.user.repository;

import jakarta.transaction.Transactional;
import main.FcmWIthAuth.user.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Integer> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    @Transactional
    void deleteByUsername(String username);

}
