package jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import io.jsonwebtoken.security.Keys;

@Service
public class tokenTest implements TokenServiceTestService {
	
	private String keyIn = "안녕하세요여기는스페이스샵입니다비밀번호찾기를요청하셨습니다";
		@Override
	    public String createToken(String userEmail) {

	        //Header 부분 설정
	        Map<String, Object> headers = new HashMap<>();
	        headers.put("typ", "JWT");
	        headers.put("alg", "HS256");
	        // -> 헤더에 타입과 알고리즘을 지정한다. 이 부분은 건드릴 필요가 없을 것 같음

	        //payload 부분 설정
	        Map<String, Object> payloads = new HashMap<>();
	        payloads.put("greeting", "Hello, Space#!");
	        payloads.put("space#method","email-link-authorize");
	        payloads.put("space#date","2023-11-16");
	        payloads.put("space#request","re:pwd");
	        payloads.put("requeste#user", userEmail);

	        Long expiredTime = 1000 * 60L * 5L; // 5분  -> ? 이거 30분으로 줄 거임.

	        Date date = new Date(); // 토큰 만료 시간
	        date.setTime(date.getTime() + expiredTime);
	    
	        SecretKey key = Keys.hmacShaKeyFor(keyIn.getBytes(StandardCharsets.UTF_8));
	        
	        
	        // 토큰 Builder
	        String jwt = Jwts.builder()
	                .setHeader(headers) // Headers 설정
	                .setClaims(payloads) // Claims 설정
	                .setSubject("Test") // 토큰 용도
	                .setExpiration(date) // 토큰 만료 시간 설정
	                .signWith(SignatureAlgorithm.HS256, key)
	                .compact(); // 토큰 생성


	        System.out.println(">> jwt : " + jwt);
	        return jwt;
	    }
		
		@Override
		public String decodeToken(String token) throws Exception {
			String[] chunks = token.split("\\.");
			Base64.Decoder decoder = Base64.getDecoder();
			String header = new String(decoder.decode(chunks[0]));
			String payload = new String(decoder.decode(chunks[1]));
			
			System.out.println("header : " + header);
			System.out.println("payload : " + payload);
			SignatureAlgorithm sa = SignatureAlgorithm.HS256;
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyIn.getBytes(), sa.getJcaName());
			String tokenWithoutSignature = chunks[0] + "." + chunks[1];
			String signature = chunks[2];
			DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);

			if (!validator.isValid(tokenWithoutSignature, signature)) {
			    throw new Exception("Could not verify JWT token integrity!");
			}
			return null;
		}
	}

