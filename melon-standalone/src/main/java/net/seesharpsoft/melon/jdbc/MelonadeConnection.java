package net.seesharpsoft.melon.jdbc;

import net.seesharpsoft.melon.MelonFactory;
import org.h2.engine.Constants;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

public class MelonadeConnection extends MelonConnection {

    public static final String PROPERTY_H2_DB_ID = "databaseId";
    
    public static final String getStandaloneConfigFilePath(String connectionUrl, Properties properties) {
        String configUrl = connectionUrl.replaceFirst(Pattern.quote(MelonadeDriver.MELON_STANDALONE_URL_PREFIX), "");
        if (configUrl.startsWith(":")) {
            configUrl = configUrl.substring(1);
        }
        if (configUrl.isEmpty()) {
            return MelonConnection.getConfigFilePath(connectionUrl, properties);
        }
        return configUrl;
    }
    
    private static String getMelonadeUrl(String url, Properties properties) {
        String configFile = getStandaloneConfigFilePath(url, properties);
        File file = new File(configFile);
        return String.format("%smem:%s", Constants.START_URL, properties.getOrDefault(PROPERTY_H2_DB_ID, file.getName()).toString());
    }

    private static Properties getMelonadeProperties(String url, Properties properties) {
        String configFile = getStandaloneConfigFilePath(url, properties);
        Properties finalProperties = new Properties(properties);
        finalProperties.put(net.seesharpsoft.melon.Constants.PROPERTY_CONFIG_FILE, configFile);
        return finalProperties;
    }
    
    public MelonadeConnection(String url, Properties properties) throws SQLException, IOException {
        super(getMelonadeUrl(url, properties), getMelonadeProperties(url, properties));
    }
    
    @Override
    public synchronized void close() throws SQLException {
        super.close();

        MelonFactory.INSTANCE.remove(melon);
    }
    
    @Override
    public synchronized void commit() throws SQLException {
        super.commit();

        this.melon.syncToStorage(this);
    }

    @Override
    public synchronized void rollback() throws SQLException {
        super.rollback();

        this.melon.syncToDatabase(this);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        super.rollback(savepoint);

        this.melon.syncToDatabase(this);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new MelonPreparedStatement(melon, super.createStatement());
    }

    @Override
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency) throws SQLException {
        return new MelonPreparedStatement(melon, super.createStatement(resultSetType, resultSetConcurrency));
    }

    @Override
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return new MelonPreparedStatement(melon, super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }
    
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency) throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql, resultSetType, resultSetConcurrency));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql, autoGeneratedKeys));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql, columnIndexes));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return new MelonPreparedStatement(melon, super.prepareStatement(sql, columnNames));
    }
}