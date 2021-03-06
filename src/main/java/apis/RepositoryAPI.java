package apis;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import controllers.CategoryController;
import controllers.KeywordController;
import controllers.RepositoryController;
import controllers.ResourceController;
import models.Repository;
import models.Resource;
import models.Category;
import models.Keyword;

//Sets the path to base URL + /hello
@Path("/repositories")
public class RepositoryAPI {
	private RepositoryController repositoryController = new RepositoryController();
	private JSONParser parser = new JSONParser();

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll(@Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		try {
			Repository repositories[];
			repositories = this.repositoryController.getAll();
			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}
	
	@GET
	@Path("/newest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNewest(@Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		Object temp = req.getParameter("lastId");
		int lastId = 0;
		if(temp != null && this.repositoryController.isInt(temp.toString()))
			lastId = Integer.parseInt(temp.toString());
		System.out.println(lastId);
		try {
			Repository repositories[];
			
			repositories = this.repositoryController.getNewest(lastId);

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}

	@GET
	@Path("/creator/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByCreator(@PathParam("id") int id, @Context HttpServletRequest req,
			@Context HttpServletResponse res) throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);

		try {
			Repository repositories[];
			repositories = this.repositoryController.getAllByCreator(Integer.parseInt(session.getAttribute("user_id").toString()));

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}

	@GET
	@Path("/category/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByCategory(@PathParam("id") int id, @Context HttpServletRequest req,
			@Context HttpServletResponse res) throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);
		try {
			Repository repositories[];
			repositories = this.repositoryController.getAllByCategory(id);

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}

