// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

import org.apache.commons.validator.routines.EmailValidator;

// This class will verify the input from the user
public class Verifier
{
    public boolean verifyEmailFormat(String email)
    {
        EmailValidator emailValidator = EmailValidator.getInstance();

        // Check if the email address is valid
        // returns true if valid, false otherwise
        return emailValidator.isValid(email);
    }

    public boolean verifyPasswordMatch(String password, String confirmPassword)
    {
        // Check if the passwords match
        // returns true if match, false otherwise
        return password.equals(confirmPassword);
    }

    public boolean verifyPasswordLength(String password)
    {
        // Check if the password length is at least 8 characters
        // returns true if length is at least 8, false otherwise
        return password.length() >= 8;
    }
}
