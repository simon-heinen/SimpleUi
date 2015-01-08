package simpleui.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * You have to manually add @PathParam("id") to the java parameters
 * 
 * @param <T>
 *            The type of the id, normally {@link Integer} or {@link String}
 */
public interface CrudRestInterfaceTemplate<T> {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String body);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String read(@PathParam("id") T id);

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String update(@PathParam("id") T id, String body);

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String delete(@PathParam("id") T id);

}
