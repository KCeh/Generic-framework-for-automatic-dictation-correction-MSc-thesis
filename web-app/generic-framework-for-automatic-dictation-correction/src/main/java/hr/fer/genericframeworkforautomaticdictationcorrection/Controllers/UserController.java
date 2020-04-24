package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @RequestMapping(value = Constants.Paths.LOGIN, method = RequestMethod.GET)
    public String login()
    {
        return Constants.Views.LOGIN;
    }
}
