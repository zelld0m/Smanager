package com.search.manager.core.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import java.util.UUID;

/**
 *
 * @author Philip Mark Gutierrez
 * @since September 29, 2013
 * @version 1.0
 */
public class DAOUtils2 {

    /**
     * <b>
     * Generates a unique address based from the ethernet address and the date and time
     * </b>
     *
     * See:
     * <ul>
     * <li>{@link EthernetAddress}</li>
     * <li>{@link Generators} timeBasedGenerator()</li>
     * </ul>
     *
     * @return
     */
    public static String generateUniqueId() {
        EthernetAddress nic = EthernetAddress.fromInterface();
        TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);
        UUID uuid = uuidGenerator.generate();
        return uuid.toString();
    }
}
