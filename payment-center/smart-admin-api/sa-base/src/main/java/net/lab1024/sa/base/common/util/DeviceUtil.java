package net.lab1024.sa.base.common.util;

/**
 * @author 孙宇
 * @date 2025/02/19 14:14
 */
public class DeviceUtil {

    public static boolean isMobile(String userAgent) {
        String[] mobileAgents = {"iphone", "ipod", "ipad", "android", "mobile", "blackberry", "webos", "incognito", "webmate", "bada", "nokia", "lg", "ucweb", "skyfire", "openwave", "opera mini", "operamini", "bb10", "fennec"};
        boolean isMobile = false;
        if (null == userAgent) {
            userAgent = "";
        }
        for (String mobileAgent : mobileAgents) {
            if (userAgent.contains(mobileAgent)) {
                isMobile = true;
                break;
            }
        }
        return isMobile
                || (userAgent.matches(".*iPhone.*")
                || userAgent.matches(".*iPad.*")
                || userAgent.matches(".*iPod.*")
                || userAgent.matches(".*Android.*")
                || userAgent.matches(".*Mobile.*")
                || userAgent.matches(".*Windows Phone.*")
                || userAgent.matches(".*BlackBerry.*")
                || userAgent.matches(".*MIDP.*")
                );
    }


}
