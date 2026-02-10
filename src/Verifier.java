package src;

import org.apache.commons.validator.routines.EmailValidator;

public class Verifier
{
    public static boolean verifyEmailFormat(String email)
    {
        EmailValidator emailValidator = EmailValidator.getInstance();

        boolean emailIsValid = emailValidator.isValid(email);
        if(emailIsValid)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
