package com.example.demo;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@Controller
public class SocialApplication {

	/**
	 * サーバーで 401 を生成する
	 * 適切な組織にいないユーザーを拒否するように認証ルールを継承
	 */
//	@Bean
//	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
//	    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//	    return request -> {
//	        OAuth2User user = delegate.loadUser(request);
//	        if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
//	        	return user;
//	        }
//
//	        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
//	                (request.getClientRegistration(), user.getName(), request.getAccessToken());
//	        String url = user.getAttribute("organizations_url");
//	        
//
//            ParameterizedTypeReference<List<Map<String, Object>>> typeRef = 
//                    new ParameterizedTypeReference<>() {};
//                    
//	        List<Map<String, Object>> orgs = rest
//	                .get().uri(url)
//	                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
//	                .retrieve()
//	                .bodyToMono(typeRef)
//	                .block();
//
//	        if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
//	            return user;
//	        }
//
//	        throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
//	    };
//	}
	
	@Bean
	public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
	    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
	            new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
	    return WebClient.builder()
	            .filter(oauth2).build();
	}
	
    @GetMapping("/user")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
    
    @GetMapping("/error")
    @ResponseBody
    public String error(HttpServletRequest request) {
    	String message = (String) request.getSession().getAttribute("error.message");
    	request.getSession().removeAttribute("error.message");

    	return message;
    }

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
}