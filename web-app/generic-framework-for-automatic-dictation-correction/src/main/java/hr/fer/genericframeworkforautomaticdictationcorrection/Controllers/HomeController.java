package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(Constants.Paths.BASE_PATH)
    public String welcome(){
        return Constants.Views.INDEX;
    }

    @RequestMapping(Constants.Paths.HOME)
    public String home(){
        return Constants.Redirect.BASE_PATH;
    }
}
