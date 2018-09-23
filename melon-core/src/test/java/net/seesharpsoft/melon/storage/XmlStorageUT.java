package net.seesharpsoft.melon.storage;

import net.seesharpsoft.commons.collection.Properties;
import net.seesharpsoft.melon.Constants;
import net.seesharpsoft.melon.MelonHelper;
import net.seesharpsoft.melon.Schema;
import net.seesharpsoft.melon.impl.ColumnImpl;
import net.seesharpsoft.melon.impl.SchemaImpl;
import net.seesharpsoft.melon.impl.TableImpl;
import net.seesharpsoft.melon.jdbc.MelonDriver;
import net.seesharpsoft.melon.test.TestFixture;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XmlStorageUT extends TestFixture {

    public static void assertAddressData(Connection connection) throws SQLException {
        try(ResultSet rs = connection.prepareStatement("SELECT * FROM Address ORDER BY ID").executeQuery()) {
            rs.next();
            assertThat(rs.getString("id"), is("1"));
            assertThat(rs.getString("city"), is("Las Vegas"));
            assertThat(rs.getString("country"), is("US"));
            rs.next();
            assertThat(rs.getString("id"), is("2"));
            assertThat(rs.getString("city"), is("Mexico City"));
            assertThat(rs.getString("country"), is("Mexico"));

            assertThat(rs.next(), is(false));
        }
    }

    @Override
    public String[] getResourceFiles() {
        return new String[] {
                "/data/Address.xml",
                "/files/Address_New.xml",
                "/files/Address_Test.xml",
                "/data/AddressAttributes.xml",
                "/XmlAttributes.yaml"
        };
    }

    @Test
    public void should_parse_address_xml() throws IOException {
        Schema schema = new SchemaImpl("dummy", new Properties());
        TableImpl table = new TableImpl(schema, "Address", new Properties());
        ColumnImpl column = new ColumnImpl(table, "id", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "city", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "country", new Properties());
        table.addColumn(column);
        Properties properties = new Properties();
        properties.put(XmlStorage.PROPERTY_ROOT, "/address/data_record");
        XmlStorage storage = new XmlStorage(table, properties, MelonHelper.getFile("/data/Address.xml"));

        List<List<String>> data = storage.read();

        assertThat(data.size(), is(2));
    }

    @Test
    public void should_write_address_xml() throws IOException {
        Schema schema = new SchemaImpl("dummy", new Properties());
        TableImpl table = new TableImpl(schema, "Address", new Properties());
        ColumnImpl column = new ColumnImpl(table, "id", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "city", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "country", new Properties());
        table.addColumn(column);
        Properties properties = new Properties();
        properties.put(XmlStorage.PROPERTY_ROOT, "/address/data_record");
        XmlStorage storage = new XmlStorage(table, properties, MelonHelper.getFile("/files/Address_New.xml"));

        List<List<String>> data = storage.read(MelonHelper.getFile("/data/Address.xml"), table, properties);

        storage.write(MelonHelper.getFile("/files/Address_New.xml"), table, properties, data);

        data = storage.read(MelonHelper.getFile("/files/Address_New.xml"), table, properties);

        assertThat(data.size(), is(2));
    }

    @Test
    public void should_repeatedly_read_and_write_address_xml_properly() throws IOException {
        Schema schema = new SchemaImpl("dummy", new Properties());
        TableImpl table = new TableImpl(schema, "Address", new Properties());
        ColumnImpl column = new ColumnImpl(table, "id", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "city", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "country", new Properties());
        table.addColumn(column);
        column = new ColumnImpl(table, "userId", new Properties());
        table.addColumn(column);
        Properties properties = new Properties();
        properties.put(XmlStorage.PROPERTY_ROOT, "/address/data_record");
        XmlStorage storage = new XmlStorage(table, properties, MelonHelper.getFile("/files/Address_Test.xml"));

        List<List<String>> data = storage.read(MelonHelper.getFile("/files/Address_Test.xml"), table, properties);
        assertThat(data.size(), is(2));

        storage.write(MelonHelper.getFile("/files/Address_Test.xml"), table, properties, data);

        List<List<String>> newData = storage.read(MelonHelper.getFile("/files/Address_Test.xml"), table, properties);

        assertThat(newData.size(), is(2));
        assertThat(newData, is(data));
    }

    @Test
    public void should_connect_to_h2_mem() throws SQLException {
        java.util.Properties properties = new java.util.Properties();
        properties.put(Constants.PROPERTY_CONFIG_FILE, "/XmlAttributes.yaml");
        try (Connection connection = DriverManager.getConnection(String.format("%sh2:mem:memdb", MelonDriver.MELON_URL_PREFIX), properties)) {
            assertAddressData(connection);
        }
    }
}
