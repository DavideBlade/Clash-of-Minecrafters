package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A class that implements this interface wants to provide a converter of the rows of a {@link ResultSet}. Once a
 * result set is obtained from a query, the {@link #process(ResultSet)} method is invoked. This method takes care
 * of converting the data in the {@link ResultSet} to an object (for example, a {@link List}) that contains all the
 * necessary information.
 *
 * @param <R> The type of object into which to convert the result set
 *
 * @author DavideBlade
 * @see QueryBuilder
 * @see QueryBuilderBase
 * @since 3.1
 */
public interface ResultSetConverter<R> {

    /**
     * Converts the non-empty result set to another object
     *
     * @param resultSet Result set to convert whose cursor already points to the first element
     *
     * @return An object that encapsulates the information obtained from the result set
     *
     * @throws SQLException If an error occurs while accessing the result set
     */
    R process(@Nonnull final ResultSet resultSet) throws SQLException;
}
