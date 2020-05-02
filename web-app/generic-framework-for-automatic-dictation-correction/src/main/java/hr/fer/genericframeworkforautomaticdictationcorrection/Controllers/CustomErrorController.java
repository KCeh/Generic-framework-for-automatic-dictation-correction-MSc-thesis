package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
public class CustomErrorController implements ErrorController {

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(Constants.Paths.ERROR)
    public String handleError(HttpServletRequest request, Model model, Locale locale)
    {
        String messageKey = "page.error.message";

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (!ObjectUtils.isEmpty(status))
        {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value())
            {
                messageKey = "page.error404.message";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
            {
                messageKey = "page.error500.message";
            }
        }

        String message= messageSource.getMessage(messageKey, null, locale);
        model.addAttribute("message", message);
        return Constants.Views.ERROR;
    }

    @Override
    public String getErrorPath() {
        return Constants.Paths.ERROR;
    }
}
