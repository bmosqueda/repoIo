package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Area;
import models.Author;
import models.Repository;

public class AuthorController extends Controller {
  public AuthorController() {
    super();
  }

  public Author[] getAll() throws ClassNotFoundException, SQLException {
    this.open();

    String sql = "SELECT "+Author.fields+" FROM authors AS a";

    PreparedStatement stament = this.connector.prepareStatement(sql);
    ResultSet resultSet = stament.executeQuery();
    ArrayList<Author> authors = new ArrayList<Author>();

    while (resultSet.next())
      authors.add(new Author(
            resultSet.getInt(1), 
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4)
          ));

    resultSet.close();
    this.close();

    return (Author[]) authors.toArray(new Author[authors.size()]);
  }

  public Author getById(int id) throws ClassNotFoundException, SQLException {
    this.open();

    String sql = "SELECT "+Author.fields+" FROM authors AS a WHERE a.author_id = " + id;

    PreparedStatement stament = this.connector.prepareStatement(sql);
    ResultSet resultSet = stament.executeQuery();
    Author author = null;

    if (resultSet.next())
      author = new Author(
          resultSet.getInt(1), 
          resultSet.getString(2),
          resultSet.getString(3),
          resultSet.getString(4)
        );

    resultSet.close();
    this.close();

    return author;
  }

  public boolean create(Author author) throws SQLException, ClassNotFoundException {
    String sql = "INSERT INTO authors (name, alias, country_of_birth) VALUES(?, ?, ?)";

    this.open();

    PreparedStatement stament = this.connector.prepareStatement(sql);
    stament.setString(1, author.getName());
    stament.setString(2, author.getAlias());
    stament.setString(3, author.getCountry_of_birth());

    stament.executeQuery();
    ResultSet generatedKeys = stament.getGeneratedKeys();

    boolean result = false;

    if (generatedKeys.next()) {
      author.setAuthor_id(generatedKeys.getInt(1));
      result = true;
    }

    generatedKeys.close();
    this.close();

    return result;
  }
  
  public Author[] getByResourceId(int id) throws ClassNotFoundException, SQLException {
	    this.open();

	    String sql = "SELECT a.* FROM authors_resource AS ar INNER JOIN authors AS a ON ar.author_id = a.author_id WHERE ar.resource_id = "+ id;

	    PreparedStatement stament = this.connector.prepareStatement(sql);
	    ResultSet resultSet = stament.executeQuery();
	    ArrayList<Author> authors = new ArrayList<Author>();

	    while (resultSet.next())
	      authors.add(new Author(resultSet.getInt("author_id"), resultSet.getString("name"), resultSet.getString("alias"), resultSet.getString("country_of_birth")));

	    resultSet.close();
	    this.close();

	    return (Author[]) authors.toArray(new Author[authors.size()]);
	  }
  
  public Author[] getAllByNameOrAlias(String name) throws ClassNotFoundException, SQLException {
	    this.open();	
	    name = this.escapeString(name);
	    String sql = "SELECT * FROM authors WHERE name LIKE '%"+name+"%' OR alias LIKE '%"+name+"%'";

	    PreparedStatement stament = this.connector.prepareStatement(sql);
	    ResultSet resultSet = stament.executeQuery();
	    ArrayList<Author> authors = new ArrayList<Author>();

	    while (resultSet.next())
	      authors.add(new Author(
	        resultSet.getInt(1), 
	        resultSet.getString(2),
	        resultSet.getString(3),
	        resultSet.getString(4)
	      ));

	    resultSet.close();
	    this.close();

	    return (Author[]) authors.toArray(new Author[authors.size()]);
	  }

  public boolean update(Author author) throws SQLException, ClassNotFoundException {
      String sql = "UPDATE authors SET name = '"+this.escapeString(author.getName())+"', alias = '"+this.escapeString(author.getAlias())+"', country_of_birth = '"+this.escapeString(author.getCountry_of_birth())+"' "
      		+ "WHERE author_id = "+author.getAuthor_id();

      this.open();

      PreparedStatement stament = this.connector.prepareStatement(sql);

      int rows = stament.executeUpdate(sql);

      stament.close();
      this.close();

      return rows > 0;
  }

  public boolean delete(Author author) throws SQLException, ClassNotFoundException {
      String sql = "DELETE FROM authors WHERE author_id = "+author.getAuthor_id();
      this.open();

      PreparedStatement stament = this.connector.prepareStatement(sql);

      int rows = stament.executeUpdate(sql);

      stament.close();
      this.close();

      return rows > 0;
  }

  public boolean hasChilds(Author author) throws SQLException, ClassNotFoundException {
      String sql = "SELECT COUNT(*) AS childs FROM authors_resource WHERE author_id = "+author.getAuthor_id();

      this.open();

      PreparedStatement stament = this.connector.prepareStatement(sql);
      ResultSet resultSet = stament.executeQuery();
      
      boolean result = false;
      if (resultSet.next())
          result = resultSet.getInt("childs") > 0;

      resultSet.close();
      this.close();

      return result;
  }
}