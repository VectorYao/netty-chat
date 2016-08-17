package wiki.tony.chat.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import wiki.tony.chat.base.bean.AuthToken;
import wiki.tony.chat.base.service.AuthService;
import wiki.tony.chat.logic.config.CachingConfig;

/**
 * 授权
 * <p>
 * Created by Tony on 4/14/16.
 */
@Component
public class AuthServiceImpl implements AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean auth(int serverId, AuthToken authToken) {
        cacheManager.getCache(CachingConfig.ONLINE).put(authToken.getUserId(), serverId);

        logger.debug("auth serverId={}, userId={}, token={}", serverId, authToken.getUserId(), authToken.getToken());
        return true;
    }

    @Override
    public boolean quit(int serverId, AuthToken authToken) {
        return true;
    }
}
