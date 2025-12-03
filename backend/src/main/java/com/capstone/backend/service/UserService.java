package com.capstone.backend.service;

import java.time.LocalDate;

import com.capstone.backend.domain.User;
import com.capstone.backend.dto.RegisterRequest;
import com.capstone.backend.dto.AddressRequest;
import com.capstone.backend.domain.Address;
import com.capstone.backend.repository.UserRepository;
import com.capstone.backend.repository.AddressRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AddressRepository addressRepository;

    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public User register(RegisterRequest req) {
        if (userRepository.findById(req.getUserid()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }
        String road = firstNonEmpty(req.getAddressRoad(), req.getAddress());

        String encodedPassword = encoder.encode(req.getPassword());
        LocalDate parsedBirthdate = LocalDate.parse(req.getBirthdate());

        User user = User.builder()
                        .username(req.getUsername())
                        .userid(req.getUserid())
                        .password(encodedPassword)
                        .birthdate(parsedBirthdate)
                        .phone(req.getPhone())
                        .addressRoad(road)
                        .addressJibun(emptyToNull(req.getAddressJibun()))
                        .postCode(emptyToNull(req.getPostCode()))
                        .addressDetail(emptyToNull(req.getAddressDetail()))
                        .build();

        Address defaultAddr = Address.builder()
                            .addressRoad(road)
                            .addressJibun(emptyToNull(req.getAddressJibun()))
                            .postCode(emptyToNull(req.getPostCode()))
                            .addressDetail(emptyToNull(req.getAddressDetail()))
                            .isDefault(true)
                            .build();
        user.getAddresses().add(defaultAddr);
        return userRepository.save(user); //MongoDB에 저장
    }

    @Transactional
    public User addAddress(String userid, AddressRequest req) {
        logger.info("[주소 추가] 시작 userId={}, req={}", userid, req);
        try {
            User user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

            Address addr = Address.builder()
                .addressRoad(req.getAddressRoad())
                .addressJibun(emptyToNull(req.getAddressJibun()))
                .postCode(emptyToNull(req.getPostCode()))
                .addressDetail(emptyToNull(req.getAddressDetail()))
                .isDefault(false)
                .build();

            user.getAddresses().add(addr);
            User saved = userRepository.save(user);
            logger.info("[주소 추가] 완료 userId={}, addedAddress={}", userid, addr);
            return saved;
        } catch (Exception e) {
            logger.error("[주소 추가] 실패 userId={}, error={}", userid, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public User changeCurrentAddress(String userid, AddressRequest req) {
        logger.info("[대표 주소 변경] 시작 userId={}, req={}", userid, req);
        try {
            User user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

            user.getAddresses().forEach(a -> a.setDefault(false));
            logger.info("[대표 주소 변경] 기존 주소 전부 isDefault=false 처리 완료 userId={}", userid);

            Address target = user.getAddresses().stream()
                .filter(a -> equalsAddr(a, req))
                .findFirst()
                .orElseGet(() -> {
                    Address na = Address.builder()
                        .addressRoad(req.getAddressRoad())
                        .addressJibun(emptyToNull(req.getAddressJibun()))
                        .postCode(emptyToNull(req.getPostCode()))
                        .addressDetail(emptyToNull(req.getAddressDetail()))
                        .isDefault(false)
                        .build();
                    user.getAddresses().add(na);
                    logger.info("[대표 주소 변경] 새로운 주소 생성 및 추가 userId={}, newAddr={}", userid, na);
                    return na;
                });

            target.setDefault(true);

            // 대표 주소 캐시 동기화
            user.setAddressRoad(target.getAddressRoad());
            user.setAddressJibun(target.getAddressJibun());
            user.setPostCode(target.getPostCode());
            user.setAddressDetail(target.getAddressDetail());

            User saved = userRepository.save(user);
            logger.info("[대표 주소 변경] 완료 userId={}, currentAddr={}", userid, target);
            return saved;
        } catch (Exception e) {
            logger.error("[대표 주소 변경] 실패 userId={}, error={}", userid, e.getMessage(), e);
            throw e;
        }
    }


    // helpers
    private static String firstNonEmpty(String... ss) {
        for (String s : ss) if (s != null && !s.isBlank()) return s;
        return null;
    }
    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
    private static boolean equalsAddr(Address a, AddressRequest b) {
        return strEq(a.getAddressRoad(), b.getAddressRoad())
            && strEq(a.getAddressJibun(), b.getAddressJibun())
            && strEq(a.getPostCode(), b.getPostCode())
            && strEq(a.getAddressDetail(), b.getAddressDetail()); // ✅
    }
    private static boolean strEq(String x, String y) {
        return (x == null ? "" : x).equals(y == null ? "" : y);
    }
    
    public User login(String userid, String password) {
    System.out.println("🔐 로그인 시도: ID = " + userid + ", 입력 PW = " + password);

    return userRepository.findByUserid(userid)
        .map(user -> {
            System.out.println("✅ 사용자 찾음: " + user.getUserid());
            boolean passwordMatches = encoder.matches(password, user.getPassword());
            System.out.println("🔍 비밀번호 일치 여부: " + passwordMatches);

            if (passwordMatches) {
                return user;
            } else {
                throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        })
        .orElseThrow(() -> {
            System.out.println("❌ 사용자 ID를 찾을 수 없음: " + userid);
            return new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        });
}


    public boolean isIdTaken(String userid) {
        return userRepository.existsByUserid(userid);
    }
}
