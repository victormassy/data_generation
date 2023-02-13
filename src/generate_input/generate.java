package generate_input;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class generate {

	// Function that splits a long into 3 shares such as long = long1 + long2 + long3
	public static long[] longSharing(long input, int pow2) {
		long[] sharedInput = new long[3];
		Random r = new Random();
		// The sum of 2 shared inputs should remain under 2**pow2
		sharedInput[0] = r.nextLong((int) -Math.pow(2, pow2 - 1), (int) Math.pow(2, pow2 - 1));
		sharedInput[1] = r.nextLong((int) -Math.pow(2, pow2 - 1), (int) Math.pow(2, pow2));
		sharedInput[2] = input - sharedInput[0] - sharedInput[1];

		return sharedInput;

	}

	// Generate a random boolean with probability prob
	public static long generateRandomBoolean(int prob) {
		Random r = new Random();
		int value = r.nextInt(0, 100);
		if (value < prob)
			return 1;
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int pow2Reports = 4;
		int nbReports = (int) Math.pow(2, pow2Reports);
		int nbVersions = 10; 
		int probTrigger = 3;
		int breakdownValue = 100;
		int pow2 = 32;
		int nbUsers = 10000;
		int valueRange = 10;
		ArrayList<eventReport> reports;

		Random r = new Random();
		long matchKey;
		long isTriggerReport;
		String str;
		byte[] strToBytes;
		FileOutputStream outputStream;
		long [] row; 
		long [] sharedValues; 
		String str1;
		String str2; 
		String str3; 
		int count;
		for(int version = 0; version < nbVersions; version ++) {
			count = 0; 
			reports = new ArrayList<eventReport>();
			for (int i = 0; i < nbReports; i++) {
				matchKey = r.nextLong(0, nbUsers);
				isTriggerReport = generateRandomBoolean(probTrigger);
				if (isTriggerReport == 1 && i < nbReports - 1) {
					count ++;
					// Create matching source event 
					reports.add(r.nextInt(0,i+1) ,new eventReport(matchKey, 0,
							r.nextLong(0, breakdownValue), 0));
					i++;
					// Add trigger report
					reports.add(
							new eventReport(matchKey, 
									1, 0, r.nextLong(1, valueRange)));
				} else {
					// Add source report
					reports.add(new eventReport(matchKey,
							0, r.nextLong(0, breakdownValue),0));
				}
	
			}
			System.out.println("Num trigger: "+count);
			//Input file in the clear for Gramine model 
			outputStream= new FileOutputStream(
					"local_repo\\gramine_rep_" + pow2Reports + "_v_" + version +".txt");
			//Our model needs nb of reports in first line
			str = nbReports + "\n";
			strToBytes = str.getBytes();
			outputStream.write(strToBytes);
			for (int i = 0; i < nbReports; i++) {
				str = reports.get(i).toStringGramine() + "\n";
				strToBytes = str.getBytes();
				outputStream.write(strToBytes);
			}
			outputStream.close();
	
			// Input file in the clear for EdgelessDB model 
			outputStream = new FileOutputStream("local_repo\\edgeless_rep_" + pow2Reports +  "_v_" + version +".txt");
			str = "INSERT INTO attribution.reports (match_key, is_trigger, value, breakdown_key) VALUES\n";
			strToBytes = str.getBytes();
			outputStream.write(strToBytes);
			for (int i = 0; i < nbReports-1; i++) {
				str = reports.get(i).toStringEdgelessDB(false) + "\n";
				strToBytes = str.getBytes();
				outputStream.write(strToBytes);
			}
			str = reports.get(nbReports-1).toStringEdgelessDB(true) + "\n";
			strToBytes = str.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
	
			
			  //3 input files for Helpers in IPA
			  FileOutputStream share1 = new FileOutputStream("local_repo\\input_MPCshare1_" +pow2Reports + "_v_" + version + ".txt"); 
			  FileOutputStream share2 = new FileOutputStream("local_repo\\input_MPCshare2_" +pow2Reports + "_v_" + version + ".txt");
			  FileOutputStream share3 = new FileOutputStream("local_repo\\input_MPCshare3_" +pow2Reports + "_v_" + version + ".txt"); 
			  str1 = "";
			  str2 = ""; 
			  str3 = ""; 
			  for(int i=0; i<nbReports; i++) { 
				  row = reports.get(i).valuesInRow(); 
				  for(int j = 0; j< row.length; j++) {
					  sharedValues = longSharing(row[j],pow2); 
					  str1 += Long.toString(sharedValues[0]) + " ";
					  str2 += Long.toString(sharedValues[1])+ " "; 
					  str3 += Long.toString(sharedValues[2]) + " ";			  
				  } 
				  str1 += "\n"; 
				  str2 += "\n"; 
				  str3 += "\n"; 
				  strToBytes = str1.getBytes();
				  share1.write(strToBytes); strToBytes = str2.getBytes();
				  share2.write(strToBytes); strToBytes = str3.getBytes();
				  share3.write(strToBytes); str1 = ""; str2 = ""; str3 = ""; 
			  }
			  share1.close(); 
			  share2.close(); 
			  share3.close();
		}
	}

}