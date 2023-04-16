package com.chat.crazy.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午12:48
 */
@Slf4j
public class WebTokenUtil {
    //定义JWT的发布者，这里可以起项目的拥有者
    private static final String TOKEN_ISSUSER = "se8w92jskjdsiodao";
    //定义JWT的有效时长
    
    private static final int TOKEN_VAILDITY_TIME = 3 * 24 * 60 * 60; // 有效时间(秒)
    //定义允许刷新JWT的有效时长(在这个时间范围内，用户的JWT过期了，不需要重新登录，后台会给一个新的JWT给前端，这个叫Token的刷新机制后面会着重介绍它的意义。)
    private static final int ALLOW_EXPIRES_TIME = 2 * 60 * 60; //  允许过期时间(分钟)
    /**
     * 根据用户的登录时间生成动态私钥
     * @param secret 用户的登录时间，也就是申请令牌的时间
     * @return
     */

    public static String genSecretKey(Instant instant){
        return String.valueOf(instant.getEpochSecond());
    }


    /**
     * 生成token
     * @param secretKey 根据用户的登录时间生成的秘钥
     * @param subject JWT中payload部分自定义的内容
     * @param issueAt 用户登录的时间，也就是申请令牌的时间
     * @return
     */
    public static String create(String secretKey, String subject, Instant issueAt) {
        String token = "";
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Instant exp = issueAt.plusSeconds(TOKEN_VAILDITY_TIME);
        token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withClaim("sub", subject)
                .withClaim("iat", Date.from(issueAt))
                .withClaim("exp", Date.from(exp))
                .sign(algorithm);
        log.trace("create token ["+token+"]; ");
        return token;
    }

    /**
     * 字符串token 解析为 jwtToken
     * @param token 要解析的Token
     * @return
     */
    public static DecodedJWT decode(String token){
        DecodedJWT jwtToken = null;
        try {
            jwtToken = JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwtToken;
    }

    /**
     * 验证token
     * @param secretKey
     * @param token
     * @throws Exception
     */
    public static void verify(String secretKey, String token) throws Exception {
        log.debug("verify token ["+token+"]");
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //校验Token
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(TOKEN_ISSUSER).build();
        verifier.verify(token);
    }

    //刷新Token
    public static String getRefreshToken(String secretKey, DecodedJWT jwtToken) {
        return getRefreshToken(secretKey, jwtToken, TOKEN_VAILDITY_TIME);
    }
    //重载的刷新Token
    public static String getRefreshToken(String secretKey, DecodedJWT jwtToken, int validityTime) {
        return getRefreshToken(secretKey, jwtToken, validityTime, ALLOW_EXPIRES_TIME);
    }

    /**
     * 根据要过期的token获取新token
     * @param secretKey 根据用户上次登录时的时间，生成的密钥
     * @param jwtToken 上次的JWT经过解析后的对象
     * @param validityTime 有效时间
     * @param allowExpiresTime 允许过期的时间
     * @return
     */
    public static String getRefreshToken(String secretKey, DecodedJWT jwtToken, int validityTime, int allowExpiresTime) {
        Instant now = Instant.now();
        Instant exp = jwtToken.getExpiresAt().toInstant();
        //如果当前时间减去JWT过期时间，大于可以重新申请JWT的时间，说明不可以重新申请了，就得重新登录了，此时返回null，否则就是可以重新申请，开始在后台重新生成新的JWT。
        if ((now.getEpochSecond()-exp.getEpochSecond())>allowExpiresTime) {
            return null;
        }
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //在原有的JWT的过期时间的基础上，加上这次的有效时间，得到新的JWT的过期时间
        Instant newExp = exp.plusSeconds(validityTime);
        //创建JWT
        String token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withClaim("sub", jwtToken.getSubject())
                .withClaim("iat", Date.from(exp))
                .withClaim("exp", Date.from(newExp))
                .sign(algorithm);
        log.trace("create refresh token ["+token+"]; iat: "+Date.from(exp)+" exp: "+Date.from(newExp));
        return token;
    }
}
