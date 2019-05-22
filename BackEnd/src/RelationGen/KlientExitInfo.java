package RelationGen;
import java.util.Map;

public class KlientExitInfo {
	
	private String exitState;
	private boolean displayFirst;
	private String action;
	private String returnWhen;
	private String returnCommand;
	private String to;
	private String sendCommandType;
	
	public KlientExitInfo() {
		
	}
	
	public KlientExitInfo(Map<String,String> init)
	{
		this.initFromMap(init);
	}
	
	public void initFromMap(Map<String,String> init)
	{
		exitState = init.get("exit-state");
		displayFirst = "true".equals(init.get("display.first")) ? true : false;
		action = init.get("action");
		returnWhen = init.get("return-when");
		returnCommand = init.get("return-command");
		to = init.get("to");
		sendCommandType = init.get("send-command-type");
		
	}
	
	public String getExitState() {
		if (exitState == null) exitState = "";
		return exitState;
	}
	public void setExitState(String exitState) {
		this.exitState = exitState;
	}
	public boolean isDisplayFirst() {
		return displayFirst;
	}
	public void setDisplayFirst(boolean displayFirst) {
		this.displayFirst = displayFirst;
	}
	public String getAction() {
		if (action == null) return "";
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getReturnWhen() {
		if (returnWhen == null) returnWhen = "";
		return returnWhen;
	}
	public void setReturnWhen(String returnWhen) {
		this.returnWhen = returnWhen;
	}
	public String getReturnCommand() {
		if (returnCommand == null) returnCommand = "";
		return returnCommand;
	}
	public void setReturnCommand(String returnCommand) {
		this.returnCommand = returnCommand;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSendCommandType() {
		return sendCommandType;
	}
	public void setSendCommandType(String sendCommandType) {
		this.sendCommandType = sendCommandType;
	}
	@Override
	public String toString() {
		return "KlientExitInfo [exitState=" + exitState + ", displayFirst=" + displayFirst + ", action=" + action
				+ ", returnWhen=" + returnWhen + ", returnCommand=" + returnCommand + ", to=" + to
				+ ", sendCommandType=" + sendCommandType + "]";
	}

}
