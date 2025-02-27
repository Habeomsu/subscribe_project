package main.FcmWIthAuth.fcm.entity;


import jakarta.persistence.*;
import lombok.*;
import main.FcmWIthAuth.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenValue;

    private LocalDate expirationDate;

    @OneToMany(mappedBy = "token")
    private List<TopicToken> topicTokens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
