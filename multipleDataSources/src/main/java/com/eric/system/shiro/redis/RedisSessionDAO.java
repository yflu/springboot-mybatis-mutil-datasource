package com.eric.system.shiro.redis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Component
public class RedisSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    @Autowired
    private RedisService redisService;


    /**
     * The Redis key prefix for the sessions
     */
    private final static String keyPrefix = "shiro_redis_session:";

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    /**
     * save session
     *
     * @param session
     * @throws UnknownSessionException
     */
    private void saveSession(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            return;
        }

        byte[] key = getByteKey(session.getId());
        byte[] value = SerializeUtils.serialize(session);
        this.redisService.set(key, value);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            return;
        }
        redisService.del(this.getByteKey(session.getId()));
    }

    @Override
    public Set<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet();
        Set<String> keys = redisService.keys(this.keyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Session s = (Session) redisService.getObject(key);
                if (s != null)
                    sessions.add(s);
            }
        }
        return sessions;
    }

    /**
     * 踢出同一账号已登录的SESSION
     *
     * @param username
     * @param sessionId
     */
    public void kickOutUser(String username, Serializable sessionId) {
        Set<Session> sessionCollection = getActiveSessions();
        if (sessionCollection != null && sessionCollection.size() > 0) {
            for (Session session : sessionCollection) {
                PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                if (principalCollection == null) {
                    continue;
                } else {
                    String principal = principalCollection.getPrimaryPrincipal().toString();
                    if (username.equals(principal) && !sessionId.equals(session.getId())) {
                        logger.info("踢出用户【" + username + "】已登录的session" + "-->" + session.getId() + ",保留最新session" + sessionId);
                        redisService.del(getByteKey(session.getId()));
                    }
                }
            }
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("session id is null");
            return null;
        }

        if (redisService.exists(this.getByteKey(sessionId))) {
            return (Session) redisService.getObject(this.getByteKey(sessionId));
        }
        return null;
    }


    /**
     * 获得key
     *
     * @param sessionId
     * @return
     */
    public static byte[] getByteKey(Serializable sessionId) {
        return getKey(sessionId).getBytes();
    }

    public static String getKey(Serializable sessionId) {
        return keyPrefix + sessionId;
    }
}