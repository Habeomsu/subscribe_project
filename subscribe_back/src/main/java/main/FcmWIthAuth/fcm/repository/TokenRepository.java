package main.FcmWIthAuth.fcm.repository;

import main.FcmWIthAuth.fcm.entity.Token;
import main.FcmWIthAuth.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValueAndUser(String fcmToken, User user);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.tokenValue IN :failedTokens")
    void deleteByTokenValueIn(@Param("failedTokens") List<String> failedTokens);

    List<Token> findByExpirationDate(LocalDate now);


}
