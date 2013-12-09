package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.PropertiesUtils;
import com.search.manager.xml.file.RuleXmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleVersionUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(RuleVersionUtil.class);
    
    public static final Pattern PATTERN = Pattern.compile("__(.*).xml", Pattern.DOTALL);
    public static final String BACKUP_PATH = PropertiesUtils.getValue("backuppath");
    public static final String PUBLISH_PATH = PropertiesUtils.getValue("publishedfilepath");
    public static final String ROLLBACK_PREFIX = "rpnv";

    @Autowired
    private RuleXmlUtil ruleXmlUtil;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RuleXml getRuleVersion(String store, RuleEntity ruleEntity, String ruleId, int version) {
        RuleVersionListXml ruleVersionListXml = getRuleVersionList(store, ruleEntity, ruleId);
        if (ruleVersionListXml != null) {
            List<RuleXml> ruleXmlList = (List<RuleXml>) ruleVersionListXml.getVersions();

            for (RuleXml xml : ruleXmlList) {
                if (xml.getVersion() == version) {
                    return xml;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private RuleVersionListXml getRuleList(String store, RuleEntity ruleEntity, String ruleId, String location) {
        RuleVersionListXml ruleVersionListXml = new RuleVersionListXml();
        String dir = ruleXmlUtil.getRuleFileDirectory(location, store, ruleEntity);
        String filename = ruleXmlUtil.getFilenameByDir(
                dir,
                ruleXmlUtil.getRuleId(ruleEntity, ruleId));

        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            try {
                FileUtils.forceMkdir(dirFile);
            } catch (IOException e) {
                logger.error("Unable to create directory", e);
                return null;
            }
        }

        FileReader reader = null;
        try {
            JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
            Unmarshaller um = context.createUnmarshaller();
            um.setEventHandler(new RuleVersionValidationEventHandler());
            File file = new File(filename);
            if (file.exists()) {
                reader = new FileReader(filename);
                ruleVersionListXml = (RuleVersionListXml) um.unmarshal(reader);
            } else {
                logger.info("File not found: " + filename);
            }
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller/unmarshaller", e);
            return null;
        } catch (IOException e) {
            logger.error("Unable to create marshaller/unmarshaller", e);
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }

        return ruleVersionListXml;
    }

    @SuppressWarnings("rawtypes")
    public RuleVersionListXml getRuleVersionList(String store, RuleEntity ruleEntity, String ruleId) {
        return getRuleList(store, ruleEntity, ruleId, BACKUP_PATH);
    }

    @SuppressWarnings("rawtypes")
    public RuleVersionListXml getPublishedList(String store, RuleEntity ruleEntity, String ruleId) {
        return getRuleList(store, ruleEntity, ruleId, PUBLISH_PATH);
    }

    public static boolean removeRollbackFile(String filename, long version) {
        File rollbackFile = new File(filename + ROLLBACK_PREFIX + version);
        return FileUtils.deleteQuietly(rollbackFile);
    }

    public static boolean createRollbackFile(String filename, long version) {
        try {
            File file = new File(filename);
            File rollbackFile = new File(filename + ROLLBACK_PREFIX + version);

            if (!rollbackFile.exists()) {
                if (file.exists()) {
                    FileUtils.copyFile(file, rollbackFile, true);
                } else {
                    FileUtils.write(rollbackFile, "");
                }
                if (rollbackFile.exists()) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Unable to create rollback file", e);
            return false;
        } catch (Exception e) {
            logger.error("Unknown error", e);
            return false;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList, String location) {
        long nextVersion = ruleVersionList.getNextVersion();
        FileWriter writer = null;
        String filename = ruleXmlUtil.getFilenameByDir(
                ruleXmlUtil.getRuleFileDirectory(location, store, ruleEntity),
                ruleXmlUtil.getRuleId(ruleEntity, ruleId));

        if (!createRollbackFile(filename, nextVersion)) {
            logger.error("Unable to create rollback file");
            return false;
        };

        try {
            JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ruleVersionList.setNextVersion(nextVersion + 1);
            writer = new FileWriter(filename);
            m.marshal(ruleVersionList, writer);
            if (!removeRollbackFile(filename, nextVersion)) {
                logger.info(String.format("Failed to delete rollback file for next version %l", nextVersion));
            };
            return true;
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller", e);
            return false;
        } catch (Exception e) {
            logger.error("Unknown error", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean saveRuleVersionList(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList, String location) {
        long nextVersion = ruleVersionList.getNextVersion();
        FileWriter writer = null;
        String filename = ruleXmlUtil.getFilenameByDir(
                ruleXmlUtil.getRuleFileDirectory(location, store, ruleEntity),
                ruleXmlUtil.getRuleId(ruleEntity, ruleId));

        if (!createRollbackFile(filename, nextVersion)) {
            logger.error("Unable to create rollback file");
            return false;
        };

        try {
            JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            writer = new FileWriter(filename);
            m.marshal(ruleVersionList, writer);
            if (!removeRollbackFile(filename, nextVersion)) {
                logger.info(String.format("Failed to delete rollback file for version %l", nextVersion));
            };
            return true;
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller", e);
            return false;
        } catch (Exception e) {
            logger.error("Unknown error", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList) {
        return addRuleVersion(store, ruleEntity, ruleId, ruleVersionList, BACKUP_PATH);
    }

    @SuppressWarnings("rawtypes")
    public boolean addPublishedVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList) {
        return addRuleVersion(store, ruleEntity, ruleId, ruleVersionList, PUBLISH_PATH);
    }

    private String getFilename(String store, RuleEntity ruleEntity, String ruleId, String location) {
        return ruleXmlUtil.getFilename(location, store, ruleEntity, ruleId);
    }

    public String getRuleVersionFilename(String store, RuleEntity ruleEntity, String ruleId) {
        return getFilename(store, ruleEntity, ruleId, BACKUP_PATH);
    }

    public String getPublishedFilename(String store, RuleEntity ruleEntity, String ruleId) {
        return getFilename(store, ruleEntity, ruleId, PUBLISH_PATH);
    }
}