package albumstore;

import java.io.InputStream;
import java.sql.*;
import org.apache.commons.dbcp2.*;

public class AlbumDao {
  private static final String ARTIST = "artist";
  private static final String TITLE = "title";
  private static final String YEAR = "year";

  private static BasicDataSource dataSource;

  public AlbumDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  public AlbumInfo getAlbumById(int albumID) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    AlbumInfo albumInfo = null;
    String selectQueryStatement = "SELECT artist, title, year FROM album WHERE albumID = ?";

    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(selectQueryStatement);
      preparedStatement.setInt(1, albumID);
      resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        albumInfo = new AlbumInfo();
        albumInfo.setArtist(resultSet.getString(ARTIST));
        albumInfo.setTitle(resultSet.getString(TITLE));
        albumInfo.setYear(resultSet.getString(YEAR));
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return albumInfo;
  }

  public int createAlbum(AlbumInfo newAlbum, InputStream imageInputStream) {
    int albumID = 0;
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO album (artist, title, year, image) VALUES (?,?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement, Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1, newAlbum.getArtist());
      preparedStatement.setString(2, newAlbum.getTitle());
      preparedStatement.setString(3, newAlbum.getYear());
      preparedStatement.setBinaryStream(4, imageInputStream);

      preparedStatement.executeUpdate();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

      if (generatedKeys.next()) {
        albumID = generatedKeys.getInt(1);
      }
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
    return albumID;
  }
}
