import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

    private static int markUserUnavailable(String loginN) // used in chat client
    {
        DatabaseConnector connection = new DatabaseConnector();
        Integer id = -1;
        if(!connection.open())
            System.out.println("unable to connect");

        ResultSet resultSet = null;
        try
        {

            String query = "select id from users where username=?";
            PreparedStatement pStatement = connection.conn.prepareStatement(query);
            pStatement.setString(1, loginN);
            ResultSet rs = pStatement.executeQuery();

            if(rs.next())
            {
                id = rs.getInt(1);
            }
            connection.conn.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    protected static void executeAvailability()  {
        DatabaseConnector connection = new DatabaseConnector();
        if(!connection.open())
            System.out.println("unable to connect");

        ResultSet resultSet = null;
        try
        {
            resultSet = connection.executeQuery("select * from users;");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next())
            {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.println("");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        connection.close();
    }
}
