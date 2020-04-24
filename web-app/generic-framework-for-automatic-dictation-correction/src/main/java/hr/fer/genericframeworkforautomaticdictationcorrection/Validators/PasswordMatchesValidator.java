package hr.fer.genericframeworkforautomaticdictationcorrection.Validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewUserForm;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        NewUserForm user = (NewUserForm) obj;
        return user.getPassword().equals(user.getRepeatedPassword());
    }
}

