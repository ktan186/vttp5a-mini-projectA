package sg.edu.nus.iss.vttp5a_mini_project_a.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.vttp5a_mini_project_a.model.Login;
import sg.edu.nus.iss.vttp5a_mini_project_a.repository.MapRepo;

@Service
public class LoginService {
    
    @Autowired
    MapRepo loginRepo;

    public void saveLogin(String userId, Login login) {
        loginRepo.putValue(userId, "login", login);
    }

    public Login getLogin(String userId) {
        return (Login) loginRepo.getValue(userId, "login");
    }

    public Boolean hasLogin(String userId) {
        return loginRepo.hasValue(userId, "login");
    }
}
