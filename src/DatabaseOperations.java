import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by joserran on 12/14/2015.
 */
public class DatabaseOperations
{
    public static void deleteUser(String userName)
    {
        DatabaseConnector connection = new DatabaseConnector();

        if(!connection.open())
        {
            System.out.println("was not able to establish a connection");
        }
        String query = "delete from users where username=?;";
        try
        {
            PreparedStatement preparedStatement = connection.conn.prepareStatement(query);
            preparedStatement.setString(1, userName);
            Integer rowsChanged = preparedStatement.executeUpdate();

            System.out.println(rowsChanged + " rows were changed in database.");
            connection.conn.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement pStatment;

                //executeUpdate()
    }
}
