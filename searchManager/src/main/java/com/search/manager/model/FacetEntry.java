package com.search.manager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.SortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacetEntry {

    private static final Logger logger =
            LoggerFactory.getLogger(FacetEntry.class);
    
    private String label;
    private long count;

    public FacetEntry(String label, long count) {
        this.label = label;
        this.count = count;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void addCount(long count) {
        this.count += count;
    }

    public long getCount() {
        return count;
    }

    public static void sortEntries(List<FacetEntry> entries, final SortType sortType, List<String> elevatedValues) {

        Map<String, FacetEntry> map = new HashMap<String, FacetEntry>();
        for (FacetEntry entry : entries) {
            map.put(entry.getLabel(), entry);
        }

        if (CollectionUtils.isNotEmpty(elevatedValues)) {
            for (String elevatedValue : elevatedValues) {
                FacetEntry entry = map.get(elevatedValue);
                if (entry != null) {
                    entries.remove(entry);
                }
            }
        }

        Collections.sort(entries, new Comparator<FacetEntry>() {
            @Override
            public int compare(FacetEntry entry1, FacetEntry entry2) {
                long val = 0;
                if (sortType == SortType.ASC_ALPHABETICALLY) {
                    val = entry1.getLabel().compareToIgnoreCase(entry2.getLabel());
                    if (val == 0) {
                        val = entry1.getLabel().compareTo(entry2.getLabel());
                    }
                } else if (sortType == SortType.DESC_ALPHABETICALLY) {
                    val = entry2.getLabel().compareToIgnoreCase(entry1.getLabel());
                    if (val == 0) {
                        val = entry2.getLabel().compareTo(entry1.getLabel());
                    }
                } else if (sortType == SortType.ASC_COUNT) {
                    val = entry1.getCount() - entry2.getCount();
                    if (val == 0) {
                        val = entry1.getLabel().compareToIgnoreCase(entry2.getLabel());
                        if (val == 0) {
                            val = entry1.getLabel().compareTo(entry2.getLabel());
                        }
                    }
                } else if (sortType == SortType.DESC_COUNT) {
                    val = entry2.getCount() - entry1.getCount();
                    if (val == 0) {
                        val = entry1.getLabel().compareToIgnoreCase(entry2.getLabel());
                        if (val == 0) {
                            val = entry1.getLabel().compareTo(entry2.getLabel());
                        }
                    }
                }
                return ((val < 0) ? -1 : ((val > 0) ? 1 : 0));
            }
        });

        if (CollectionUtils.isNotEmpty(elevatedValues)) {
            int i = 0;
            for (String elevatedValue : elevatedValues) {
                FacetEntry entry = map.get(elevatedValue);
                if (entry != null) {
                    entries.add(i++, entry);
                }
            }
        }

    }

    private static String getFirstLevelCategory(String facetTemplate) {
        String category = "";
        if (StringUtils.isNotEmpty(facetTemplate)) {
            if (StringUtils.contains(facetTemplate, " |")) {
                category = facetTemplate.substring(0, facetTemplate.indexOf(" |"));
            } else {
                category = facetTemplate;
            }
        }
        return category;
    }

    /**
     * For Facet Template Name facets. Special processing.
     */
    public static void sortFacetTemplateEntries(List<FacetEntry> entries, final SortType sortType, List<String> elevatedValues) {
        // collate values
        Map<String, FacetEntry> catMap = new HashMap<String, FacetEntry>();
        for (FacetEntry entry : entries) {
            String key = getFirstLevelCategory(entry.getLabel());
            FacetEntry catMapEntry = catMap.get(key);
            if (catMapEntry == null) {
                catMapEntry = new FacetEntry(key, 0);
                catMap.put(key, catMapEntry);
            }
            catMapEntry.addCount(entry.getCount());
        }

        // sort 1st level by count
        final List<FacetEntry> mapEntries = new ArrayList<FacetEntry>();
        mapEntries.addAll(catMap.values());
        sortEntries(mapEntries, sortType, elevatedValues);

        final List<String> sortedMapEntries = new ArrayList<String>();
        for (FacetEntry entry : mapEntries) {
            sortedMapEntries.add(entry.getLabel());
        }

        // sort entries
        Collections.sort(entries, new Comparator<FacetEntry>() {
            @Override
            public int compare(FacetEntry entry1, FacetEntry entry2) {
                int val = 0;
                int val1 = sortedMapEntries.indexOf(getFirstLevelCategory(entry1.getLabel()));
                int val2 = sortedMapEntries.indexOf(getFirstLevelCategory(entry2.getLabel()));
                if (val1 == val2) {
                    val = entry1.getLabel().compareToIgnoreCase(entry2.getLabel());
                    if (val == 0) {
                        val = entry1.getLabel().compareTo(entry2.getLabel());
                    }
                } else {
                    val = val1 - val2;
                }
                return val;
            }
        });

    }

    public static void main(String[] args) {
        List<String> elevatedValues = new ArrayList<String>();
        elevatedValues.add("Software");
        elevatedValues.add("Office Equipment & Supplies");
        elevatedValues.add("Services");

        List<FacetEntry> entries = new ArrayList<FacetEntry>();
        entries.add(new FacetEntry("Office Equipment & Supplies | Books | Hardware How-To Books", 2062));
        entries.add(new FacetEntry("Software | Backup/Archive/Storage Software | Data Archive Software", 85));
        entries.add(new FacetEntry("Services | 3rd Party Delivered Services", 6));
        entries.add(new FacetEntry("Services | 3rd Party Delivered Services | Help Desk", 3));
        entries.add(new FacetEntry("Computers", 2));
        entries.add(new FacetEntry("Computers | All-In-One Computers", 700));
        entries.add(new FacetEntry("Electronics | Tablets & Accessories | Tablet Cases/Covers", 494));
        entries.add(new FacetEntry("Electronics | Cell/Smart Phones & Accessories | Cell/Smart Phone Cases & Holsters", 398));
        entries.add(new FacetEntry("", 64));
        entries.add(new FacetEntry("Office Equipment & Supplies | Books", 593));
        entries.add(new FacetEntry("Electronics | MP3 Players & Accessories | MP3 Armbands/Cases/Covers", 390));
        entries.add(new FacetEntry("Office Equipment & Supplies | Books | Hardware How-To Books", 21));
        entries.add(new FacetEntry("Computer Accessories", 181));
        entries.add(new FacetEntry("Electronics | MP3 Players & Accessories | MP3 Power Adapters/Chargers", 172));
        entries.add(new FacetEntry("Computer Accessories | Notebook Accessories | Notebook Batteries", 124));
        entries.add(new FacetEntry("Electronics | Cell/Smart Phones & Accessories | Cell/Smart Phone Accessories", 123));
        entries.add(new FacetEntry("Computer Accessories | Notebook Carrying Cases & Accessories | Sleeve/Shuttle Notebook Cases", 105));
        entries.add(new FacetEntry("Electronics | Video Camcorders & Accessories | Camcorder Accessories", 89));
        entries.add(new FacetEntry("Data Storage", 1));

//		sortEntries(entries, SortType.ASC_ALPHABETICALLY, elevatedValues);
//		for (FacetEntry entry: entries) {
//			System.out.println(entry.getLabel() + ": " + entry.getCount());
//		}
//		System.out.println();
//
//		sortEntries(entries, SortType.DESC_ALPHABETICALLY, elevatedValues);
//		for (FacetEntry entry: entries) {
//			System.out.println(entry.getLabel() + ": " + entry.getCount());
//		}
//		System.out.println();
//
//		sortEntries(entries, SortType.ASC_COUNT, elevatedValues);
//		for (FacetEntry entry: entries) {
//			System.out.println(entry.getLabel() + ": " + entry.getCount());
//		}
//		System.out.println();
//
//		sortEntries(entries, SortType.DESC_COUNT, elevatedValues);
//		for (FacetEntry entry: entries) {
//			System.out.println(entry.getLabel() + ": " + entry.getCount());
//		}
//		System.out.println();

        sortFacetTemplateEntries(entries, SortType.ASC_ALPHABETICALLY, elevatedValues);
        for (FacetEntry entry : entries) {
            logger.info(String.format("%s: %i", entry.getLabel(), entry.getCount()));
        }
        logger.info(String.format("%n"));

        sortFacetTemplateEntries(entries, SortType.DESC_ALPHABETICALLY, elevatedValues);
        for (FacetEntry entry : entries) {
            logger.info(String.format("%s: %i", entry.getLabel(), entry.getCount()));
        }
        logger.info(String.format("%n"));

        sortFacetTemplateEntries(entries, SortType.ASC_COUNT, elevatedValues);
        for (FacetEntry entry : entries) {
            logger.info(String.format("%s: %i", entry.getLabel(), entry.getCount()));
        }
        logger.info(String.format("%n"));

        sortFacetTemplateEntries(entries, SortType.DESC_COUNT, elevatedValues);
        for (FacetEntry entry : entries) {
            logger.info(String.format("%s: %i", entry.getLabel(), entry.getCount()));
        }
        logger.info(String.format("%n"));
    }
}
