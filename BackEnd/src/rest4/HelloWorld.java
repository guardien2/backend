package rest4;

import java.util.ArrayList;
import java.util.List;

//import java.util.ArrayList;
//import java.util.List;

import javax.ws.rs.*;


import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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
				StatementResult result = session.run("MATCH (a:Artifact)-[:CONTAINS]->(t:Type) RETURN a.fileName AS artefakt, count(t) AS klasser ORDER BY klasser DESC");

				
				String artefakt= null;
				String klasser = null;
				String json = null;
				List<Name> nameList = new ArrayList<>();
		
				Name n = null;
				//System.out.println(result.list());
				
				//loop through all data
				while(result.hasNext()) {
					Record res = result.next();
					System.out.println(res.toString());
					
					artefakt = res.get("artefakt").toString();
					klasser = res.get("klasser").toString();
					n = new Name(artefakt,klasser);
					
					
					nameList.add(n);
				
				}
				
				//Jackson ObjectMapper used to serialize java object as JSON output
				ObjectMapper objectMapper = new ObjectMapper();
			
				json = objectMapper.writeValueAsString(nameList);
				System.out.println(json);
				
				return json;
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			return "Cant Connect to DB";
				
		
				
				
   

	}
	
	

}
