package ua.nure.st.kpp.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.entity.User;
import ua.nure.st.kpp.example.demo.form.user.RegistrationForm;
import ua.nure.st.kpp.example.demo.service.TransformerService;
import ua.nure.st.kpp.example.demo.service.UserService;

/**
 * @author Stanislav Hlova
 */

@Controller
public class UserController {

    private final UserService userService;
    private final TransformerService transformerService;

    @Autowired
    public UserController(UserService userService, TransformerService transformerService) {
        this.userService = userService;
        this.transformerService = transformerService;
    }
    @GetMapping("/login")
    public String login(){
        return "users/login";
    }

    @GetMapping("/register")
    public String registrationPage(Model model, RegistrationForm registrationForm) {
        model.addAttribute("registrationForm", registrationForm);
        return "users/registration";
    }

    @PostMapping("/register")
    public String register(RegistrationForm registrationForm) throws DAOException {
        User user = transformerService.toUser(registrationForm);
        userService.register(user);
        return "redirect:/login";
    }
}
