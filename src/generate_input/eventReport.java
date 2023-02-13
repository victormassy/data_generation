package generate_input;

public class eventReport {
	private long matchKey;
	private long isTriggerReport;
	private long breakdownKey;
	private long triggerValue;
	
	
	public eventReport(long matchKey, long isTriggerReport,
			long breakdownKey, long triggerValue) {
		super();
		this.matchKey = matchKey;
		this.isTriggerReport = isTriggerReport;
		this.breakdownKey = breakdownKey;
		this.triggerValue = triggerValue;
	}


	public String toStringEdgelessDB(boolean lastItem) { 
		if(lastItem)return "(" +matchKey + ", " + isTriggerReport + 
				", " + triggerValue + ", "+  breakdownKey + ");";
		else return "(" +matchKey + ", " + isTriggerReport + ", "
				+ triggerValue + ", "+  breakdownKey + "),";
	}
	
	public String toStringGramine() {
		return  matchKey + " " + isTriggerReport + " " + triggerValue 
				+ " "+  breakdownKey;
	}

	public long[] valuesInRow() {
		long[] row = new long[4];
		row[0] = matchKey;
		row[1] = isTriggerReport;
		row[2] = triggerValue;
		row[3] = breakdownKey;
		return row;
	}

	public long getMatchKey() {
		return matchKey;
	} 
	
	
	
	
}
