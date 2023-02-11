package contacts;

import java.util.function.Predicate;

class Utils {

    private static final String PHONE_NUM_REGEX_1 = "^\\+?(\\d )?\\(?\\w{3}\\)?[ -]\\(?\\w{2,3}\\)?[ -]\\w{2,4}([ -]\\w{2,4})?$";
    private static final String PHONE_NUM_REGEX_2 = "^\\(?\\w{3,4}\\)?([ -]\\(?\\w{3,4}\\)?)?$";

    private static final String PHONE_NUM_REGEX_3 = "^\\+?\\d? ?\\(?\\w{1,8}\\)?$";
    private static final Predicate<String> IS_VALID_NUMBER = (p) -> p.matches(PHONE_NUM_REGEX_1)
            || p.matches(PHONE_NUM_REGEX_2) || p.matches(PHONE_NUM_REGEX_3);

    public static String validateNumber(String number) {
        if (!IS_VALID_NUMBER.test(number)) {
            System.out.println("Wrong number format!");
            number = "[no number]";
        }
        return number;
    }
}