	@GET
	@Path("/area/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByArea(@PathParam("id") int id, @Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);
		try {
			Repository repositories[];
			repositories = this.repositoryController.getAllByArea(id);

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}

	@POST
	@Path("/keyword")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByKeyword(@Context HttpServletRequest req, String body, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
		{
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);
		}
		else
			return Response.getJSONError("Necesitas iniciar sesión", 400, res);
		
		JSONObject json;

		try {
			json = (JSONObject) this.parser.parse(body);
		} catch (org.json.simple.parser.ParseException e) {
			return Response.getJSONError("Los parámetros deben de venir en formato JSON", 400, res);
		}

		Object keywords = json.get("keywords");

		if (keywords == null)
			return Response.getJSONError("Parámetros incompletos para buscar por tags", 400, res);

		try {
			Repository repositories[];
			repositories = this.repositoryController.getAllByKeywords(keywords.toString());

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}
	
	@GET
	@Path("/title")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByTitle(@Context HttpServletRequest req, String body, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);

		String title = req.getParameter("title");
		
		if (title == null || title.equals(""))
			return Response.getJSONError("Parámetros incompletos para buscar por título", 400, res);

		try {
			Repository repositories[];
			repositories = this.repositoryController.getAllByTitle(title);

			return this.repositoryController.arrayToJSON(repositories);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 500, res);
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getById(@PathParam("id") int id, @Context HttpServletRequest req, @Context HttpServletResponse res)
			throws ClassNotFoundException, SQLException {

		Repository repository = this.repositoryController.getById(id);

		if (repository == null)
			return Response.getJSONError("Repositorio con el id " + id + " no encontrado", 404, res);
		
		//Repository to JSON
		JSONObject json = repository.toJSONObject();
		
		//Agregar sus categorías
		CategoryController catController = new CategoryController();
		Category categories[] = catController.getAllByRepositoryId(id);

		json.put("categories", CategoryController.arrayToJSONArray(categories));

		if (req.getParameter("withoutResources") != null)
			return json.toString();
		
		//Agregar sus recursos
		ResourceController resController = new ResourceController();
		JSONArray resources = resController.getAllByRepositoryIdJSON(id);

		json.put("resources", resources);
				
		return json.toString();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String create(@Context HttpServletRequest req, String body, @Context HttpServletResponse res)
			throws ClassNotFoundException {
		HttpSession session = req.getSession(false);

		if (session != null)
		{
			if (session.getAttribute("email") == null)
				return Response.getJSONError("Necesitas iniciar sesión", 400, res);
		}
		else
			return Response.getJSONError("Necesitas iniciar sesión", 400, res);

		JSONObject json;
		System.out.println(body);

		try {
			json = (JSONObject) this.parser.parse(body);
		} catch (org.json.simple.parser.ParseException e) {
			return Response.getJSONError("Los parámetros deben de venir en formato JSON", 400, res);
		}

		Object name = json.get("name");
		Object description = json.get("description");
		Object url = json.get("url");
		Object tags = json.get("tags");
		
		if (url == null || description == null || name == null || tags == null)
			return Response.getJSONError("Parámetros incompletos para registrar escuela", 400, res);

		Object creator_id = json.get("creator_id");
		Repository repository = new Repository(Integer.parseInt(session.getAttribute("user_id").toString()),
				name.toString(), description.toString(), url.toString(), tags.toString());

		try {
			if (!this.repositoryController.create(repository))
				return Response.getJSONError("No se pudo crear la categoría", 400, res);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 400, res);
		}
		
		//Insertar los tags
		KeywordController keyController = new KeywordController();
		String keys[] = repository.getTags().split(",");
		for(String key : keys)
		{
			Keyword tempKey = new Keyword(KeywordController.keywordFormat(key.trim()));
			try {
				int key_id = keyController.getIdByKey(tempKey);
				if(key_id == -1)
				{
					keyController.create(tempKey);
					key_id = tempKey.getKeyword_id();
				}
				
				repositoryController.insertKeywordRepository(repository.getRepository_id(), key_id);
					
			} catch (SQLException e) {
				System.out.println("Problema al buscar o insertar keyword");
				System.out.println(e.getMessage());
			}
		}
		
		//Insertar sus categorías
		JSONArray categories = (JSONArray)json.get("categories");
		
		if(categories == null)
			return repository.toJSON();
		
		int categoriesCount = categories.size();
		for(int i = 0; i < categoriesCount; i++)
		{
			Object catId = categories.get(i);

			if(catId == null)
				continue;
			else if(!this.repositoryController.isInt(catId.toString()))
				continue;
			
			try {
				this.repositoryController.insertCategoryRepository(repository.getRepository_id(), Integer.parseInt(catId.toString()));
			} catch (SQLException e) {
				System.out.println("Hubo un problem al guardar la categoría: " + catId.toString());
				System.out.println(e.getMessage());
			}
		}
		
		
		//Insertar recursos
		JSONArray resources = (JSONArray)json.get("resources");

		if(resources== null)
			return repository.toJSON();

		int resourcesCount = resources.size();
		for(int i = 0; i < resourcesCount; i++)
		{
			ResourceController resController = new ResourceController();
			
			JSONObject resTemp = (JSONObject) resources.get(i);
			
			Object title = resTemp.get("title");
			Object resDescription = resTemp.get("description");
			Object size = resTemp.get("size");
			Object type = resTemp.get("type");
			Object urlRes = resTemp.get("url");
			System.out.println(resTemp.toJSONString());
			if(title == null || resDescription == null || size == null || type == null || urlRes == null)
				continue;
			else if(!this.repositoryController.isInt(type.toString()) || !this.repositoryController.isInt(size.toString()))
				continue;

			try {
				Resource resource = new Resource(
						title.toString(), 
						resDescription.toString(), 
						Integer.parseInt(size.toString()),
						repository.getRepository_id(),
						Integer.parseInt(type.toString()),
						urlRes.toString()
					);
				
				if (!resController.create(resource))
				{
					System.out.println("No se pudo crear el recurso, no exception");
					continue;
				}
				
				//Insertar las áreas del recuros
				JSONArray areas = (JSONArray)resTemp.get("areas");
				
				if(areas != null)
				{
					int areasCount = areas.size();
					
					for(int j = 0; j < areasCount; j++)
					{
						Object areaId = areas.get(j);
						if(areaId == null)
							continue;
						else if(!this.repositoryController.isInt(areaId.toString()))
							continue;
						
						try {
							resController.insertAreaResource(resource.getResource_id(), Integer.parseInt(areaId.toString()));
						} catch (SQLException e) {
							System.out.println("Hubo un problem al guardar el área : " + areaId.toString() + " del recuros " + resource.getResource_id());
							System.out.println(e.getMessage());
						}
					}
				}
				
				//Insertar los autores del recurso
				JSONArray authors = (JSONArray)resTemp.get("authors");
				
				if(authors != null)
				{
					int authorsCount = authors.size();
					
					for(int j = 0; j < authorsCount; j++)
					{
						Object authorId = authors.get(j);
						
						if(authorId == null)
							continue;
						else if(!this.repositoryController.isInt(authorId.toString()))
							continue;
						
						try {
							resController.insertAuthorResource(resource.getResource_id(), Integer.parseInt(authorId.toString()));
						} catch (SQLException e) {
							System.out.println("Hubo un problem al guardar el autor : " + authorId.toString() + " del recuros " + resource.getResource_id());
							System.out.println(e.getMessage());
						}
					}
				}
				
				
			} catch (SQLException e) {
				System.out.println("Hubo un problem al guardar el recurso: ");
				System.out.println(e.getMessage());
			}
		}
		JSONObject jsonId = new JSONObject();
		jsonId.put("repository_id", repository.getRepository_id());
		
		return jsonId.toString();
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

		Repository repo = this.repositoryController.getById(id);

		if (repo == null)
			return Response.getJSONError("Repositorio con el id " + id + " no encontrado", 404, res);

		try {
			if(repo.getCreator_id() != Integer.parseInt(session.getAttribute("user_id").toString()))
				return Response.getJSONError("No puedes eliminar este repositorio, no te pertenece", 400, res);

			if (this.repositoryController.delete(id))
				return repo.toJSON();
			else
				return Response.getJSONError("No se pudo eliminar el repositorio", 400, res);
		} catch (SQLException e) {
			return Response.getJSONError(e.getMessage(), 400, res);
		}
	}
}