package rest4;

//import java.util.ArrayList;
//import java.util.List;

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
	@Path("{id}")
	@Produces("application/json")
	public Person hamtaPerson(@PathParam("id")int id) {
		System.out.println(String.format("Söker efter person med id %d", id));
		Person p = new Person(id,"John","Eriksson",1995);
		return p;
	}
	
	@GET
	@Path("connect")
	
	public String connect() {
	    Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			   StatementResult result = session.run("MATCH (n:Java) RETURN n.name LIMIT 25");
			   
			   
			   Record res = null;
		       while (result.hasNext()) {
		    	    res = result.next();
		        	System.out.println(res.get(0));
		        
		            
		       }
		       
			   /*List<Record> storeList = storeList(result);
			   for(Record r :storeList) {
				   System.out.print(r.get(0));
			   }*/
			   
		
			   
				return res.toString();
		}
	

	}
	
	/* public List<Record> storeList(StatementResult statementResult) {

	        List<Record> list = new ArrayList<>();
	        while (statementResult.hasNext()) {
	        	System.out.println(statementResult.next());
	            list.add(statementResult.next());

	        }

	        return list;

	    }*/
	@GET
	@Path("class")
	@Produces("application/json")
	public String ClassFind() {
		 Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
			try (Session session = driver.session()) {
				StatementResult result = session.run("match (n:Class) return n.fqn as fqn, n.name as name");

				Record res = null;
				
				System.out.println(result.list());
				
				while(result.hasNext()) {
					res = result.next();
					//System.out.println(result.list().toString());
					System.out.println(res.get("fqn"));
				}
			return "hej";	   
			}
	}
	

}
