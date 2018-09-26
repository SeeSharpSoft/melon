package net.seesharpsoft.melon.config;

import net.seesharpsoft.commons.collection.Properties;
import net.seesharpsoft.melon.*;

import java.io.File;
import java.io.IOException;

public class StorageConfig extends ConfigBase {
    public String uri;

    public Storage getStorage(Table table, Properties additionalProperties) {
        Properties finalProperties = getProperties(additionalProperties);
        try {
            if (uri != null && !uri.isEmpty()) {
                File targetFile = MelonHelper.getFile(uri, ((File) additionalProperties.get(Constants.PROPERTY_CONFIG_FILE)).getParentFile().getAbsolutePath());

                return StorageFactory.INSTANCE.createStorageFor(table, finalProperties, targetFile);
            }
            return StorageFactory.INSTANCE.createStorageFor(table, finalProperties, null);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }
}
