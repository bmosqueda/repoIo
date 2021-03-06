package apis;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import controllers.SchoolController;
import models.School;

//Sets the path to base URL + /hello
@Path("/schools")
public class SchoolAPI {
	private SchoolController schoolController = new SchoolController();
	private JSONParser parser = new JSONParser();
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll(@Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		try {
			School schools[];
			schools = this.schoolController.getAll();

			return this.schoolController.arrayToJSON(schools);
		} catch (SQLException e) {
			return Response.getJSONError("Error al busca la Escuela", 500, res);
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getById(@PathParam("id") int id, @Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException, SQLException {

		School school = this.schoolController.getById(id);

		if (school == null)
			return Response.getJSONError("La escuela con el id " + id + " no encontrado", 404, res);

		return school.toJSON();
	}
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@Context HttpServletRequest req, String body, @Context HttpServletResponse res)
            throws ClassNotFoundException {
        HttpSession session = req.getSession(false);

        if (session != null)
            if (session.getAttribute("email") == null)
                return Response.getJSONError("Necesitas iniciar sesión", 400, res);

        JSONObject json;
        
        try {
			json = (JSONObject) this.parser.parse(body);
		} catch (org.json.simple.parser.ParseException e) {
			return Response.getJSONError("Los parámetros deben de venir en formato JSON", 400, res);
		}

        Object name = json.get("name");

        if (name == null)
            return Response.getJSONError("Parámetros incompletos para registrar escuela", 400, res);

        School school = new School(name.toString());

        try {
            if (this.schoolController.create(school))
                return school.toJSON();
            else
                return Response.getJSONError("No se pudo crear la escuela", 400, res);
        } catch (SQLException e) {
            return Response.getJSONError(e.getMessage(), 400, res);
        }
    }

  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String update(@PathParam("id") int id, @Context HttpServletRequest req, String body, @Context HttpServletResponse res)
      throws ClassNotFoundException {
    HttpSession session = req.getSession(false);

    if (session != null)
    {
      if (session.getAttribute("email") == null)
        return Response.getJSONError("Necesitas iniciar sesión", 400, res);
    }
    else
      return Response.getJSONError("Necesitas iniciar sesión", 400, res);
    
    if(Integer.parseInt(session.getAttribute("role_id").toString()) != 1)
      return Response.getJSONError("Sólo administradores", 403, res);
    
    School school = null;
    try {
      school = this.schoolController.getById(id);
    } catch (SQLException e1) {
      System.out.println(e1.getMessage());
      return Response.getJSONError("Problema al buscar la categoría", 404, res);
    }

    if (school == null)
      return Response.getJSONError("La escuela con el id " + id + " no encontrado", 404, res);

    JSONObject json;

    try {
      json = (JSONObject) this.parser.parse(body);
    } catch (org.json.simple.parser.ParseException e) {
      return Response.getJSONError("Los parámetros deben de venir en formato JSON", 400, res);
    }

    Object name = json.get("name");

    if (name == null)
      return Response.getJSONError("Parámetros incompletos para registrar la escuela", 400, res);

    school.setName(name.toString());

    try {
      if (this.schoolController.update(school))
        return school.toJSON();
      else
        return Response.getJSONError("No se pudo crear la categoría", 400, res);
    } catch (SQLException e) {
      return Response.getJSONError(e.getMessage(), 400, res);
    }
  }

  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String delete(@PathParam("id") int id, @Context HttpServletRequest req, @Context HttpServletResponse res)
      throws ClassNotFoundException, SQLException {
    HttpSession session = req.getSession(false);
    
    if (session != null)
    {
      if (session.getAttribute("email") == null)
        return Response.getJSONError("Necesitas iniciar sesión", 400, res);
    }
    else
      return Response.getJSONError("Necesitas iniciar sesión", 400, res);
    
    if(Integer.parseInt(session.getAttribute("role_id").toString()) != 1)
      return Response.getJSONError("Sólo administradores", 403, res);

    School school = this.schoolController.getById(id);

    if (school == null)
      return Response.getJSONError("La escuela con el id " + id + " no encontrado", 404, res);
    
    try {
        if(this.schoolController.hasChilds(school.getSchool_id()))
          return Response.getJSONError("No se puede eliminar el área porque hay que recursos registrados que usan este elemento", 400, res);
        
      if (this.schoolController.delete(school))
        return school.toJSON();
      else
        return Response.getJSONError("No se pudo eliminar la escuela", 400, res);
    } catch (SQLException e) {
      return Response.getJSONError(e.getMessage(), 400, res);
    }
  }
}