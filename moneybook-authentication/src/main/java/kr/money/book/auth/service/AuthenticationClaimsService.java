package kr.money.book.auth.service;

import kr.money.book.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationClaimsService extends AbstractAuthenticationService {

    public AuthenticationClaimsService(JwtUtil jwtUtil) {
        super(jwtUtil);
    }
}
