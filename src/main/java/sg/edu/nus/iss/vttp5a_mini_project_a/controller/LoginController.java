package sg.edu.nus.iss.vttp5a_mini_project_a.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_mini_project_a.model.Login;
import sg.edu.nus.iss.vttp5a_mini_project_a.service.LoginService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("")
public class LoginController {
    
    @Autowired
    LoginService loginService;
    
    @GetMapping("/")
    public String login(Model model) {
        Login user = new Login();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/")
    public String postLogin(@Valid @ModelAttribute("user") Login user, BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "login";
        }
        
        if (!loginService.hasLogin(user.getUserId())) {
            model.addAttribute("error", "User ID does not exist. Please create a new account.");
            return "login";
        }

        Login login = loginService.getLogin(user.getUserId());
        if (!login.getPassword().equals(user.getPassword())) {
            model.addAttribute("error", "Invalid password.");
            return "login";
        }

        session.setAttribute("user", user);

        return "redirect:/inventory";
    }
    
    @GetMapping("/createuser")
    public String createUser(Model model) {
        Login user = new Login();
        model.addAttribute("user", user);
        return "createuser";
    }

    @PostMapping("/createuser")
    public String postCreateUser(@Valid @ModelAttribute("user") Login user, BindingResult result, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (result.hasErrors()) {
            return "createuser";
        }
        
        if (loginService.hasLogin(user.getUserId())) {
            model.addAttribute("error", "User ID already exists. Please use another unique User ID.");
            return "createuser";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "createuser";
        }
        
        loginService.saveLogin(user.getUserId(), user);

        return "usercreated";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}
