package com.rab3tech.user.ui.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rab3tech.customer.service.LoginService;
import com.rab3tech.vo.LoginVO;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@GetMapping(value= {"/customer/login","/logout/success"})
	public String showCustomerLogin(@RequestParam(value="error",required=false) boolean messsage,Model model) {
		  if(messsage) {
			  model.addAttribute("error", "Sorry , your username and password are wrong!!!!!!!!!!!");
		  }
		return "customer/login";	//login.html
	}	
	
	

	@GetMapping(value= {"/access/denied"})
	public String accessDenied(Model model) {
			return "customer/accessDenied";	//accessDenied.html
	}	
	
	@GetMapping(value= {"/customer/dashboard"})
	public String customerDashboard(Model model,HttpSession session) {
		  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		  String username=authentication.getName();// javatech1000@gmail.com
	       //retrieving the role of the logged in user.
	       Collection<? extends GrantedAuthority> grantedList=authentication.getAuthorities();
	       //Here we are assuming last role present inside the list will be actual role of
	       //logged in user.
	       String nextPage="";
	       if(grantedList!=null && grantedList.size()>0){
	         Iterator<? extends GrantedAuthority> iterator=grantedList.iterator();
	         if(iterator.hasNext()){
	             GrantedAuthority ga=iterator.next();
	             nextPage=ga.getAuthority(); //
	         }
	       }
	       //EMPLOYEE_ROLE or CUSTOMER_ROLE,ADMIN_ROLE
	   	String viewName ="customer/dashboard";
		Optional<LoginVO> optional=loginService.findUserByUsername(username);
		if(optional.isPresent()) {
					LoginVO  loginVO2=optional.get();
					loginVO2.setPassword("");
					session.setAttribute("userSessionVO", loginVO2);
					switch (loginVO2.getRoles().get(0)) {
					case "EMPLOYEE":
						viewName ="employee/dashboard";
						break;
					case "CUSTOMER":
						viewName ="customer/dashboard";
						break;
					case "ADMIN":
						viewName ="admin/dashboard";
						break;	
						
					default:
						viewName ="guest";
						break;
				}
				return viewName;	//dashboard.html
		}	
		return viewName;	//dashboard.html
	}	
	
	
	/*@PostMapping(value= {"/customer/login","/auth"})
	public String postLogin(@ModelAttribute LoginVO loginVO,HttpSession session,Model model) {
			Optional<LoginVO> optional=loginService.authUser(loginVO);
			if(optional.isPresent()) {
				LoginVO  loginVO2=optional.get();
				loginVO2.setPassword("*@&@&^@^&@^@");
				session.setAttribute("userSessionVO", loginVO2);
				String viewName="";
				switch (loginVO2.getRoles().get(0)) {
					case "ROLE_EMPLOYEE":
						viewName ="employee/dashboard";
						break;
					case "ROLE_CUSTOMER":
						viewName ="customer/dashboard";
						break;
					default:
						viewName ="guest";
						break;
				}
				return viewName;	//dashboard.html
			}else {
				model.addAttribute("error","Sorry , your username and password are not valid!");
				return "customer/login";	//login.html
			}
		
	}	*/

}
