package com.search.manager.dao.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.ProductDetailsAware;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.StringUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRuleVersionDAO<T extends RuleXml> implements IRuleVersionDAO<T> {

    private static final Logger logger =
            LoggerFactory.getLogger(AbstractRuleVersionDAO.class);

    protected abstract RuleEntity getRuleEntity();

    protected abstract boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes, boolean isVersion);

    protected RuleVersionListXml<?> getRuleVersionList(String store, String ruleId) {
        return RuleVersionUtil.getRuleVersionList(store, getRuleEntity(), ruleId);
    }

    protected RuleVersionListXml<?> getPublishedList(String store, String ruleId) {
        return RuleVersionUtil.getPublishedList(store, getRuleEntity(), ruleId);
    }

    @Override
    public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes) {
        RuleVersionListXml<?> ruleVersionListXml = getRuleVersionList(store, ruleId);
        if (ruleVersionListXml != null) {
            if (!addLatestVersion(ruleVersionListXml, store, ruleId, username, name, notes, true)) {
                return false;
            }
        }
        return RuleVersionUtil.addRuleVersion(store, getRuleEntity(), ruleId, ruleVersionListXml);
    }

    @Override
    public boolean createPublishedRuleVersion(String store, String ruleId, String username, String name, String notes) {
        RuleVersionListXml<?> ruleVersionListXml = getPublishedList(store, ruleId);
        RuleEntity entity = getRuleEntity();

        if (ruleVersionListXml != null) {
            if (!addLatestVersion(ruleVersionListXml, store, ruleId, username, name, notes, false)) {
                return false;
            }

            List<?> versions = ruleVersionListXml.getVersions();

            if (versions != null) {
                RuleXml latestRuleXml = (RuleXml) versions.get(versions.size() - 1);
                RuleStatus ruleStatus = RuleXmlUtil.getRuleStatus(RuleEntity.getValue(entity.getCode()), store, ruleId);

                latestRuleXml.setRuleStatus(ruleStatus);
            }
        }

        return RuleVersionUtil.addPublishedVersion(store, entity, ruleId, ruleVersionListXml);
    }

    @Override
    public boolean restoreRuleVersion(RuleXml xml) {
        return RuleXmlUtil.restoreRule(xml);
    }

    ;

	public String getRuleVersionFilename(String store, String ruleId) {
        return RuleVersionUtil.getRuleVersionFilename(store, getRuleEntity(), StringUtil.escapeKeyword(ruleId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean deleteRuleVersion(String store, String ruleId, final String username, final long version) {
        FileWriter writer = null;
        try {
            String filename = getRuleVersionFilename(store, ruleId);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            Document prefsDom = db.parse(filename);
            prefsDom.setXmlStandalone(true);
            JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
            Binder<Node> binder = context.createBinder();
            RuleVersionListXml<RuleXml> prefsJaxb = (RuleVersionListXml<RuleXml>) binder.unmarshal(prefsDom);

            List<RuleXml> versions = prefsJaxb.getVersions();

            CollectionUtils.forAllDo(versions, new Closure() {
                public void execute(Object o) {
                    if (((T) o).getVersion() == version) {
                        ((T) o).setDeleted(true);
                        ((T) o).setLastModifiedBy(username);
                        ((T) o).setLastModifiedDate(new DateTime());
                    }
                }
            ;
            });

			prefsJaxb.setVersions(versions);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            writer = new FileWriter(filename);
            m.marshal(prefsJaxb, writer);

            return true;
        } catch (JAXBException e) {
            logger.error("JAXBException");
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException");
        } catch (SAXException e) {
            logger.error("SAXException");
        } catch (IOException e) {
            logger.error("IOException");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public List<RuleXml> getRuleVersions(RuleVersionListXml<?> ruleVersionListXml) {
        List<RuleXml> ruleVersionInfoList = new ArrayList<RuleXml>();
        if (ruleVersionListXml != null) {
            List<?> ruleXmlList = ruleVersionListXml.getVersions();

            if (CollectionUtils.isNotEmpty(ruleXmlList)) {
                for (RuleXml ruleVersion : (List<RuleXml>) ruleXmlList) {
                    if (!ruleVersion.isDeleted()) {
                        if (ruleVersion instanceof ElevateRuleXml || ruleVersion instanceof ExcludeRuleXml || ruleVersion instanceof DemoteRuleXml) {
                            ProductDetailsAware productDetailsAware = (ProductDetailsAware) ruleVersion;
                            productDetailsAware.setProducts(RuleXmlUtil.getProductDetails(ruleVersion, UtilityService.getStoreId()));
                            ruleVersionInfoList.add((RuleXml) productDetailsAware);
                        } else {
                            ruleVersionInfoList.add(ruleVersion);
                        }
                    }
                }

                Collections.sort(ruleVersionInfoList, new Comparator<RuleXml>() {
                    @Override
                    public int compare(RuleXml r1, RuleXml r2) {
                        return r2.getVersion() < r1.getVersion() ? 0 : 1;
                    }
                });
            }
        }
        return ruleVersionInfoList;
    }

    @Override
    public List<RuleXml> getPublishedRuleVersions(String store, String ruleId) {
        return getRuleVersions(getPublishedList(store, ruleId));
    }

    @Override
    public List<RuleXml> getRuleVersions(String store, String ruleId) {
        return getRuleVersions(getRuleVersionList(store, ruleId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getRuleVersionsCount(String store, String ruleId) {
        RuleVersionListXml<?> ruleVersionListXml = getRuleVersionList(store, ruleId);
        int count = 0;

        List<?> ruleXmlList = ruleVersionListXml.getVersions();
        if (ruleVersionListXml != null && CollectionUtils.isNotEmpty(ruleXmlList)) {
            for (RuleXml ruleVersion : (List<RuleXml>) ruleXmlList) {
                if (!ruleVersion.isDeleted()) {
                    count++;
                }
            }
        }

        return count;
    }
}
