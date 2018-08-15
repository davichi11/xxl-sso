package com.xxl.sso.core.store;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.user.User;
import com.xxl.sso.core.util.JedisUtil;

/**
 * local login store
 *
 * @author xuxueli 2018-04-02 20:03:11
 */
public class SsoLoginStore {

    /**
     * get
     *
     * @param sessionId
     * @return
     */
    public static User get(String sessionId) {

        String redisKey = redisKey(sessionId);
        Object objectValue = JedisUtil.getObjectValue(redisKey);
        return objectValue != null ? (User) objectValue : null;
    }

    /**
     * remove
     *
     * @param sessionId
     */
    public static void remove(String sessionId) {
        String redisKey = redisKey(sessionId);
        JedisUtil.del(redisKey);
    }

    /**
     * put
     *
     * @param sessionId
     * @param user
     */
    public static void put(String sessionId, User user) {
        String redisKey = redisKey(sessionId);
        JedisUtil.setObjectValue(redisKey, user);
    }

    private static String redisKey(String sessionId){
        return Conf.SSO_SESSIONID.concat("#").concat(sessionId);
    }

}
