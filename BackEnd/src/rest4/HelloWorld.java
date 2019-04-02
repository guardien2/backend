package rest4;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;

import org.neo4j.driver.v1.*;


@Path("admin")
public class HelloWorld {
	
	private String uri = "bolt://localhost:7687";
	private String user = "";
	private String password = "";

	

	@GET
	@Path("hello")
	public String sayHello() {
		return "Hello World";
	}
	

	@GET
	@Path("connect")
	public String connect() {
	    Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			   StatementResult result = session.run("MATCH (n)\r\n" + 
			   		"RETURN count(n)");
			   
			  String s = result.next().toString();
			   //List<Record> storeList = storeList(result);
			   
			   
		
			   
				return s;
		}
	

	}
	
	/* public List<Record> storeList(StatementResult statementResult) {

	        List<Record> list = new ArrayList<>();
	        while (statementResult.hasNext()) {

	            list.add(statementResult.next());

	        }

	        return list;

	    }
*/
}
