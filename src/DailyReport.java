import static java.lang.System.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class DailyReport implements Runnable{

	static Properties props;
	static Logger logger;
	static Connection conn,conn2,conn3;
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	
	String home_dir;
	DailyReport() throws FileNotFoundException, IOException{
		home_dir = "Log4j.properties";
		loadProperties();
		logger = Logger.getLogger(DailyReport.class);
		testMode = "TRUE".equals(props.getProperty("testMode").toUpperCase())?true:false;
		logger.info("Test Mod : "+testMode);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Thread t = new Thread(new DailyReport());
		Thread t2 = new Thread(new Runnable(){
			@Override
			public void run() {
				while(!exit){
					String now = sdf.format(new Date());
					logger.info(now+" program running...");
					if(now.equals(props.getProperty("Joy.StartTime"))||testMode){
						JoyReportSend = true;
					}
					if(now.equals(props.getProperty("US.StartTime"))||testMode){
						USReportSend = true;
					}
					if(now.equals(props.getProperty("CRM.StartTime"))||testMode){
						CRMReportSend = true;
					}
					
					
					if(testMode) return;
					try {
						Thread.sleep(1000*60);
					} catch (InterruptedException e) {
					}
				}
			}
			
		});
		t.start();
		t2.start();
		
	}
	
	static boolean exit = false;
	static boolean testMode = true;
	static boolean JoyReportSend = false;
	static boolean USReportSend = false;
	static boolean CRMReportSend = false;
	
	@Override
	public void run(){
		while(!exit){
			logger.info(" program2 running...");
			if(JoyReportSend||testMode){
				logger.info("joyReport starting...");
				try {
					connectDB();
					joyReport();
					JoyReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send joyReport!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("joyReport end...");
			}

			if(USReportSend||testMode){
				logger.info("US Report starting...");
				try {
					connectDB();
					sendVolumeReport();
					USReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send US Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("US Report end...");
			}
			if(CRMReportSend||testMode){
				logger.info("CRM Report starting...");
				try {
					connectDB();
					sendCRMReport();
					CRMReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send CRM Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("CRM Report end...");
			}
			
			if(testMode) return;
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	public static void sendCRMReport(){
		
		String fileName = "nameBinding_"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".xlsx";
		List<Map<String,String>> head = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		
		
		Map<String,String> m = null;
		
		m = new HashMap<String,String>();
		m.put("name", "Secondary Number");
		m.put("col", "chinaMsisdn");
		head.add(m);
		
		m = new HashMap<String,String>();
		m.put("name", "Document Type");
		m.put("col", "type");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "name");
		m.put("col", "name");
		head.add(m);

		m = new HashMap<String,String>();
		m.put("name", "Document No.");
		m.put("col", "id");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Location");
		m.put("col", "location");
		head.add(m);
		
		
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://192.168.10.199:3306/CRM_DB?characterEncoding=utf8", "crmuser", "crm");
			st = conn.createStatement();
			
			String sql = "select serviceid,name,id,type,chinaMsisdn "
					+ "from CRM_DB.CRM_NAME_VERIFIED "
					+ "where send_date is null ";
			
			out.println("Execute SQL:"+sql);
			
			rs =st.executeQuery(sql);
			
			
			while(rs.next()){
				Map<String,Object> m2 = new HashMap<String,Object>();
				m2.put("name", rs.getString("name"));
				m2.put("id", rs.getString("id"));
				m2.put("type", rs.getString("type"));
				m2.put("chinaMsisdn", rs.getString("chinaMsisdn"));
				m2.put("location", "Taiwan");
				data.add(m2);
			}
			rs.close();
			Workbook wb = createExcel(head,data,"xlsx");
			File f = new File(fileName);
			FileOutputStream fo = new FileOutputStream(f);
			wb.write(fo);
			fo.close();
			
			
			//20161129 驗證已import內容
			//重複的中國號
			sql = "select chinaMsisdn,count(1) CD from CRM_DB.CRM_NAME_VERIFIED group by chinaMsisdn having count(1)>1 ";
			//重複的中華號
			sql = "select chtMsisdn,count(1) CD from CRM_DB.CRM_NAME_VERIFIED group by chtMsisdn 	having count(1)>1 ";
			//重複數大於5的證號
			sql = "select id,count(1) CD from CRM_DB.CRM_NAME_VERIFIED group by id having count(1)>5 ";
			//同名不同證號
			sql = "select distinct A.name,A.id,B.id from CRM_DB.CRM_NAME_VERIFIED A inner join CRM_DB.CRM_NAME_VERIFIED B on A.name = B.name "
					+ "where  A.type=B.type and A.id<>B.id " ;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(st!=null) st.close();
				if(conn!=null) conn.close();
			} catch (SQLException e) {	}
		}
		
		String subject = "實名制Report",mailReceiver=props.getProperty("CRM.recevier"),mailContent="";
		if(testMode || mailReceiver == null || "".equals(mailReceiver)){
			mailReceiver = props.getProperty("default.recevier");
			//subject = "Joy default Report";
		}
		sendMail(subject,mailContent, "CRM_Report", mailReceiver,fileName);
		
	}
	
	public static Workbook createExcel(List<Map<String,String>> head,List<Map<String,Object>> data,String type) throws IOException{
		Workbook wb = null;
		int rowN = 0;
		int sheetN = 0;
		//建立xls檔案
		if(type.matches("^xls$")){
			wb = new HSSFWorkbook();  
			HSSFSheet sheet = (HSSFSheet) wb.createSheet("sheet"+sheetN++);  
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 20*256);
			HSSFRow row = sheet.createRow(rowN++);
			HSSFCell cell ;
			//欄位樣式
			HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); 

			//字型大小
			HSSFFont font = (HSSFFont) wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //粗體

			style.setFont(font);
			
			//寫入Header
			for(int col = 0 ;col < head.size() ;col++){
				cell = row.createCell(col);
				cell.setCellStyle(style);
				cell.setCellValue(head.get(col).get("name"));
			}
			
			for(int r = 0 ; r<data.size() ;r++){
				row = sheet.createRow(rowN++);
				for(int col = 0; col < head.size() ;col++){
					row.createCell(col).setCellValue(nvl(data.get(r).get(head.get(col).get("col")),"").toString());;
				}
				//滿頁換Sheet
				if(rowN==65534){
					sheet = (HSSFSheet) wb.createSheet("sheet"+sheetN++);
					rowN = 0;
				}
			}
			
		
		}
		
		//建立xlsx檔案
		if(type.matches("^xlsx$")){
			wb = new XSSFWorkbook();  
			XSSFSheet sheet = (XSSFSheet) wb.createSheet("sheet"+sheetN++);  
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 20*256);
			XSSFRow row = sheet.createRow(rowN++);
			XSSFCell cell ;
			//欄位樣式
			XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle(); 

			//字型大小
			
			
			//寫入Header
			for(int col = 0 ;col < head.size() ;col++){
				cell = row.createCell(col);
				cell.setCellStyle(style);
				cell.setCellValue(head.get(col).get("name"));
			}
			
			for(int r = 0 ; r<data.size() ;r++){
				row = sheet.createRow(rowN++);
				for(int col = 0; col < head.size() ;col++){
					row.createCell(col).setCellValue(nvl(data.get(r).get(head.get(col).get("col")),"").toString());;
				}
				//滿頁換Sheet
				if(rowN==65534){
					sheet = (XSSFSheet) wb.createSheet("sheet"+sheetN++);
					rowN = 0;
				}
			}
		}
		
		return wb;
	}
	
	public static void ErrorHandle(String cont){
		ErrorHandle(cont,null);
	}
	/**
	 * 
	 * @param fileName
	 * @param head Map 內容 name:欄位名稱，col:欄位代號
	 * @param data Map<col,value>
	 * @return
	 */
	public boolean createSheetFile(String fileName,List<Map<String,String>> head,List<Map<String,Object>> data){
		
		boolean result = false;
		if(fileName == null){
			System.out.println("File name is null.");
			return false;
		}

		int rowN = 0;
		int sheetN = 0;
		//建立xls檔案
		if(fileName.matches(".+\\.xls")){
			HSSFWorkbook wb = new HSSFWorkbook();  
			HSSFSheet sheet = wb.createSheet("sheet"+sheetN++);  
			sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 20*256);
			HSSFRow row = sheet.createRow(rowN++);
			HSSFCell cell ;
			//欄位樣式
			HSSFCellStyle style = wb.createCellStyle(); 

			//字型大小
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //粗體

			style.setFont(font);
			
			//寫入Header
			for(int col = 0 ;col < head.size() ;col++){
				cell = row.createCell(col);
				cell.setCellStyle(style);
				cell.setCellValue(head.get(col).get("name"));
			}
			
			for(int r = 0 ; r<data.size() ;r++){
				row = sheet.createRow(rowN++);
				for(int col = 0; col < head.size() ;col++){
					row.createCell(col).setCellValue(nvl(data.get(r).get(head.get(col).get("col")),""));;
				}
				//滿頁換Sheet
				if(rowN==65534){
					sheet = wb.createSheet("sheet"+sheetN++);
					rowN = 0;
				}
			}
			
			try {
				File f = new File(fileName);
				FileOutputStream os = new FileOutputStream(f);
				wb.write(os);
				os.close();
				result = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//建立xlsx檔案
		if(fileName.matches(".+\\.xlsx")){
			
		}
		return result;
	}

	public void joyReport() throws Exception{
		String imsiStart = props.getProperty("IMSI.start");
		String imsiEnd = props.getProperty("IMSI.end");
		
		if("".equals(imsiStart)||"".equals(imsiEnd))
			throw new Exception("no efficient imsi set.");
			
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf2.format(c.getTime());
		
		String sql = "SELECT IMSI, MIN(SUBSTR(CALLTIME,1,10)) START_DATE, SUM(DATAVOLUME)/1024/1024 VOLUME_MB "
				+ "FROM HUR_DATA_USAGE "
				+ "WHERE IMSI>'"+imsiStart+"' AND IMSI <='"+imsiEnd+"' "
				+ "AND SUBSTR(CALLTIME,1,10)<='"+today+"' "
				+ "GROUP BY IMSI ";
		Statement st = null;
		ResultSet rs = null;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "Joy-TWdata"+today.replace("/", "")+".xls";
			st = conn.createStatement();
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
			
				Map<String,Object> m = new HashMap<String,Object>();
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null));
				data.add(m);
			}
			//createFile(csvName,csvContent);
			
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("IMSI","IMSI"));
			head.add(mapSetting("START_DATE","START_DATE"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
			
			createSheetFile(csvName,head,data);
			String mailReceiver = props.getProperty("Joy.recevier");
			String subject = "Joy daily report-"+today.replace("/", "");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Dear Joy colleagues,\n"
					+ "\n"
					+ "Please see the daily report for Taiwan Data Card product.\n"
					+ "Thank you very much.\n"
					+ "\n"
					+ "Sim2travel Inc.";
			
			sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	
	private static Map<String,String> mapSetting(String name,String col){
		Map<String, String> m = new HashMap<String,String>();
		
		m.put("name", name);
		m.put("col", col);
		
		return m;
	}

	public void createFile(String fileName,String content) throws IOException{
		File file = new File(fileName);
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			fw.append(content);
		} finally {
			try {
				if(fw!=null) fw.close();
			} catch (IOException e) {}
		}
	}
	
	public void connectDB() throws ClassNotFoundException, SQLException{
		String url=props.getProperty("Oracle.URL")
				.replace("{{Host}}", props.getProperty("Oracle.Host"))
				.replace("{{Port}}", props.getProperty("Oracle.Port"))
				.replace("{{ServiceName}}", (props.getProperty("Oracle.ServiceName")!=null?props.getProperty("Oracle.ServiceName"):""))
				.replace("{{SID}}", (props.getProperty("Oracle.SID")!=null?props.getProperty("Oracle.SID"):""));
		
		conn=connDB(props.getProperty("Oracle.DriverClass"), url, 
				props.getProperty("Oracle.UserName"), 
				props.getProperty("Oracle.PassWord")
				);
		logger.info("Connect to "+url);
	}
	public Connection connDB(String DriverClass, String URL,
			String UserName, String PassWord) throws ClassNotFoundException, SQLException {
		Connection conn = null;

			Class.forName(DriverClass);
			conn = DriverManager.getConnection(URL, UserName, PassWord);
		return conn;
	}
	
	public void loadProperties() throws FileNotFoundException, IOException{
		props = new Properties();  
		props.load(new FileInputStream(home_dir));
		PropertyConfigurator.configure(props);
	}

	public void sendVolumeReport(){
		logger.info("sendVolumeReport...");
		Statement st = null;
		ResultSet rs = null;
		
		Map<String,Map<String,Map<String,String>>> totalCount = new HashMap<String,Map<String,Map<String,String>>>();
		
		try{
			st = conn.createStatement();
			
			
			//統計每月有在美國使用總天數與總量，MO日期、CD天數、SU量
			String sql=
					"select A.SERVICEID,substr(A.day,0,6) MO,count(1) CD,sum(A.volume) SU "
					+ "from HUR_CURRENT_DAY A "
					+ "where A.MCCMNC like '310%' "
					+ "group by A.Serviceid,substr(A.day,0,6)";
			
			
			logger.debug("SQL : "+sql);
			rs = st.executeQuery(sql);
			logger.info("Query end!");

			while(rs.next()){
				Map<String,Map<String,String>> m1 = new HashMap<String,Map<String,String>>();
				Map<String,String> m2 = new HashMap<String,String>();
				String serviceid = rs.getString("SERVICEID");
				String month = rs.getString("MO");

				if(totalCount.containsKey(serviceid)){
					m1 = totalCount.get(serviceid);
				}
				
				m2.put("DAY", String.valueOf(rs.getInt("CD")));
				m2.put("VOLUME", String.valueOf(rs.getDouble("SU")/1024/1024));
				
				m1.put(month, m2);
				totalCount.put(serviceid, m1);
			}
			
			rs = null;
			
			//統計每月在美國流量包中使用天數與總量，PID、CD天數、SU量
			Map<String,Map<String,String>> subCount = new HashMap<String,Map<String,String>>();
			
			sql=
					"select A.PID,count(1) CD ,sum(B.s) SU "
					+ "from HUR_VOLUME_POCKET A,(	select B.Serviceid,B.day,sum(B.volume) s "
					+ "								from HUR_CURRENT_DAY B "
					+ "								where B.MCCMNC like '310%' "
					+ "								group by B.Serviceid,B.day) B "
					+ "where A.SERVICEID = B.Serviceid AND A.START_DATE<=B.day AND A.END_DATE>=B.day "
					+ "group by A.PID "
					+ "order by A.pid ";
			
			logger.debug("SQL : "+sql);
			rs = st.executeQuery(sql);
			logger.info("Query end!");

			while(rs.next()){
				Map<String,String> m1 = new HashMap<String,String>();
				m1.put("DAY", String.valueOf(rs.getInt("CD")));
				m1.put("VOLUME", String.valueOf(FormatDouble(rs.getDouble("SU")/1024/1024, "0.0000")));
				subCount.put(rs.getString("PID"), m1);
			}
			
			rs = null;
			
			
			//建立report
			String report = "";
			report+="<html><head></head><body><table>";

			String [] v = new String[]{
					"中華門號",
					"起始時間",
					"結束時間",
					//"Email",
					"已警示",
					"建立時間",
					"取消時間",
					"客戶姓名",
					//"進線者姓名",
					"手機型號",
					"期間內流量(MB)",
					"期間外流量(MB)",
					"期間內使用天數",
					"時間外使用天數",
					};
			report += pfString(v);
			
			sql=
					"SELECT A.SERVICEID,A.PID,B.FOLLOWMENUMBER CHTMSISDN,A.SERVICEID,A.MCC,A.ALERTED,A.ID,A.CALLER_NAME,A.CUSTOMER_NAME,A.PHONE_TYPE,A.EMAIL,A.CANCEL_REASON, "
					+ "A.START_DATE,A.END_DATE,"
					+ "TO_CHAR(A.CREATE_TIME,'yyyy/MM/dd hh24:mi:ss') CREATE_TIME,TO_CHAR(A.CANCEL_TIME,'yyyy/MM/dd hh24:mi:ss') CANCEL_TIME "
					+ "from HUR_VOLUME_POCKET A,FOLLOWMEDATA B "
					+ "WHERE A.SERVICEID = B.SERVICEID(+) AND A.TYPE=0 AND B.FOLLOWMENUMBER like '886%' "
					+ "ORDER BY A.START_DATE DESC ";
			
			logger.debug("SQL : "+sql);
			rs = st.executeQuery(sql);
			logger.info("Query end!");

			while(rs.next()){
				String serviceid = rs.getString("SERVICEID");
				String pid = rs.getString("PID");
				String id = convertString(rs.getString("ID"),"ISO-8859-1","Big5");
				String cusName = convertString(rs.getString("CUSTOMER_NAME"),"ISO-8859-1","Big5");
				//String calName = convertString(rs.getString("CALLER_NAME"),"ISO-8859-1","Big5");
				String startDate = rs.getString("START_DATE");
				String endDate = rs.getString("END_DATE");
				if(!id.matches("^\\d+$")){
					//cusName = markName(cusName);
					//calName = markName(calName);
				}
				int totalday = 0;
				double totleVolume = 0d;
				if(totalCount.containsKey(serviceid)){
					String startMonth = startDate.substring(0,6);
					String endMonth = endDate.substring(0,6);
					String d;
					if(totalCount.get(serviceid).containsKey(startMonth)){
						d = totalCount.get(serviceid).get(startMonth).get("DAY");
						totalday += (d==null?0:Integer.parseInt(d));
						d = totalCount.get(serviceid).get(startMonth).get("VOLUME");
						totleVolume += (d==null?0d:Double.parseDouble(d));
					}
					if(!startMonth.endsWith(endMonth)&&totalCount.get(serviceid).containsKey(endMonth)){
						d = totalCount.get(serviceid).get(endMonth).get("DAY");
						totalday += (d==null?0:Integer.parseInt(d));
						d = totalCount.get(serviceid).get(endMonth).get("VOLUME");
						totleVolume += (d==null?0d:Double.parseDouble(d));
					}
				}
				
				int inDay = (subCount.get(pid)!=null?Integer.parseInt(subCount.get(pid).get("DAY")):0);
				double inVolume = (subCount.get(pid)!=null?Double.parseDouble(subCount.get(pid).get("VOLUME")):0.d);
				
				report += pfString(new String[]{
						rs.getString("CHTMSISDN"),
						startDate,
						endDate,
						//convertString(rs.getString("EMAIL"),"ISO-8859-1","Big5"),
						rs.getString("ALERTED"),
						rs.getString("CREATE_TIME"),
						nvl(rs.getString("CANCEL_TIME")," "),
						cusName,
						//calName,
						convertString(rs.getString("PHONE_TYPE"),"ISO-8859-1","Big5"),
						//String.valueOf(volumeList.get(pid)==null?FormatDouble(0d, "0.0000"):FormatDouble((Double) volumeList.get(pid)/1024/1024, "0.0000")),
						String.valueOf(inVolume),
						String.valueOf(FormatDouble(Math.abs(totleVolume-inVolume), "0.0000")),
						String.valueOf(inDay),
						String.valueOf(totalday-inDay),
						});
			}
			report+="</table></body></html>";
		
			String mailReceiver = props.getProperty("US.recevier");
			String subject = "美國流量包 Report";
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				subject = "美國流量包 Deafult Report";
			}
			
			
			//sendMail("美國流量包Report", report, "DVRS Report", "Galen.Kao@sim2travel.com,douglas.chuang@sim2travel.com,yvonne.lin@sim2travel.com,ranger.kao@sim2travel.com");
			sendHTMLMail(subject, report, "USPacketReport", mailReceiver );
			//sendHTMLMail("美國流量包Report", report, "DVRS Report", "ranger.kao@sim2travel.com");
			

				

		} catch (SQLException e) {
			ErrorHandle("At set sendVolumeReport Got a SQLException", e);
		} catch (UnsupportedEncodingException e) {
			ErrorHandle("At set sendVolumeReport Got a UnsupportedEncodingException", e);
		}finally{
			try {
				if(st!=null)
					st.close();
				
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public Double FormatDouble(Double value,String form){
		if(value == null)
			value = 0d;
		
		if(form==null || "".equals(form)){
			form="0.00";
		}
			
		/*DecimalFormat df = new DecimalFormat(form);   
		String str=df.format(value);*/
		
		return Double.valueOf(new DecimalFormat(form).format(value));
	}
	
public static String convertString(String msg,String sCharset,String dCharset) throws UnsupportedEncodingException{
		
		if(msg==null)
			msg=" ";
		
		return sCharset==null? new String(msg.getBytes(),dCharset):new String(msg.getBytes(sCharset),dCharset);
	}
public static String nvl(Object msg,String s){
	if(msg==null)
		msg = s;
	return msg.toString();
}
	
	static String errorMsg;
	public static void ErrorHandle(String cont,Exception e){
		if(e!=null){
			logger.error(cont, e);
			
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			//send mail
			errorMsg=s.toString();
		}else{
			logger.error(cont);
			errorMsg="";
		}
		
		sendErrorMail(cont);
	}
	
	
	static String errorReceviver,errorSubject,errorContent;
	
	static void sendErrorMail(String msg){

		errorReceviver=props.getProperty("mail.errorReceviver");
		errorSubject="DVRS Warnning Mail";
		errorContent="Error :"+msg+"<br>\n"
				+ "Error occurr time: "+DateFormat()+"<br>\n"
				+ "Error Msg : "+errorMsg;	
		
		String [] cmd=new String[3];
		cmd[0]="/bin/bash";
		cmd[1]="-c";
		cmd[2]= "/bin/echo \""+errorContent+"\" | /bin/mail -s \""+errorSubject+"\" -r DVRS_ALERT "+errorReceviver;

		try{
			Process p = Runtime.getRuntime().exec (cmd);
			p.waitFor();
			if(logger!=null)
				logger.info("send mail cmd:"+cmd[2]);
			System.out.println("send mail cmd:"+cmd[2]);
		}catch (Exception e){
			if(logger!=null)
				logger.info("send mail fail:"+cmd[2]);
			System.out.println("send mail fail:"+cmd[2]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
	
	static String iniform= "yyyy/MM/dd HH:mm:ss";
	public static String DateFormat(){
		DateFormat dateFormat = new SimpleDateFormat(iniform);
		return dateFormat.format(new Date());
	}
	
	public static String pfString(String[] value){
		String r ="";
		r+="<tr>";
		
		for(int i = 0;i<value.length;i++){
			if(i==value.length-1)
				r+="<td align='right'>";
			else 
				r+="<td>";
			
			r+= value[i]+"</td>";
		}
		r+="</tr>";
		
		return r;
	}
	
	
	void sendMail(String mailSubject,String mailContent,String mailSender,String mailReceiver){
		sendMail(mailSubject,mailContent,mailSender,mailReceiver,null);
	}
	static void sendMail(String mailSubject,String mailContent,String mailSender,String mailReceiver,String fileName){
		String [] cmd=new String[3];
		cmd[0]="/bin/bash";
		cmd[1]="-c";
		cmd[2]= "/bin/echo \""+mailContent+"\" | "
				+ "/bin/mail -s \""+mailSubject+"\" -r "+mailSender+" "
				+(fileName==null?"":"-a "+fileName)+" "
				+mailReceiver;

		try{
			Process p = Runtime.getRuntime().exec (cmd);
			p.waitFor();
			if(logger!=null)
				logger.info("send mail cmd:"+cmd[2]);
			System.out.println("send mail cmd:"+cmd[2]);
		}catch (Exception e){
			if(logger!=null)
				logger.info("send mail fail:"+cmd[2]);
			System.out.println("send mail fail:"+cmd[2]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
	
	void sendHTMLMail(String mailSubject,String mailContent,String mailSender,String mailReceiver){
		String [] cmd=new String[3];
		cmd[0]="/bin/bash";
		cmd[1]="-c";
		cmd[2]= "echo \""+mailContent+"\" | mutt -s \""+mailSubject+"\"  -e \"set content_type=text/html\" "+mailReceiver+" -e 'my_hdr From:"+mailSender+"<local@localhost.com>'";

		try{
			Process p = Runtime.getRuntime().exec (cmd);
			p.waitFor();
			if(logger!=null)
				logger.info("send mail cmd:"+cmd[2]);
			System.out.println("send mail cmd:"+cmd[2]);
		}catch (Exception e){
			if(logger!=null)
				logger.info("send mail fail:"+cmd[2]);
			System.out.println("send mail fail:"+cmd[2]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

}
