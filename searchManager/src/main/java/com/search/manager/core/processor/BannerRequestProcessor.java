package com.search.manager.core.processor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.ws.ConfigManager;
import com.search.ws.SolrConstants;
import com.search.ws.SolrResponseParser;

@Component("bannerRequestProcessor")
public class BannerRequestProcessor implements RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BannerRequestProcessor.class);
    // TODO change settings to banner
    private static final String PROPERTY_MODULE_NAME = "settings";

    @Autowired
    private ConfigManager configManager;
    @Autowired
    private RequestProcessorUtil requestProcessorUtil;

    @Autowired
    @Qualifier("bannerRuleItemServiceSp")
    private BannerRuleItemService bannerRuleItemServiceSp;
    @Autowired
    @Qualifier("bannerRuleItemServiceSolr")
    private BannerRuleItemService bannerRuleItemServiceSolr;

    @Override
    public boolean isEnabled(RequestPropertyBean requestPropertyBean) {
        // check request parameter (disableBanner) and configuration file (TODO)
        return !requestPropertyBean.isDisableRule();
    }

    @Override
    public void process(HttpServletRequest request, SolrResponseParser solrHelper,
            RequestPropertyBean requestPropertyBean, List<Map<String, String>> activeRules,
            Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs) {
        List<BannerRuleItem> bannerList = null;

        if (isEnabled(requestPropertyBean) || requestPropertyBean.isGuiRequest()) {
            logger.info("Enabled? true");
            try {
                bannerList = getActiveBannerRuleItems(requestPropertyBean.getStoreId(),
                        requestPropertyBean.getKeyword(), requestPropertyBean.isGuiRequest(),
                        requestPropertyBean.getCurrentDate());
                if (CollectionUtils.isNotEmpty(bannerList)) {
                    activeRules.add((requestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_BANNER,
                            bannerList.get(0).getRule().getRuleId(), requestPropertyBean.getKeyword(),
                            isEnabled(requestPropertyBean))));
                    if (isEnabled(requestPropertyBean)) {
                        logger.info("Applied? true");
                        solrHelper.setBannerRuleItems(bannerList);
                        // TODO: also log expired and non-active banner
                    } else {
                        logger.info("Applied? false");
                    }
                }
            } catch (CoreServiceException e) {
                logger.error("Error Getting Active Banner Rule Item.", e);
            }
        } else {
            logger.info("Enabled? false");
        }
    }

    private List<BannerRuleItem> getActiveBannerRuleItems(String storeId, String keyword, boolean fromSearchGui,
            DateTime currentDate) throws CoreServiceException {
        List<BannerRuleItem> bannerRuleItems = null;
        try {
            if (fromSearchGui) {
                bannerRuleItems = bannerRuleItemServiceSp.getActiveBannerRuleItems(storeId, keyword, currentDate);
            } else {
                bannerRuleItems = bannerRuleItemServiceSolr.getActiveBannerRuleItems(storeId, keyword, currentDate);
            }
        } catch (CoreServiceException e) {
            if (!fromSearchGui) {
                if (!configManager.isSolrImplOnly()) {
                    try {
                        bannerRuleItems = bannerRuleItemServiceSp.getActiveBannerRuleItems(storeId, keyword,
                                currentDate);
                    } catch (CoreServiceException e1) {
                        logger.error("Failed to get active bannerRuleItems {}", e1);
                        return null;
                    }
                } else {
                    return null;
                }
            }
            throw e;
        }

        String autoPrefixProtocol = configManager.getProperty(PROPERTY_MODULE_NAME, storeId,
                DAOConstants.SETTINGS_AUTOPREFIX_BANNER_LINKPATH_PROTOCOL);
        Boolean isAutoPrefixProtocol = BooleanUtils.toBoolean(StringUtils.defaultIfBlank(autoPrefixProtocol, "false"));

        if (bannerRuleItems != null && isAutoPrefixProtocol) {
            for (int i = 0; i < bannerRuleItems.size(); i++) {
                String protocol = StringUtils.defaultIfBlank(configManager.getProperty(PROPERTY_MODULE_NAME, storeId,
                        DAOConstants.SETTINGS_DEFAULT_BANNER_LINKPATH_PROTOCOL), "http:");
                bannerRuleItems.get(i).setLinkPath(
                        (protocol.endsWith(":") ? protocol : protocol + ":") + bannerRuleItems.get(i).getLinkPath());
            }
        }

        return bannerRuleItems;
    }

}
