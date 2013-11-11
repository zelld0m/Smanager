package com.search.properties.manager.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Module;
import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 7, 2013
 * @version 1.0
 */
public class Groups {

    /**
     * Checks whether a group exists in a module
     *
     * @param name the name of the group
     * @param module the module to look into
     * @return <pre>true</pre> if the group exists else <pre>false</pre>
     */
    public static boolean containsGroup(String name, Module module) {
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            String groupName = group.getName();
            if (!Strings.isNullOrEmpty(groupName) && groupName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether at least one group in a module has no name
     *
     * @param module the module to look into
     * @return <pre>true</pre> if at least one group has no name else <pre>false</pre>
     */
    public static boolean containsAGroupWithoutAName(Module module) {
        List<Group> groupsWithoutAName = Lists.newArrayList();
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            if (Strings.isNullOrEmpty(group.getName())) {
                groupsWithoutAName.add(group);
                break;
            }
        }

        return !groupsWithoutAName.isEmpty();
    }

    /**
     * Searches for a group by name
     *
     * @param name the name of the group
     * @param module the {@link Module} object
     * @return the {@link Group} object with the matching name
     * @throws PropertyException thrown when the group with the passed name does not
     * exists
     */
    public static Group getGroupByName(String name, Module module)
            throws PropertyException {
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            String groupName = group.getName();

            if (!Strings.isNullOrEmpty(groupName) && groupName.equals(name)) {
                return group;
            }
        }

        throw new PropertyException(String.format(
                "Group with the name %s cannot be found in the module %s",
                name, module.getName()));
    }

    /**
     * <p>
     * Returns all the groups in the specified module without a name
     * </p>
     *
     * <p>
     * If no group without a name if found, an empty {@link java.util.ArrayList} is
     * returned
     * </p>
     *
     * @param module the {@link Module} object
     * @return all the groups in the specified module without a name
     */
    public static List<Group> getAllGroupsWithoutAName(Module module) {
        List<Group> toReturn = Lists.newArrayList();

        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            if (Strings.isNullOrEmpty(group.getName())) {
                toReturn.add(group);
            }
        }

        return toReturn;
    }
}
