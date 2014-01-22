package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.AuditTrailDao;
import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.dao.CommentDao;
import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.dao.ImportRuleTaskDao;
import com.search.manager.core.dao.RuleStatusDao;

public class DaoSpConfigurationTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("ruleStatusDaoSp")
    private RuleStatusDao ruleStatusDao;
    @Autowired
    @Qualifier("auditTrailDaoSp")
    private AuditTrailDao auditTrailDao;
    @Autowired
    @Qualifier("commentDaoSp")
    private CommentDao commentDao;
    @Autowired
    @Qualifier("importRuleTaskDaoSp")
    private ImportRuleTaskDao importRuleTaskDao;
    // rule
    @Autowired
    @Qualifier("bannerRuleDaoSp")
    private BannerRuleDao bannerRuleDao;
    @Autowired
    @Qualifier("bannerRuleItemDaoSp")
    private BannerRuleItemDao bannerRuleItemDao;
    @Autowired
    @Qualifier("imagePathDaoSp")
    private ImagePathDao imagePathDao;

    @Test
    public void testWiring() {
        assertNotNull(ruleStatusDao);
        assertNotNull(auditTrailDao);
        assertNotNull(commentDao);
        assertNotNull(importRuleTaskDao);
        // rule
        assertNotNull(bannerRuleDao);
        assertNotNull(bannerRuleItemDao);
        assertNotNull(imagePathDao);
    }

}
