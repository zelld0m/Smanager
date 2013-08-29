package com.search.properties.manager.model;

import com.google.common.base.Objects;
import java.util.List;

/**
 * Represents a group of a store in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class Group {

    private String name;
    private List<Member> members;

    public Group(String name, List<Member> groups) {
        this.name = name;
        this.members = groups;
    }

    public String getName() {
        return name;
    }

    public List<Member> getMember() {
        return members;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("name", name).
                add("members", members).
                toString();
    }
}
