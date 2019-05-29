package RelationGen;

import java.util.Map;

/**
 * 
 * @author Mikael Eriksson
 *
 */

public class KlientInfo {
	
	private String xmlFile;
	private String clientName;
	private String programName;
	private String classFqn;
	private String transaction;
	
	private KlientExitInfo exitInfo; // Endast en, vi har flera KlientInfo för klienter med flera utgångar, för enkelhets skull
	
	
	public KlientInfo(String xmlFile, KlientExitInfo exit)
	{
		this.xmlFile = xmlFile;
		this.exitInfo = exit;
	}
	
	public KlientInfo(String xmlFile, Map<String,String> init, KlientExitInfo exit)
	{
		this.xmlFile = xmlFile;
		this.exitInfo = exit;
		initFromMap(init);
	}
	
	public void initFromMap(Map<String,String> init)
	{
		clientName = init.get("name");
		programName = init.get("program-name");
		classFqn =   init.get("class");
		transaction = init.get("transaction");
	}
	
	
	

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getClassFqn() {
		return classFqn;
	}

	public void setClassFqn(String javaClass) {
		this.classFqn = javaClass;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public KlientExitInfo getExitInfo() {
		return exitInfo;
	}

	public void setExitInfo(KlientExitInfo exitInfo) {
		this.exitInfo = exitInfo;
	}

	@Override
	public String toString() {
		return "KlientInfo [xmlFile=" + xmlFile + ", clientName=" + clientName + ", programName=" + programName
				+ ", classFqn=" + classFqn + ", transaction=" + transaction + ", exitInfo=" + exitInfo + "]";
	}
	
	

}
