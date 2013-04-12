package com.search.manager.xml.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;

public class SpellIndex {

    private static final Logger logger = LoggerFactory.getLogger(SpellIndex.class);

    private static final String BASE_RULE_DIR = PropsUtils.getValue("rulepath");
    private static final String SPELL_FILE = PropsUtils.getValue("spellfile");
    private static final RuleEntity ENTITY = RuleEntity.SPELL;

    private Map<String, SpellRules> rules = new HashMap<String, SpellRules>();
    private Map<String, String> xmlPath = new HashMap<String, String>();
    private String dirPath;

    public void init() throws Exception {
        dirPath = new StringBuilder()
                .append(BASE_RULE_DIR)
                .append(File.separator)
                .append(RuleEntity.getValue(ENTITY.getCode()) != null ? RuleEntity.getValue(ENTITY.getCode()) : ENTITY
                        .name()).toString();
        if (!FileUtil.isDirectoryExist(dirPath)) {
            FileUtil.createDirectory(dirPath);
        }
    }

    public void load(String store) throws Exception {
        if (xmlPath.get(store) == null) {
            String storeDir = new StringBuilder().append(dirPath).append(File.separator).append(store).toString();
            String spellFile = new StringBuilder().append(storeDir).append(File.separator).append(SPELL_FILE)
                    .toString();
            xmlPath.put(store, spellFile);

            if (!FileUtil.isDirectoryExist(storeDir)) {
                FileUtil.createDirectory(storeDir);
                rules.put(store, new SpellRules());
            } else if (!FileUtil.isExist(spellFile)) {
                rules.put(store, new SpellRules());
            } else {
                rules.put(store, read(xmlPath.get(store)));
            }
        }
    }

    public void unload(String store) throws Exception {
        xmlPath.remove(store);
        rules.remove(store);
    }

    public void reload(String store) throws Exception {
        unload(store);
        load(store);
    }

    public SpellRules get(String store) {
        if (rules.get(store) == null) {
            try {
                load(store);
            } catch (Exception e) {
                logger.error("Unable to load spell rules for " + store, e);
            }
        }

        return rules.get(store);
    }

    public boolean save(String storeId) {
        return write(rules.get(storeId), xmlPath.get(storeId));
    }

    public void destroy() throws Exception {
        logger.info("Destroying spell index.");
        for (String store : xmlPath.keySet()) {
            String filepath = xmlPath.get(store);
            SpellRules rules = this.rules.get(store);

            logger.info("Writing spell rules for {} to file.", store);
            write(rules, filepath);
        }
        logger.info("Spell index destroyed.");
    }

    private SpellRules read(String filepath) {
        FileReader reader = null;
        SpellRules rules = null;

        try {
            JAXBContext context = JAXBContext.newInstance(SpellRules.class);
            Unmarshaller m = context.createUnmarshaller();

            reader = new FileReader(filepath);
            rules = (SpellRules) m.unmarshal(reader);
            rules.generateSecondaryIndex();
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller", e);
        } catch (Exception e) {
            logger.error("Unknown error", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return rules;
    }

    private boolean write(SpellRules rules, String filepath) {
        boolean success = false;
        FileWriter writer = null;

        try {
            JAXBContext context = JAXBContext.newInstance(SpellRules.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            writer = new FileWriter(filepath);
            m.marshal(rules, writer);
            success = true;
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller", e);
        } catch (Exception e) {
            logger.error("Unknown error", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }

        return success;
    }
}
