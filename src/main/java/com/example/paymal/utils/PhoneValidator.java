package com.example.paymal.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;

public class PhoneValidator {
    public static String validateUzbekPhone(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber uzNumber = phoneUtil.parse(number, "UZ");
            if (phoneUtil.isValidNumber(uzNumber)) {
                if (uzNumber.getCountryCode() != 998) {
                    return "Raqam xato: Faqat O'zbekiston raqamlari ruxsat etiladi";
                }
                return "Raqam valid: tur - " + phoneUtil.getNumberType(uzNumber);
            } else {
                return "Raqam mavjud emas";
            }
        } catch (NumberParseException e) {
            return "Raqam formati noto'g'ri";
        }
    }
}
