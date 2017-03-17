package messages;

public class ReportMessage extends Message {
	private static final long serialVersionUID = 1L;
	public String report;

	public ReportMessage(String report) {
		this.action = "Report";
		this.report = report;
	}
}