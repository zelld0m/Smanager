package com.search.manager.report.model.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleVersionValidationEventHandler implements ValidationEventHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(RuleVersionValidationEventHandler.class);

    @Override
    public boolean handleEvent(ValidationEvent event) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nEVENT");
        sb.append("\nSEVERITY:  " + event.getSeverity());
        sb.append("\nMESSAGE:  " + event.getMessage());
        sb.append("\nLINKED EXCEPTION:  " + event.getLinkedException());
        sb.append("\nLOCATOR");
        if (event.getLocator() != null) {
            sb.append("\n    LINE NUMBER:  " + event.getLocator().getLineNumber());
            sb.append("\n    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
            sb.append("\n    OFFSET:  " + event.getLocator().getOffset());
            sb.append("\n    OBJECT:  " + event.getLocator().getObject());
            sb.append("\n    NODE:  " + event.getLocator().getNode());
            sb.append("\n    URL:  " + event.getLocator().getURL());
        }
        logger.error(sb.toString());
        return true;
    }
}
