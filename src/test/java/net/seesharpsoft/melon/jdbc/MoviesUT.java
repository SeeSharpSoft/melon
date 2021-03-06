package net.seesharpsoft.melon.jdbc;

import net.seesharpsoft.melon.test.TestFixture;
import org.junit.Test;

import java.sql.SQLException;

public class MoviesUT extends TestFixture {
    @Override
    public String[] getResourceFiles() {
        return new String[] {
                "/schemas/Movies.yaml",
                "/data/movies.xml",
                "/data/movies_title.properties",
                "/data/my_movies.csv"
        };
    }

    @Test
    public void should_insert_new_entry_in_empty_file() throws SQLException {
        try (MelonConnection connection = getConnection("/schemas/Movies.yaml")) {
            connection.prepareStatement("INSERT INTO \"Movie\" (\"Name\") VALUES ('Test')").execute();
            connection.commit();
        }
    }

}
