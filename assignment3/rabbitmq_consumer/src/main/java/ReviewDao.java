import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class ReviewDao {

  private static final String LIKE = "like";
  private static final String DISLIKE = "dislike";

  private static BasicDataSource dataSource;

  public ReviewDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  public void createReviewById(String likeOrNot, int albumID) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String updateQueryStatement;
    if (likeOrNot == LIKE) {
      updateQueryStatement = "UPDATE album SET like = like + 1 WHERE albumID = ?";
    } else {
      updateQueryStatement = "UPDATE album SET dislike = dislike + 1 WHERE albumID = ?";
    }
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(updateQueryStatement);
      preparedStatement.setInt(1, albumID);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();

    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

}
