package main.FcmWIthAuth.user.entity;

import jakarta.persistence.*;
import lombok.*;
import main.FcmWIthAuth.fcm.entity.Token;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Token> tokens;


}
