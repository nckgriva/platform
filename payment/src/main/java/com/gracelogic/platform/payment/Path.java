package com.gracelogic.platform.payment;

import com.gracelogic.platform.web.PlatformPath;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:32
 */
public class Path extends PlatformPath {
    public static final String PLATFORM_PAYMENT = PLATFORM + "/payment";

    public static final String PAYMENT_YANDEX_MONEY = PLATFORM_PAYMENT + "/yandex-money";
    public static final String PAYMENT_SBRF = PLATFORM_PAYMENT + "/sbrf";
    public static final String PAYMENT_ELECSNET = PLATFORM_PAYMENT + "/elecsnet";
}
