package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.AuthenticationRequest;
import com.cntt2.logistics.dto.request.IntrospectRequest;
import com.cntt2.logistics.dto.request.LogoutRequest;
import com.cntt2.logistics.dto.request.RefreshTokenRequest;
import com.cntt2.logistics.entity.Driver;
import com.cntt2.logistics.entity.InvalidatedToken;
import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.User;
import com.cntt2.logistics.repository.DriverRepository;
import com.cntt2.logistics.repository.InvalidatedTokenRepositoy;
import com.cntt2.logistics.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepositoy invalidatedTokenRepositoy;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);

        // 👉 Kiểm tra chữ ký trước
        boolean verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new JOSEException("Invalid JWT signature");
        }

        // 👉 Sau đó mới lấy claims
        var claimsSet = signedJWT.getJWTClaimsSet();

        if (!isRefresh) {
            // Truy cập: kiểm tra hết hạn dựa trên `exp`
            Date expiration = claimsSet.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw new JOSEException("JWT expired");
            }
        } else {
            // Refresh: kiểm tra issueTime + REFRESHABLE_DURATION
            Date issueTime = claimsSet.getIssueTime();
            if (issueTime == null) {
                throw new JOSEException("Invalid JWT: missing issue time");
            }

            Date refreshableUntil = Date.from(
                    issueTime.toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
            );

            if (refreshableUntil.before(new Date())) {
                throw new JOSEException("Refresh window expired");
            }
        }

        // Check đã bị vô hiệu hóa chưa
        if (invalidatedTokenRepositoy.existsById(claimsSet.getJWTID())) {
            throw new JOSEException("Token has been invalidated");
        }

        return signedJWT;
    }

    public Boolean introspect(IntrospectRequest request) throws JOSEException, ParseException {
        try {
            var token = request.getToken();
            SignedJWT signedJWT = verifyToken(token, false);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration.after(new Date());
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public Map<String, Object> authenticate(AuthenticationRequest request) {
        String email = request.getEmail().trim();
        String username = email.contains("@") ? email.split("@")[0] : email;

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

        // Tìm trong bảng User
        Optional<User> user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().startsWith(username + "@"))
                .findFirst();

            boolean isMatch = passwordEncoder.matches(request.getPassword(), user.get().getPassword());
            if (!isMatch) {
                throw new ApplicationContextException("Incorrect password");
            }

            String token = generateToken(user.get());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("role", user.get().getRole());
            return data;
    }


    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jwtID  =signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtID)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepositoy.save(invalidatedToken);
        } catch (ParseException | JOSEException e) {
            throw new ApplicationContextException("Failed to logout");
        }
    }

    public String refreshToken(RefreshTokenRequest request)
            throws ParseException, JOSEException {
        try {
            SignedJWT signedJWT = verifyToken(request.getToken(), true);

            String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            String email = signedJWT.getJWTClaimsSet().getSubject();

            var user = userRepository.findByEmail(email).orElseThrow(
                    () -> new ApplicationContextException("Email does not exist")
            );

            String newToken = generateToken(user);

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtID)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepositoy.save(invalidatedToken);

            return newToken;
        } catch (ParseException | JOSEException e) {
            throw new ApplicationContextException("Failed to refresh token: " + e.getMessage());
        }
    }

    private String generateToken(User user) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("cntt2")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


}
