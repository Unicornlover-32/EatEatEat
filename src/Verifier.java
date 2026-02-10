package src;

import org.apache.commons.validator.routines.EmailValidator;

public class Verifier
{
    public static void verifyEmailFormat(String email) throws EmailConfirmationException
    {
        EmailValidator emailValidator = EmailValidator.getInstance();

        boolean emailIsValid = emailValidator.isValid(email);
        if(emailIsValid)
        {
            System.out.println("Email verified");
        }
        else throw new EmailConfirmationException("Invalid email format");
    }
}
