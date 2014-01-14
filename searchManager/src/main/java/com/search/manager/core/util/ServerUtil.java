package com.search.manager.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mpedcp
 * @since December 16, 2013
 * @version 1.0
 */
public class ServerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServerUtil.class);
    private static InetAddress inetAddress = null;

    static {
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error("", e);
        }
    }

    public static String getHostName() {
        if (inetAddress != null)
            return inetAddress.getHostName();

        return StringUtils.EMPTY;
    }
}