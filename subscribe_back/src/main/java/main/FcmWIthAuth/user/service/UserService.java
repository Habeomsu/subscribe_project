package main.FcmWIthAuth.user.service;

import jakarta.transaction.Transactional;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.exception.GeneralException;
import main.FcmWIthAuth.user.dto.JoinDto;
import main.FcmWIthAuth.user.entity.Role;
import main.FcmWIthAuth.user.entity.User;
import main.FcmWIthAuth.user.repository.RefreshRepository;
import main.FcmWIthAuth.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshRepository refreshRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       RefreshRepository refreshRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.refreshRepository = refreshRepository;
    }

    public void joinProcess(JoinDto joinDto) {

        String username=joinDto.getUsername();
        String password=joinDto.getPassword();
        String email=joinDto.getEmail();

        boolean isexist = userRepository.existsByUsername(username);
        if (isexist) {
            throw new GeneralException(ErrorStatus._EXIST_USERNAME);
        }

        User data = User.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .role(Role.ROLE_USER)
                    .email(email)
                    .build();

        userRepository.save(data);

    }

    @Transactional
    public void deleteUser(String username) {
        // 사용자가 존재하는지 확인
        if (!userRepository.existsByUsername(username)) {
            throw new GeneralException(ErrorStatus._USERNAME_NOT_FOUND);
        }

        // 사용자 삭제
        userRepository.deleteByUsername(username);
        refreshRepository.deleteByUsername(username);

    }


}
