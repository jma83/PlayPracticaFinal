package validators;

public class StringUtils {
    //https://mkyong.com/regular-expressions/java-regex-check-alphanumeric-string/
    private static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9_]+$";

    public static boolean isAlphanumeric(final String input) {
        return input.matches(ALPHANUMERIC_PATTERN);
    }

    public static boolean checkNameFormat(final String input) {
        return input.matches("(\\w+\\s\\w+)");
    }

    //http://www.techdive.in/java/java-password-validation
    public static boolean checkPasswordFormat(final String password)
    {
        boolean valid = true;

        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars ))
        {
            valid = false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            valid = false;
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches( numbers ))
        {
            valid = false;
        }

        return valid;
    }
}