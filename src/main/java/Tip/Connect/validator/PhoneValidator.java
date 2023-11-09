package Tip.Connect.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class PhoneValidator implements Predicate<String> {
    private static final String PHONE_REGEX = "^[0-9\\-\\+]{9,15}$";
    private static final Pattern pattern = Pattern.compile(PHONE_REGEX);

    @Override
    public boolean test(String number) {
        if (number == null) {
            return false;
        }
        return pattern.matcher(number).matches();
    }
}