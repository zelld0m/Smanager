package com.search.manager.utility;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XSSUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(XSSUtil.class);
    
    private static final Map<String, WJTXSSThreadLocal> ENGINES = new ConcurrentHashMap<String, WJTXSSThreadLocal>();

    private enum HTMLPATTERN_LIST {

        BLACKLISTED_TAGS, BLACKLISTED_TAGEVENTS, ATTRIB_CMD_WATCHLIST;
        private final Set<String> list = new HashSet<String>();

        public void add(String pattern) {
            list.add(pattern);
        }

        public Set<String> getList() {
            return list;
        }
    }

    /**
     * this class is a wrapper to prevent concurrency problem when using non-threadsafe
     * Pattern.matcher
     *
     * @author genarop
     *
     */
    public static class WJTXSSThreadLocal extends ThreadLocal<Matcher> {

        private final Pattern p;

        public WJTXSSThreadLocal(String regex) {
            p = Pattern.compile(regex);
        }

        @Override
        protected Matcher initialValue() {
            return p.matcher("GMP");
        }

        public Matcher getMatcherEngine(String haystack) {
            return p.matcher(haystack);
        }
    }

    static {
        /**
         * blacklisted tags *
         */
        addRegexPattern("(@[iI][mM][pP][oO][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGS);

        addRegexPattern("(<[/]?[tT][iI][tT][lL][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[sS][tT][yY][lL][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[bB][gG][sS][oO][uU][nN][dD]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[bB][aA][sS][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[eE][mM][bB][eE][dD]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[mM][aA][rR][qQ][uU][eE][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[oO][bB][jJ][eE][cC][tT]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[mM][eE][tT][aA]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[sS][cC][rR][iI][pP][tT]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[fF][rR][aA][mM][eE][sS][eE][tT]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[tT][iI][tT][lL][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[mM][oO][cC][hH][aA]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[lL][aA][yY][eE][rR]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[iI][lL][aA][yY][eE][rR]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[lL][iI][nN][kK]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[xX][mM][lL]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[fF][rR][aA][mM][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[aA][pP][pP][lL][eE][tT]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[iI][fF][rR][aA][mM][eE]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[bB][lL][iI][nN][kK]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[fF][oO][rR][mM]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[iI][nN][pP][uU][tT]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[bB][uU][tT][tT][oO][nN]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[hH][tT][mM][lL]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[mM][aA][pP]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[tT][eE][xX][tT][aA][rR][eE][aA]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[hH][eE][aA][dD]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[bB][oO][dD][yY]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[nN][oO][bB][rR]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[wW][bB][rR]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);
        addRegexPattern("(<[/]?[dD][iI][vV]([\\x20-\\x7E]*?)[/]?>)", HTMLPATTERN_LIST.BLACKLISTED_TAGS);


        /**
         * tag events *
         */
        addRegexPattern("([oO][nN][bB][oO][uU][nN][cC][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][vV][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][lL][aA][yY][oO][uU][tT][cC][oO][mM][pP][lL][eE][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][eE][lL][eE][cC][tT][sS][tT][aA][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][uU][nN][lL][oO][aA][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][bB][lL][cC][lL][iI][cC][kK])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][oO][nN][tT][rR][oO][lL][sS][eE][lL][eE][cC][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][aA][tT][aA][sS][eE][tT][cC][oO][mM][pP][lL][eE][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][oO][pP])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][vV][eE][sS][tT][aA][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][uU][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][dD][oO][wW][nN])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][wW][hH][eE][eE][lL])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][eE][aA][cC][tT][iI][vV][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][tT][oO][pP])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][uU][nN][lL][oO][aA][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][aA][tT][aA][aA][vV][aA][iI][lL][aA][bB][lL][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][eE][rR][rR][oO][rR][uU][pP][dD][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][lL][eE][aA][vV][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][fF][iI][lL][tT][eE][rR][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][vV][eE][eE][nN][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][cC][uU][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][oO][wW][sS][dD][eE][lL][eE][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][fF][oO][cC][uU][sS][oO][uU][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][aA][fF][tT][eE][rR][pP][rR][iI][nN][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][eE][sS][iI][zZ][eE][eE][nN][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][kK][eE][yY][dD][oO][wW][nN])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][kK][eE][yY][pP][rR][eE][sS][sS])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][uU][bB][mM][iI][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][aA][tT][aA][sS][eE][tT][cC][hH][aA][nN][gG][eE][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG][eE][nN][tT][eE][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][cC][rR][oO][lL][lL])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][fF][iI][nN][iI][sS][hH])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][oO][wW][sS][iI][nN][sS][eE][rR][tT][eE][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][tT][aA][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][eE][aA][dD][yY][sS][tT][aA][tT][eE][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][eE][lL][eE][cC][tT][iI][oO][nN][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][pP][rR][iI][nN][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][aA][cC][tT][iI][vV][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][pP][rR][oO][pP][eE][rR][tT][yY][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][lL][uU][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG][oO][vV][eE][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][eE][lL][lL][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][dD][eE][aA][cC][tT][iI][vV][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG][lL][eE][aA][vV][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][oO][wW][eE][nN][tT][eE][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][oO][uU][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG][eE][nN][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][kK][eE][yY][uU][pP])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][fF][oO][cC][uU][sS])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][aA][bB][oO][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][eE][sS][eE][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][sS][eE][lL][eE][cC][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][dD][rR][aA][gG][sS][tT][aA][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][lL][oO][sS][eE][cC][aA][pP][tT][uU][rR][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][oO][pP][yY])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][aA][fF][tT][eE][rR][uU][pP][dD][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][eE][rR][rR][oO][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][uU][pP])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][eE][sS][iI][zZ][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][pP][aA][sS][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][hH][eE][lL][pP])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][mM][oO][vV][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][oO][wW][eE][xX][iI][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][oO][nN][tT][eE][xX][tT][mM][eE][nN][uU])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][cC][oO][pP][yY])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][hH][aA][nN][gG][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][oO][vV][eE][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][rR][eE][sS][iI][zZ][eE][sS][tT][aA][rR][tT])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][eE][dD][iI][tT][fF][oO][cC][uU][sS])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][lL][oO][aA][dD])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][mM][oO][uU][sS][eE][eE][nN][tT][eE][rR])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][pP][aA][sS][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][cC][lL][iI][cC][kK])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][bB][eE][fF][oO][rR][eE][uU][pP][dD][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][aA][cC][tT][iI][vV][aA][tT][eE])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);
        addRegexPattern("([oO][nN][fF][oO][cC][uU][sS][iI][nN])", HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS);

        // tag attributes
        addRegexPattern("([jJ][aA][vV][aA][sS][cC][rR][iI][pP][tT])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("([lL][iI][vV][eE][sS][cC][rR][iI][pP][tT])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("([eE][xX][pP][rR][eE][sS][sS][iI][oO][nN])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("([uU][rR][lL])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("(@[iI][mM][pP][oO][rR][tT])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("([eE][vV][aA][lL])", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
        addRegexPattern("((&\\{)([\\x20-\\x7A\\x7C\\x7E]*?)(\\}))", HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST);
    }

    private static final void addRegexPattern(final String regex, HTMLPATTERN_LIST listType) {
        ENGINES.put(regex, new WJTXSSThreadLocal(regex));

        switch (listType) {
            case BLACKLISTED_TAGS:
                HTMLPATTERN_LIST.BLACKLISTED_TAGS.add(regex);
                break;
            case BLACKLISTED_TAGEVENTS:
                HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS.add(regex);
                break;
            case ATTRIB_CMD_WATCHLIST:
                HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST.add(regex);
                break;
        }
    }

    private static final Matcher getEngine(String regex, String input) {
        WJTXSSThreadLocal tl = ENGINES.get(regex);

        if (tl != null) {
            return tl.getMatcherEngine(input);
        } else {
            return null;
        }
    }

    /**
     * Call all blacklist strings validation
     *
     * @param dirtyString
     * @return string w/o blacklisted strings
     */
    public static final String sanitize(String dirtyString) {
        if (!StringUtil.isBlank(dirtyString)) {
            String targetStr = stripCTRLChars(dirtyString);
            targetStr = stripBlackListedTags(targetStr);
            targetStr = stripBlackListedTagEvents(targetStr);
            targetStr = stripWatchListStrOnEachTag(targetStr);
            return targetStr;
        } else {
            return dirtyString;
        }
    }

    /**
     * Strip off blacklisted tags
     *
     * @param input
     * @return string w/o blacklisted tags
     */
    public final static String stripBlackListedTags(String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }

        for (String bl : HTMLPATTERN_LIST.BLACKLISTED_TAGS.getList()) {
            input = input.replaceAll(bl, " ");
            /**
             * put space instead of blank to prevent other xss attack style *
             */
        }
        return input;
    }

    /**
     * Check if String has blacklisted tags
     *
     * @param input
     * @return true if found
     */
    public final static boolean hasBlackListedTags(String input) {
        if (StringUtil.isBlank(input)) {
            return false;
        }

        for (String bl : HTMLPATTERN_LIST.BLACKLISTED_TAGS.getList()) {
            if (getEngine(bl, input).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Strip off blacklisted tag events
     *
     * @param input
     * @return string w/o blacklisted tag events
     */
    public final static String stripBlackListedTagEvents(String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }

        for (String bl : HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS.getList()) {
            input = input.replaceAll(bl, " ");
            /**
             * put space instead of blank to prevent other xss attack style *
             */
        }
        return input;
    }

    /**
     * Check if String has blacklisted tag events
     *
     * @param input
     * @return true if found
     */
    public final static boolean hasBlackListedTagEvents(String input) {
        if (StringUtil.isBlank(input)) {
            return false;
        }

        for (String bl : HTMLPATTERN_LIST.BLACKLISTED_TAGEVENTS.getList()) {
            if (getEngine(bl, input).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Strip off blacklisted attrib cmd strings
     *
     * @param input
     * @return string w/o blacklisted attr cmd
     */
    public final static String stripWatchListStrOnEachTag(String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input);
        Matcher m = Pattern.compile("<[\\x20-\\x3B\\x3D\\x3F-\\x7E]*?>").matcher(input);
        int count = 0;
        while (m.find()) {
            String singleNodeTag = m.group();
            int origLen = singleNodeTag.length();
            /**
             * remove javascript comments lurking inside tags *
             */
            singleNodeTag = stripComments(singleNodeTag);
            /**
             * remove unicode chars lurking inside tags *
             */
            singleNodeTag = stripPeskyUnicodedChars(singleNodeTag);
            for (String bl : HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST.getList()) {
                singleNodeTag = singleNodeTag.replaceAll(bl, " ");
                /**
                 * put space instead of blank to prevent other xss attack style *
                 */
            }

            int start = m.start();
            int end = (start + origLen);
            if (end > sb.toString().length()) {
                end = start + singleNodeTag.length();
            }
            count = origLen - singleNodeTag.length();

            sb.replace(start + count, start + origLen, singleNodeTag);
        }

        return sb.toString();
    }

    /**
     * Check if String has blacklisted attrib cmd strings
     *
     * @param input
     * @return true if found
     */
    public final static boolean hasWatchListStrOnEachTag(String input) {
        if (StringUtil.isBlank(input)) {
            return false;
        }

        Matcher m = Pattern.compile("<[\\x20-\\x3B\\x3D\\x3F-\\x7E]*?>").matcher(input);
        while (m.find()) {
            String singleNodeTag = m.group();
            singleNodeTag = stripComments(singleNodeTag);
            singleNodeTag = stripPeskyUnicodedChars(singleNodeTag);
            for (String bl : HTMLPATTERN_LIST.ATTRIB_CMD_WATCHLIST.getList()) {
                if (getEngine(bl, singleNodeTag).find()) {
                    return true;
                }
            }
            if (hasBlackListedTagEvents(singleNodeTag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Strip off Unicode characters. These unicode chars can do harmful tricks when
     * applied inside tags
     *
     * @param input
     * @return string w/o Unicode chars
     */
    public final static String stripPeskyUnicodedChars(String input) {
        if (!StringUtil.isBlank(input)) {
            /**
             * remove - &#x[ALPHANUMERIC]  *
             */
            input = input.replaceAll("(&#([xX]?)[0-9a-zA-Z]{1,7})", " ");
            /**
             * put space instead of blank to prevent other xss attack style *
             */
            /**
             * remove - \\u[ALPHANUMERIC], %[ALPHANUMERIC]  *
             */
            input = input.replaceAll("([\\x25\\x5C][uU]?[0-9a-zA-Z]{1,7})", " ");
            /**
             * put space instead of blank to prevent other xss attack style *
             */
            return input;
        } else {
            return input;
        }
    }

    /**
     * Check if String has Unicoded Chars
     *
     * @param input
     * @return true if found
     */
    public final static boolean hasPeskyUnicodedChars(String input) {
        if (!StringUtil.isBlank(input)) {
            return Pattern.compile("(&#([xX]?)[0-9a-zA-Z]{1,7})").matcher(input).find();
        } else {
            return false;
        }
    }

    /**
     * Strip off Javascript Comments if found
     *
     * @param input
     * @return string w/o Javascript Comments
     */
    public final static String stripComments(String input) {
        if (!StringUtil.isBlank(input)) {
            return input.replaceAll("(/[\\*]{1,}([\\x20-\\x2E\\x30-\\x7E]*?)[\\*]{1,}/)", " ");
        } else {
            return input;
        }
    }

    /**
     * Strip off Control Characters
     *
     * @param input
     * @return string w/o ctrol chars
     */
    public final static String stripCTRLChars(String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }

        //input = StringEscapeUtils.unescapeHtml(input);
//		input = input.replaceAll("[\\x00-\\x1F\\x7F]", "");
        input = input.replaceAll("[\\x00-\\x09\\x0B-\\x1F\\x7F]", "");

        return input;
    }

    private XSSUtil() {
    }

    public static void main(String[] args) {
        logger.info(String.format("%b", hasWatchListStrOnEachTag("<p >url</p>")));
        logger.info(stripWatchListStrOnEachTag("<p style='url:'>u rl</p>"));
    }

    public final static String encodeltgt(String input) { //Used for tinymce EDIT
        if (StringUtil.isBlank(input)) {
            return input;
        }
        input = input.replaceAll("&gt;", "&amp;gt;").replaceAll("&lt;", "&amp;lt;");

        return input;
    }
}
