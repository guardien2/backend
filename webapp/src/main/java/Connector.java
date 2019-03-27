import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

public class Connector implements AutoCloseable {

	
	private String uri = "bolt://localhost:7474";
	private String user = "neo4j";
	private String password = "password";
	private final Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	
	public Connector() {
		
	}

	public void close() throws Exception {
		driver.close();
	}

	public String printGreeting(final String message) {
		try (Session session = driver.session()) {
			String greeting = session.writeTransaction(new TransactionWork<String>() {
				@Override
				public String execute(Transaction tx) {
					StatementResult result = tx.run(
							"CREATE (a:Greeting) " + "SET a.message = $message " + "RETURN a.message + ', from node ' + id(a)",
							parameters("message", message));
					return result.single().get(0).asString();
				}
			});
			return greeting;
		}	
	}

	public static void main(String... args) throws Exception {
		try (Connector greeter = new Connector()) {
			greeter.printGreeting("hello, world");
		}
	}

}
