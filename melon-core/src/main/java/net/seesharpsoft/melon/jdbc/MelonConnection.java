package net.seesharpsoft.melon.jdbc;

import lombok.Getter;
import net.seesharpsoft.commons.jdbc.ConnectionWrapper;
import net.seesharpsoft.melon.Constants;
import net.seesharpsoft.melon.Melon;
import net.seesharpsoft.melon.MelonFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class MelonConnection extends ConnectionWrapper {
    
    public static final String getConfigFilePath(String connectionUrl, Properties properties) {
        return properties.getOrDefault(Constants.PROPERTY_CONFIG_FILE, Constants.DEFAULT_CONFIG_FILE).toString();
    }

    public static final String getDelegateConnectionUrl(String connectionUrl, Properties properties) {
        return connectionUrl.replaceFirst("\\:melon\\:", ":");
    }

    public static final Properties getDelegateConnectionProperties(String connectionUrl, Properties properties) {
        Properties finalProperties = new Properties(properties);
        finalProperties.put("AUTOCOMMIT", "false");
        return finalProperties;
    }
    
    @Getter
    protected Melon melon;
    
    public MelonConnection(String url, Properties properties) throws SQLException, IOException {
        super(DriverManager.getConnection(getDelegateConnectionUrl(url, properties), getDelegateConnectionProperties(url, properties)));

        melon = MelonFactory.INSTANCE.getOrCreateMelon(url, properties);
        melon.syncToDatabase(this);
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