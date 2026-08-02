import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class TestSQLSyntax {
    public static void main(String[] args) {
        // We will just print out the query to inspect if it's generally considered invalid.
        // Actually, SQL Server does NOT allow alias.column_name in the SET clause of an UPDATE statement.
        // Example: UPDATE i SET i.total_amount = ... is INVALID. It must be UPDATE i SET total_amount = ...
        System.out.println("SQL Server does not allow alias prefix in SET clause (e.g., SET i.total_amount).");
    }
}
