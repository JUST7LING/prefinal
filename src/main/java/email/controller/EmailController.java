package email.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.mail.internet.MimeMessage;
import jwt.TokenServiceTestService;

@CrossOrigin
@Controller
@RequestMapping(value = "email")
public class EmailController {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TokenServiceTestService tokentest;
	
	
	@PostMapping(value = "sendEmail")
	@ResponseBody
	public void sendEmail(@RequestParam(required = false, defaultValue = "1") int emailType,
						@RequestParam String emailId, @RequestParam String emailDomain  
						) {
		// emailType => 링크로 연결하려면 1, 인증번호를 보내려면 2
		String emailAddr = emailId+"@"+emailDomain;
		System.out.println(emailAddr+" and emailType: "+emailType);
		String subject = "";
		String content = "";
		String whenOops = "";
		
		switch(emailType) {
		 
		  case 1: {
			  String tokenURL = "";
			  subject = "[Space#] SpaceSharp에서 비밀번호 찾기를 요청하셨나요?";
			  content = "<!DOCTYPE HTML> <html> <head> <meta charset = \"UTF-8\"/> <style> #body, #caption{text-align: center;} #caption{font-size: 5em;} table{height: 390px; width: 530px;} #btn{margin-left: 175px; } #oops{font-size: 0.75em; text-align: center; margin-left: 160px; color: gray; } #oopsHref{text-decoration: none;} table{border: 15px solid #86A3B8;} #introduce{font-size: 0.5em; color: gray;} #textColor{ color: #F55050; } .rowSpace{height: 10px;} </style> </head> <body> <div> <table> <tr height = '100px'> <td><div id = 'caption'>SpaceSharp</div></td> </tr> <tr> <td><div id = 'body'> 안녕하세요, SpaceSharp입니다! <br><div class = 'rowSpace'></div> 아래 <span id = 'textColor'>SpaceSharp 열기</span> 버튼을 누르시면 <br><div class = 'rowSpace'></div> 요청하신 이메일 인증이 완료됩니다. </div></td> </tr> <tr height = '100px'> <td><div id = 'btn'><a href = '";
			  tokenURL = "http://localhost:3000/user/auth/"+tokentest.createToken(emailAddr);
			  content += tokenURL;
			  content += "' target = '_blank'><img width = '150px' src = 				'https://me2.do/G87iQ5hN'></a> </div><a href = '";
			  content += whenOops;
			  content += "' id = 'oopsHref'> <span id = 'oops'>Oops! 인증을 요청하지 않았어요</span></a> <br> </td></tr> </table> </div> <span id = 'introduce'>Project Space#, 2023.</span> </body> </html>";
			 
			  break;
		  }
		  
		  case 2 : {
			  int verifNum = 0;
			  subject = "[Space#] SpaceSharp에서 인증번호를 보내드립니다.";
			  content = "<!DOCTYPE HTML> <html> <head> <meta charset = \"UTF-8\"/> <style> #body, #caption{text-align: center;} #caption{font-size: 5em;} table{height: 390px; width: 530px;} #btn{margin-left: 175px; } #oops{font-size: 0.75em; text-align: center; margin-left: 160px; color: gray; } #oopsHref{text-decoration: none;} table{border: 15px solid #86A3B8;} #introduce{font-size: 0.5em; color: gray;} #textColor{ color: #F55050; } .rowSpace{height: 10px;} #verifNums{font-size: 3.5em; color: #F55050} </style> </head> <body> <div> <table> <tr height = '100px'> <td><div id = 'caption'>SpaceSharp</div></td> </tr> <tr> <td><div id = 'body'> 안녕하세요, SpaceSharp입니다! <br><div class = 'rowSpace'></div> 아래 <span id = 'textColor'>인증번호</span>를 정확히 입력해 주세요! <br><div class = 'rowSpace'></div> 인증번호를 입력하시면 요청하신 인증이 완료됩니다. </div></td> </tr> <tr height = '100px'> <td><div id = 'btn'><span id = 'verifNums'>";
			  verifNum = 23628;
			  content += ""+verifNum;
			  content += "</span> </div><a href = '";
			  content += whenOops;
			  content += "' id = 'oopsHref'> <span id = 'oops'>Oops! 인증을 요청하지 않았어요</span></a> <br> </td></tr> </table> </div> <span id = 'introduce'>Project Space#, 2023.</span> </body> </html>";
			  break;
		  }
		  
		  default: {
			  System.out.println("emailType을 확인해 주세요.");
			  break;
		  } 
		  }
		 
		
		String from = "Space# <no-reply@project.spaceSharp.com>";
		String to = "<"+emailAddr+">";
		
		 try 
	        {
	            MimeMessage mail = mailSender.createMimeMessage();
	            MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true, "UTF-8");
	            
	            mailHelper.setFrom(from);
	            mailHelper.setTo(to);
	            mailHelper.setSubject(subject);
	            mailHelper.setText(content, true);
	            
	            mailSender.send(mail);
	        } 
	        catch(Exception e) 
	        {
	            e.printStackTrace();
	        }
	        
		
	}
	
	@PostMapping(value = "emailAuth")
	@ResponseBody
	public String authEmail(@RequestParam String token) {
		// 토큰 해금
		
		System.out.println("인증 요청됨 : "+token);
		try {
		tokentest.decodeToken(token);}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "Y";
	}
	
	@PostMapping(value = "shortURL")
	@ResponseBody
	public String shortURL(@RequestParam String Inputurl) {
		    String clientId = "z27v9n36ke";//애플리케이션 클라이언트 아이디값";
	        String clientSecret = "JxJ9Y4wLIUJuQTj1YwxwrL5Z5YqX7BA2ThQ3k8ob";//애플리케이션 클라이언트 시크릿값";
	        try {
	            String text = Inputurl;
	            String apiURL = "https://naveropenapi.apigw.ntruss.com/util/v1/shorturl";
	            URL url = new URL(apiURL);
	            HttpURLConnection con = (HttpURLConnection)url.openConnection();
	            con.setRequestMethod("POST");
	            con.setDoOutput(true); // 이 항목을 추가
	            con.setRequestProperty("Content-Type", "application/json");
	            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
	            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
	            // post request
	            JSONObject json = new JSONObject();
	            //json dependency를 추가해야 사용 가능하다.
	            json.put("url", text);
	            String postParams = json.toString();
	            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	            wr.writeBytes(postParams);
	            wr.flush();
	            wr.close();
	            int responseCode = con.getResponseCode();
	            BufferedReader br;
	            if(responseCode==200) { // 정상 호출
	                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            } else {  // 오류 발생
	                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	            }
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = br.readLine()) != null) {
	                response.append(inputLine);
	            }
	            br.close();
	            System.out.println(response.toString());
	            return response.toString();
	        } catch (Exception e) {
	            System.out.println(e);
	            return "error.";
	        }
			
	}
	
	@GetMapping(value = "returnTest")
	//@ResponseBody
	public String returnTest() {
		System.out.println("IN");
		return "redirect:http://127.0.0.1:3000/result";
	}
	
	
	
}
