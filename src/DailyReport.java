import static java.lang.System.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
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
	static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
	
	static boolean exit = false;
	static boolean testMode = true;
	static boolean JoyReportSend = false;
	static boolean USReportSend = false;
	static boolean CRMReportSend = false;
	static boolean SMSReportSend = false;
	static boolean AnnexReportSend = false;
	static boolean go2PlayReportSend = false;
	static boolean applicationReportSend = false;
	static boolean fineMifiReportSend = false;
	static boolean yunYoBoReportSend = false;
	static boolean CCTReportSend = false;
	static boolean FanTravelReportSend = false;
	
	String home_dir;
	DailyReport() throws FileNotFoundException, IOException{
		home_dir = "Log4j.properties";
		loadProperties();
		logger = Logger.getLogger(DailyReport.class);
		testMode = "TRUE".equals(props.getProperty("testMode").toUpperCase())?true:false;
		logger.info("Test Mod : "+testMode);
		//20170816 add
		JoyReportSend = "TRUE".equals(props.getProperty("Joy.preDo").toUpperCase())?true:false;
		USReportSend = "TRUE".equals(props.getProperty("US.preDo").toUpperCase())?true:false;
		CRMReportSend = "TRUE".equals(props.getProperty("CRM.preDo").toUpperCase())?true:false;
		SMSReportSend = "TRUE".equals(props.getProperty("SMS.preDo").toUpperCase())?true:false;
		AnnexReportSend = "TRUE".equals(props.getProperty("Annex.preDo").toUpperCase())?true:false;
		go2PlayReportSend = "TRUE".equals(props.getProperty("GO2PLAY.preDo").toUpperCase())?true:false;
		applicationReportSend = "TRUE".equals(props.getProperty("App.preDo").toUpperCase())?true:false;
		fineMifiReportSend = "TRUE".equals(props.getProperty("FineMifi.preDo").toUpperCase())?true:false;
		yunYoBoReportSend = "TRUE".equals(props.getProperty("YunYoBo.preDo").toUpperCase())?true:false;
		CCTReportSend = "TRUE".equals(props.getProperty("CCT.preDo").toUpperCase())?true:false;
		FanTravelReportSend = "TRUE".equals(props.getProperty("FanTravel.preDo").toUpperCase())?true:false;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Thread t = new Thread(new DailyReport());
		Thread t2 = new Thread(new Runnable(){
			@Override
			public void run() {
				while(!exit){
					logger.info("Test Mod t2: "+testMode);
					String now = sdf.format(new Date());
					logger.info(now+" program running...");
					if(now.equals(props.getProperty("Joy.StartTime"))){
						JoyReportSend = true;
					}
					if(now.equals(props.getProperty("US.StartTime"))){
						//20170220 stop
						USReportSend = true;
					}
					System.out.println("watcherCRM "+now);
					if(now.equals(props.getProperty("CRM.StartTime"))){
						Calendar cal = Calendar.getInstance();
						int week = cal.get(Calendar.DAY_OF_WEEK);
						if(2<=week && week<=6 ){
							CRMReportSend = true;
						}
					}
					//20170216 add
					if(now.equals(props.getProperty("SMS.StartTime"))){
						SMSReportSend = true;
					}
					//20170703 add
					if(now.equals(props.getProperty("Annex.StartTime"))){
						AnnexReportSend = true;
					}
					
					if(now.equals(props.getProperty("GO2PLAY.StartTime"))){
						go2PlayReportSend = true;
					}
					
					//20180322 add
					if(now.equals(props.getProperty("App.StartTime"))){
						applicationReportSend = true;
					}
					
					//20180509 add
					if(now.equals(props.getProperty("FineMifi.StartTime"))){
						fineMifiReportSend = true;
					}
					
					//20180612 add
					if(now.equals(props.getProperty("YunYoBo.StartTime"))){
						yunYoBoReportSend = true;
					}
					
					//20180731 add
					if(now.equals(props.getProperty("CCT.StartTime"))){
						CCTReportSend = true;
					}
					
					//20180731 add
					if(now.equals(props.getProperty("FanTravel.StartTime"))){
						FanTravelReportSend = true;
					}
					
					
					
					if(testMode) { 
						logger.info("return");
						return;
					}
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
	

	
	@Override
	public void run(){
		while(!exit){
			logger.info("Test Mod t1: "+testMode);
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {}
			
			logger.info(" program2 running...");
			if(JoyReportSend){
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
			
			if(go2PlayReportSend){
				logger.info("GO2PLAY Report starting...");
				try {
					connectDB();
					go2PlayReport();
					go2PlayReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send GO2PLAY Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("GO2PLAY Report end...");
			}

			if(USReportSend){
				logger.info("US Report starting...");
				try {
					connectDB();
					sendUSReport();
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
			if(CRMReportSend){
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
			
			if(SMSReportSend){
				logger.info("SMS Report starting...");
				try {
					connectNobillDB();
					sendSMSReport();
					SMSReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send SMS Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("SMS Report end...");
			}
			
			if(AnnexReportSend){
				logger.info("SMS Annex starting...");
				try {
					connectDB();
					sendAnnexReport();
					AnnexReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send SMS Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("SMS Report end...");
			}
			//20180322 add
			if(applicationReportSend){
				logger.info("Application report starting...");
				try {
					connectDB();
					applicationDailyReport();
					applicationReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send Application Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("Application report end...");
			}
			//20180509 add
			if(fineMifiReportSend){
				logger.info("FineMi report starting...");
				try {
					connectDB();
					fineMifiReport();
					fineMifiReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send FineMi Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("FineMi report end...");
			}
			
			//20180312 add
			if(yunYoBoReportSend){
				logger.info("YunYoBo report starting...");
				try {
					connectDB();
					yunYoBoReport();
					yunYoBoReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send YunYoBo Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("YunYoBo report end...");
			}
			
			//20180731 add
			if(CCTReportSend){
				logger.info("CCT report starting...");
				try {
					connectDB();
					CCTReport();
					CCTReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send CCT Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("CCT report end...");
			}
			
			//20181008 add
			if(FanTravelReportSend){
				logger.info("FanTravel report starting...");
				try {
					connectDB();
					FanTravelReport();
					FanTravelReportSend = false;
				} catch (Exception e) {
					ErrorHandle("Can't send FanTravel Report!",e);
				}finally{
					try {
						if(conn!=null) conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("FanTravel report end...");
			}
			
			
			if(testMode) { 
				logger.info("return");
				return;
			}
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	public void sendSMSReport() throws Exception{
		//取得前一天的日期
		String dateS = sdf3.format(new Date(new Date().getTime()-24*60*60*1000));
		String fileName = "SoftleaderOK"+dateS+".xlsx";
		List<Map<String,String>> head = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		List<Map<String,Integer>> size = new ArrayList<Map<String,Integer>>();
		
		
		Map<String,Integer> mz = null;
		mz = new HashMap<String,Integer>();
		mz.put("number", 0);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 1);
		mz.put("width",15);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 2);
		mz.put("width",20);
		size.add(mz);
		
		Map<String,String> m = null;
		
		m = new HashMap<String,String>();
		m.put("name", "Time");
		m.put("col", "time");
		head.add(m);
		
		m = new HashMap<String,String>();
		m.put("name", "Destination");
		m.put("col", "dest");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Originator/Recipient");
		m.put("col", "number");
		head.add(m);

		m = new HashMap<String,String>();
		m.put("name", "Source");
		m.put("col", "source");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Type");
		m.put("col", "type");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "State");
		m.put("col", "state");
		head.add(m);
		
		String sql = "SELECT TO_CHAR(TIMESTAMP,'yyyy-mm-dd hh24:mi:ss.ff3') TIME,  "
				+ "			 destaddrvalue DEST,  "
				+ "			 origorrecipaddrvalue NUM,  "
				+ "			 sourcename SOURCE,   "
				+ "			DECODE(TYPE,0,'Submit',1,'Status report') TYPE, "
				+ "			DECODE(state,0,'Initial',    1,'Enroute',     2,'Delivered',     3,'Expired',     4,'Deleted',     5,'Undeliverable',     "
				+ "									6,'Accepted',     7,'Unknown',     8,'Rejected',     9,'Delivered direct') STATE "
				+ "FROM nobill.smcdr "
				+ "WHERE sourcename='SoftLeader-in'   AND TO_CHAR(TIMESTAMP,'yyyymmdd') = '"+dateS+"' AND state in (2,9) "
				+ "ORDER BY TIMESTAMP ";
		
		Statement st = conn.createStatement();
		logger.info("Execute SQL:"+sql);
		ResultSet rs = st.executeQuery(sql);
		int i = 0;
		while(rs.next()){
			Map<String,Object> md = new HashMap<String,Object>();
			md.put("time", rs.getString("TIME"));
			md.put("dest", rs.getString("DEST"));
			md.put("number", rs.getString("NUM"));
			md.put("source", rs.getString("SOURCE"));
			md.put("type", rs.getString("TYPE"));
			md.put("state", rs.getString("STATE"));
			data.add(md);
			i++;
		}
		
		logger.info("Create File "+fileName);
		Workbook wb = createExcel(head,data,"xlsx",size);
		File f = new File(fileName);
		FileOutputStream fo = new FileOutputStream(f);
		wb.write(fo);
		fo.close();
		logger.info("Create File End...");
		
		/*FTPClient ftp = null;
		
		try {
			
			if(testMode)
				ftp = connectFTP(props.getProperty("test.FTP.host"),props.getProperty("test.FTP.username"),props.getProperty("test.FTP.password"),props.getProperty("test.FTP.dest"));
			else
				ftp = connectFTP(props.getProperty("FTP.host"),props.getProperty("FTP.username"),props.getProperty("FTP.password"),props.getProperty("FTP.dest"));
			UpdatToFTP(ftp,fileName,fileName);
		} finally {
			if(ftp!=null) ftp.disconnect();
		}		*/
		
		String subject = "SoftLeader簡訊發送結果"+dateS,mailReceiver=props.getProperty("SMS.recevier");
		String mailContent = "Softleader 簡訊發送成功筆數為 "+i+" 筆";
		if(testMode || mailReceiver == null || "".equals(mailReceiver)){
			mailReceiver = props.getProperty("default.recevier");
			subject = "test report";
		}
		sendMail(subject, mailContent, "SoftLeader_Report", mailReceiver);
		
	}
	
	public FTPClient connectFTP(String host,String username,String password,String dest) throws Exception{
		

		FTPClient ftp = new FTPClient();
		
		logger.info("connect to FTP : "+host);
		//建立連線
		ftp.connect(host);
 
		//登入
		if (!ftp.login(username, password)) {
			ftp.logout();
			throw new Exception("FTP登入失敗");
		}
		//取得回應碼
		int reply = ftp.getReplyCode();

		System.out.println("reply:"+reply);
		//登入狀態
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("FTP無回應");
		}           
  
		//FTP改為被動模式
		ftp.enterLocalPassiveMode();
   
		//改路徑
		ftp.changeWorkingDirectory(dest);   
   
		logger.info("connect Ftp Success!");
		
		return ftp;
	}

	public void UpdatToFTP(FTPClient ftp,String localFileName,String destFileName) throws IOException{

		logger.info("Updating...");
		FileInputStream fis = null;
		 try {
			fis =  new FileInputStream(localFileName); 
			 
			ftp.setBufferSize(1024);  
			//ftp.setControlEncoding("big5");
			// 设置文件类型（二进制）  
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);  

			
			if(ftp.storeFile(destFileName, fis)){
				logger.info("Update Success!");
			}else{
				logger.info("Update fail!");
			}
		}finally{
			logger.info("close FTP...");
			if(fis!=null) fis.close();
		}
	}
	
	public  void sendCRMReport() throws AddressException, MessagingException, IOException{
		
		Date now = new Date();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(now);
		String fileName = "nameBinding_"+sDate+".xlsx";
		List<Map<String,String>> head = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		List<Map<String,Integer>> size = new ArrayList<Map<String,Integer>>();
		
		
		Map<String,Integer> mz = null;
		mz = new HashMap<String,Integer>();
		mz.put("number", 0);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 1);
		mz.put("width",15);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 2);
		mz.put("width",20);
		size.add(mz);
		
		
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
		
		m= new HashMap<String,String>();
		m.put("name", "中華號");
		m.put("col", "chtMsisdn");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "備註");
		m.put("col", "remark");
		head.add(m);
		
		
		String mailContent = "實名制Report:\n\n";
		
		//Set<String> canceledServiceid = new HashSet<String>();
		Statement st = null;
		ResultSet rs = null;
		try {

			conn3 = DriverManager.getConnection("jdbc:mysql://10.42.1.199:3306/CRM_DB?characterEncoding=utf8", "crmuser", "crm");
			st = conn3.createStatement();
			
			String sql = "select serviceid,name,id,type,vln,msisdn,remark "
					+ "from CRM_DB.CRM_NAME_VERIFIED "
					+ "where vln like '86%' and (send_date is null or send_date ='')  ";
			
			logger.info("Execute SQL:"+sql);
			rs =st.executeQuery(sql);
			logger.info("Query End!");
			
			while(rs.next()){
				Map<String,Object> m2 = new HashMap<String,Object>();
				m2.put("name", rs.getString("name"));
				m2.put("id", rs.getString("id"));
				m2.put("type", rs.getString("type"));
				m2.put("chinaMsisdn", rs.getString("vln"));
				m2.put("location", "台湾");
				m2.put("chtMsisdn", rs.getString("msisdn"));
				m2.put("remark", rs.getString("remark"));
				data.add(m2);
			}
			rs.close();
			logger.info("Create File "+fileName);
			Workbook wb = createExcel(head,data,"xlsx",size);
			File f = new File(fileName);
			FileOutputStream fo = new FileOutputStream(f);
			wb.write(fo);
			fo.close();
			
			logger.info("Create File End...");
			
			//將已退租的更改為歷史
			//20170220 del
		/*	sql = "select serviceid from service A where to_char(A.datecanceled,'yyyyMMdd') <= '"+sDate+"' "
					+ "and to_char(A.datecanceled+3,'yyyyMMdd') >='"+sDate+"' ";
			st2 = conn.createStatement();
			logger.info("Execute SQL:"+sql);
			rs = st2.executeQuery(sql);
			
			while(rs.next()){
				canceledServiceid.add(rs.getString("serviceid"));
			}*/
			
			//String serviceidInQuery = "";
			/*Iterator<String> it = canceledServiceid.iterator();
			for(int i = 1 ;it.hasNext();i++){
				serviceidInQuery+= it.next();
				if(i>=1000){
					sql = "update CRM_DB.CRM_NAME_VERIFIED set status = '0' where status = '1' and serviceid in ( "+serviceidInQuery+" ) ";
					logger.info("Execute SQL:"+sql);
					st.executeUpdate(sql);
					serviceidInQuery = "";
				}else{
					serviceidInQuery+=",";
				}
			}
			
			if(!"".equals(serviceidInQuery)){
				sql = "update CRM_DB.CRM_NAME_VERIFIED set status = '0' where status = '1' and serviceid in ( "+serviceidInQuery.substring(0,serviceidInQuery.length()-1)+" ) ";
				logger.info("Execute SQL:"+sql);
				st.executeUpdate(sql);
				serviceidInQuery = "";
			}*/
			
			mailContent += "重複驗證的中國號:"+"\n";
			mailContent += "\t"+"中國號"+"\t"+"數量"+"\n";
			//20161129 驗證已import內容
			//重複的中國號
			sql = "select vln,count(1) CD from CRM_DB.CRM_NAME_VERIFIED where status = 1 group by vln having count(1)>1 ";
			logger.info("Execute SQL:"+sql);
			rs =st.executeQuery(sql);
			
			while(rs.next()){
				mailContent += "\t"+rs.getString("vln")+"\t"+rs.getString("CD")+"\n";
			}
			
			/*mailContent += "\n";
			mailContent += "重複的中華號資料:"+"\n";
			mailContent += "\t"+"中華號"+"\t"+"數量"+"\n";
			//重複的中華號
			sql = "select chtMsisdn,count(1) CD from CRM_DB.CRM_NAME_VERIFIED where status = 1  group by chtMsisdn 	having count(1)>1 ";
			logger.info("Execute SQL:"+sql);
			rs =st.executeQuery(sql);
			
			while(rs.next()){
				mailContent += "\t"+rs.getString("chtMsisdn")+"\t"+rs.getString("CD")+"\n";
			}*/
			
			mailContent += "\n";
			mailContent += "一證件認證超過5個號碼:"+"\n";
			mailContent += "\t"+"證號"+"\t"+"數量"+"\n";
			//重複數大於5的證號
			sql = "select id,count(1) CD from CRM_DB.CRM_NAME_VERIFIED where status = 1 group by id having count(1)>5 ";
			logger.info("Execute SQL:"+sql);
			rs =st.executeQuery(sql);
			
			while(rs.next()){
				mailContent += "\t"+rs.getString("id")+"\t"+rs.getString("CD")+"\n";
			}
			
			mailContent += "\n";
			mailContent += "同證號不同名字:"+"\n";
			mailContent += "\t"+"證號"+"\t"+"名稱1"+"\t"+"名稱2"+"\n";
			//同名不同證號
			sql = "select distinct A.name AN,A.id AD,B.name BN from CRM_DB.CRM_NAME_VERIFIED A inner join CRM_DB.CRM_NAME_VERIFIED B on A.id = B.id "
					+ "where A.status=1 and B.status=1 and (A.name<>B.name or A.type<>B.type) order by A.id " ;
			logger.info("Execute SQL:"+sql);
			rs =st.executeQuery(sql);
			
			while(rs.next()){
				mailContent += "\t"+rs.getString("AD")+"\t"+rs.getString("AN")+"\t"+rs.getString("BN")+"\n";
			}
			
			
			//更新SendDate
			sql  = "update CRM_DB.CRM_NAME_VERIFIED set send_date = '"+new SimpleDateFormat("yyyy/MM/dd").format(now)+"' "
					+ "where vln like '86%' and (send_date is null or send_date ='') ";
			logger.info("Execute SQL:"+sql);
			st.executeUpdate(sql);
			
			//備份資料庫
			sql  = "delete from CRM_DB.CRM_NAME_VERIFIED_BAK ";
			logger.info("Execute SQL:"+sql);
			st.executeUpdate(sql);
			sql  = "insert into CRM_DB.CRM_NAME_VERIFIED_BAK select * from CRM_DB.CRM_NAME_VERIFIED ";
			logger.info("Execute SQL:"+sql);
			st.executeUpdate(sql);
			
			
			
		} catch (SQLException e) {
			ErrorHandle(e);
		} catch (IOException e) {
			ErrorHandle(e);
		}finally{
			try {
				if(st!=null) st.close();
				if(conn3!=null) conn3.close();
			} catch (SQLException e) {	}
		}
		
		String subject = "實名制Report",mailReceiver=props.getProperty("CRM.recevier");
		if(testMode || mailReceiver == null || "".equals(mailReceiver)){
			mailReceiver = props.getProperty("default.recevier");
			//subject = "Joy default Report";
		}
		//sendMail(subject,mailContent, "CRM_Report", mailReceiver,fileName);
		sendMailwithAuthenticator(subject,mailContent, "CRM_Report", mailReceiver,fileName);
	}
	
	public static Workbook createExcel(List<Map<String,String>> head,List<Map<String,Object>> data,String type) throws IOException{
		
		return createExcel(head, data,type,null) ;
		
	}
	public static Workbook createExcel(List<Map<String,String>> head,List<Map<String,Object>> data,String type,List<Map<String,Integer>> size) throws IOException{
		Workbook wb = null;
		int rowN = 0;
		int sheetN = 0;
		//建立xls檔案
		if(type.matches("^xls$")){
			wb = new HSSFWorkbook();  
			HSSFSheet sheet = (HSSFSheet) wb.createSheet("sheet"+sheetN++);  
			if(size!=null){
				for(Map<String,Integer> m : size){
					sheet.setColumnWidth( m.get("number"),m.get("width")*256);
				}
			}
			
			/*sheet.setColumnWidth(0, 20*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 20*256);*/
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
			if(size!=null){
				for(Map<String,Integer> m : size){
					sheet.setColumnWidth(m.get("number"),m.get("width")*256);
				}
			}
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
	public static void ErrorHandle(Exception e){
		ErrorHandle(null,e);
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
	public boolean createSheetFile(String fileName,List<Map<String,String>> head,List<Map<String,Object>> data,double total){
		
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
				if(head.get(col).get("size")!=null)
					sheet.setColumnWidth(col, 256*Integer.valueOf(head.get(col).get("size")));
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
			
			if(total!=0){
				//20161201 增加總量
				row = sheet.createRow(rowN++);
				row.createCell(2).setCellValue("total");
				row.createCell(3).setCellValue(total);
				row.createCell(4).setCellValue("MB");
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
		
		//sendMailwithAuthenticator("JoyTest","TestMail", "Joy_Report", props.getProperty("default.recevier"),null);
		
		String imsiStart = props.getProperty("Joy.IMSI.start");
		String imsiEnd = props.getProperty("Joy.IMSI.end");
		
		if("".equals(imsiStart)||"".equals(imsiEnd))
			throw new Exception("no efficient imsi set.");
			
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf3.format(c.getTime());
		
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		double total = 0;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "Joy-TWdata"+today.replace("/", "")+".xls";
			
			st = conn.createStatement();
			//20161201 新增ICCID資料
			Map<String,String> iccidMap = new HashMap<String,String>();
			sql = "select serviceid,imsi,iccid from imsi A where IMSI>'"+imsiStart+"' AND IMSI <='"+imsiEnd+"' ";
					//+ "serviceid in (select serviceid from service where priceplanid = 167) ";
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()){
				iccidMap.put(rs.getString("imsi"), rs.getString("iccid"));
			}
			
			rs.close();
			
			/*sql = "SELECT IMSI, MIN(SUBSTR(CALLTIME,1,10)) START_DATE, SUM(DATAVOLUME)/1024/1024 VOLUME_MB "
					+ "FROM HUR_DATA_USAGE "
					+ "WHERE IMSI>'"+imsiStart+"' AND IMSI <='"+imsiEnd+"' "
					+ "AND SUBSTR(CALLTIME,1,10)<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";*/
			//因Hur_data_usage可能搬移資料，改以日累計作為統計來源
			sql = "SELECT A.IMSI, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB "
					+ "from ("
					+ "			SELECT A.serviceid , NEWIMSI IMSI "
					+ "			from ( "
					+ "						SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "						FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "						WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "						AND b.serviceid=c.serviceid "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "						UNION ALL "
					+ "						SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "						WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "						AND B.SERVICEID=C.SERVICEID "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "						AND a.oldvalue <> a.newvalue) A, "
					+ "					("
					+ "						SELECT SERVICEID,MAX(COMPLETEDATE) COMPLETEDATE "
					+ "						FROM ( "
					+ "									SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "									FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "									WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "									AND b.serviceid=c.serviceid "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "									UNION ALL "
					+ "									SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "									FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "									WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "									AND B.SERVICEID=C.SERVICEID "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "									AND a.oldvalue <> a.newvalue )"
					+ "						GROUP BY SERVICEID) B"
					+ "			WHERE A.SERVICEID = B.SERVICEID AND A.COMPLETEDATE = B.COMPLETEDATE ) A  , "
					+ "			HUR_CURRENT_DAY B "
					+ "WHERE A.SERVICEID = B.SERVICEID "
					+ "AND A.IMSI>'"+imsiStart+"' AND A.IMSI <='"+imsiEnd+"' "
					+ "AND B.DAY >= '20161001' AND B.DAY<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";
			
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
			
				Map<String,Object> m = new HashMap<String,Object>();
				String imsi = rs.getString("IMSI");
				String iccid =  iccidMap.get(imsi);
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", imsi);
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",iccid);
				data.add(m);
				total+= volume;
			}
			//createFile(csvName,csvContent);
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("IMSI","IMSI"));
			head.add(mapSetting("START_DATE","START_DATE"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
			head.add(mapSetting("ICCID","ICCID"));
		
			createSheetFile(csvName,head,data,total);
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
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "Joy_Report", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	
	public void go2PlayReport() throws Exception{
		
		//sendMailwithAuthenticator("JoyTest","TestMail", "Joy_Report", props.getProperty("default.recevier"),null);
		
		String imsiStart = props.getProperty("GO2PLAY.IMSI.start");
		String imsiEnd = props.getProperty("GO2PLAY.IMSI.end");
		
		if("".equals(imsiStart)||"".equals(imsiEnd))
			throw new Exception("no efficient imsi set.");
			
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf3.format(c.getTime());
		
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		double total = 0;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "GO2PLAY-data"+today.replace("/", "")+".xls";
			
			st = conn.createStatement();
			/*//20161201 新增ICCID資料
			Map<String,String> iccidMap = new HashMap<String,String>();
			sql = "select serviceid,imsi,iccid from imsi A "
					+ "where (IMSI>='454120290050007' AND IMSI <='454120290057006') "
					+ "OR  (IMSI>='454120290066022' AND IMSI <='454120290067021') "
					+ "OR  (IMSI>='454120290075022' AND IMSI <='454120290076021') "
					+ "";
					//+ "serviceid in (select serviceid from service where priceplanid = 167) ";
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()){
				iccidMap.put(rs.getString("imsi"), rs.getString("iccid"));
			}
			
			rs.close();
			
			sql = "SELECT IMSI, MIN(SUBSTR(CALLTIME,1,10)) START_DATE, SUM(DATAVOLUME)/1024/1024 VOLUME_MB "
					+ "FROM HUR_DATA_USAGE "
					+ "WHERE IMSI>'"+imsiStart+"' AND IMSI <='"+imsiEnd+"' "
					+ "AND SUBSTR(CALLTIME,1,10)<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";*/
			//因Hur_data_usage可能搬移資料，改以日累計作為統計來源
			/*sql = "SELECT A.IMSI, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB "
					+ "from ("
					+ "			SELECT A.serviceid , NEWIMSI IMSI "
					+ "			from ( "
					+ "						SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "						FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "						WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "						AND b.serviceid=c.serviceid "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "						UNION ALL "
					+ "						SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "						WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "						AND B.SERVICEID=C.SERVICEID "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "						AND a.oldvalue <> a.newvalue) A, "
					+ "					("
					+ "						SELECT SERVICEID,MAX(COMPLETEDATE) COMPLETEDATE "
					+ "						FROM ( "
					+ "									SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "									FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "									WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "									AND b.serviceid=c.serviceid "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "									UNION ALL "
					+ "									SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "									FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "									WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "									AND B.SERVICEID=C.SERVICEID "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "									AND a.oldvalue <> a.newvalue )"
					+ "						GROUP BY SERVICEID) B"
					+ "			WHERE A.SERVICEID = B.SERVICEID AND A.COMPLETEDATE = B.COMPLETEDATE ) A  , "
					+ "			HUR_CURRENT_DAY B "
					+ "WHERE A.SERVICEID = B.SERVICEID "
					//+ "AND A.IMSI>='"+imsiStart+"' AND A.IMSI <='"+imsiEnd+"' "
					+ "AND ("
					+ "(IMSI>='454120290050007' AND IMSI <='454120290057006') "
					+ "OR  (IMSI>='454120290066022' AND IMSI <='454120290067021')"
					+ "OR  (IMSI>='454120290075022' AND IMSI <='454120290076021') "
					+ ") "
					+ "AND B.DAY >= '20180118' AND B.DAY<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";

			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
			
				Map<String,Object> m = new HashMap<String,Object>();
				String imsi = rs.getString("IMSI");
				String iccid =  iccidMap.get(imsi);
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", imsi);
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",iccid);
				data.add(m);
				total+= volume;
			}*/
			//createFile(csvName,csvContent);
			 
			//20180606 add
			/*sql = "select a.serviceid,a.imsi,a.iccid ,MIN(C.DAY) START_DATE, SUM(C.VOLUME)/1024/1024 VOLUME_MB,b.PRICEPLANID " 
					+ "from imsi a,service b,HUR_CURRENT_DAY c " 
					+ "where a.serviceid = b.serviceid and a.serviceid = c.serviceid(+) " 
					+ "and b.subsidiaryID = 78 " 
					+ "AND ((c.DAY >= '20180118' AND c.DAY<='"+today+"')) "
					+ "group by a.serviceid,a.imsi,a.iccid " 
					+ "order by START_DATE DESC ";*/
			
			/*sql = "select a.serviceid,a.imsi,a.iccid ,MIN(C.DAY) START_DATE, SUM(C.VOLUME)/1024/1024 VOLUME_MB "
					+ ",( CASE "
					+ "   WHEN b.PRICEPLANID = 182 then '9020-GO2PLAY-7D' "
					+ "   WHEN (a.imsi>=454120290056507 and a.imsi<= 454120290057006) then '9020-GO2PLAY-7D'  "
					+ "   WHEN (a.imsi>=454120290066022  and a.imsi<= 454120290066521 ) then '9020-GO2PLAY-7D' "
					+ "   else '9009-GO2PLAY'  END ) PRICEPLAN "
					+ "from imsi a,service b,HUR_CURRENT_DAY c  "
					+ "where a.serviceid = b.serviceid and a.serviceid = c.serviceid(+)  "
					+ "and b.subsidiaryID = 78  "
					+ "AND ((c.DAY >= 20180118 AND c.DAY<="+today+"))  "
					+ "group by a.serviceid,a.imsi,a.iccid,b.priceplanid  "
					+ "order by PRICEPLAN ASC,START_DATE DESC ";*/
			
			/*sql = "select c.serviceid,d.imsi,d.iccid ,MIN(e.DAY) START_DATE, SUM(e.VOLUME)/1024/1024 VOLUME_MB ,"
					+ "( CASE  "
					+ "   WHEN c.PRICEPLANID = 182 then '9020-GO2PLAY-7D'  "
					+ "   WHEN c.PRICEPLANID = 183 then '9021-GO2PLAY-15D'  "
					+ "   WHEN (d.imsi>=454120290056507 and d.imsi<= 454120290057006) then '9020-GO2PLAY-7D'   "
					+ "   WHEN (d.imsi>=454120290066022  and d.imsi<= 454120290066521 ) then '9020-GO2PLAY-7D'  "
					+ "   else '9009-GO2PLAY'  END ) PRICEPLAN  "
					+ "FROM NEWSERVICEORDERINFO a, serviceorder b, service c ,imsi d,HUR_CURRENT_DAY e  "
					+ "WHERE a.fieldid=3713 AND a.orderid=b.orderid  "
					+ "AND b.serviceid=c.serviceid and a.fieldvalue = d.imsi  "
					+ "AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205'  "
					+ "AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "AND c.serviceid = e.serviceid "
					+ "and c.subsidiaryID = 78 "
					+ "AND ((e.DAY >= 20180118 AND e.DAY<='"+today+"')) "
					+ "group by c.serviceid,d.imsi,d.iccid,c.priceplanid  "
					+ "order by PRICEPLAN ASC,START_DATE DESC,d.imsi asc  ";*/
			
			sql = "select a.serviceid,C.imsi,C.iccid ,MIN(b.DAY) START_DATE, SUM(b.VOLUME)/1024/1024 VOLUME_MB , "
					+ "case	WHEN (c.imsi>=454120290056507 and c.imsi<= 454120290057006) then 'D-Go2Play-07D'    "
					+ "     WHEN (c.imsi>=454120290066022 and c.imsi<= 454120290066521) then 'D-Go2Play-07D'   "
					+ "     else E.NAME END PRICEPLAN "
					+ "from service a,HUR_CURRENT_DAY b,IMSI C,"
					+ "		( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
					+ "			from ( "
					+ "					select B.SERVICEID,A.FIELDVALUE "
					+ "					FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
					+ "					where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "					AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205') A, "
					+ "				( "
					+ "					select B.SERVICEID,A.newvalue "
					+ "					FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
					+ "					where A.FIELDID=3713 AND A.ORDERID=B.ORDERID  "
					+ "					AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' ) B "
					+ "			where A.serviceid = B.serviceid(+)) D ,"
					+ "		PRICEPLAN E "
					+ "where A.SERVICEID =B.SERVICEID AND A.SERVICEID = D.SERVICEID "
					+ "AND C.IMSI = D.IMSI AND E.PRICEPLANID = a.priceplanid "
					+ "AND A.subsidiaryid = 78 "
					+ "AND ((b.DAY >= '20180118' AND b.DAY<='20181016'))   "
					+ "group by a.serviceid,C.imsi,C.ICCID,E.name "
					+ "order by PRICEPLAN ASC,START_DATE DESC,c.imsi asc ";
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				Map<String,Object> m = new HashMap<String,Object>();				
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				
				m.put("PRICEPLAN",rs.getString("PRICEPLAN"));
				
				data.add(m);
				total+= volume;
			}
			
			
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("IMSI","IMSI"));
			head.add(mapSetting("START_DATE","START_DATE"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
			head.add(mapSetting("ICCID","ICCID","40"));
			head.add(mapSetting("PRICEPLAN","PRICEPLAN"));
		
			createSheetFile(csvName,head,data,total);
			String mailReceiver = props.getProperty("GO2PLAY.recevier");
			String subject = "GO2PLAY daily report-"+today.replace("/", "");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Dear GO2PLAY colleagues,\n"
					+ "\n"
					+ "Please see the daily report.\n"
					+ "Thank you very much.\n"
					+ "\n"
					+ "Sim2travel Inc.";
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "GO2PLAY_Report", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	//20180509
	public void fineMifiReport() throws Exception{

		String subsidiaryID = props.getProperty("FineMifi.subsidiaryID");
		
		if("".equals(subsidiaryID))
			throw new Exception("FineMifi no efficient subsidiaryID set.");
			
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf3.format(c.getTime());
		
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		double total = 0;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB, MCCMNC";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "FineMifi-data"+today.replace("/", "")+".xls";
			
			st = conn.createStatement();
			/*//20161201 新增ICCID資料
			Map<String,String> iccidMap = new HashMap<String,String>();
			sql = "select serviceid,imsi,iccid from imsi A where A.serviceid in (select serviceid from service where subsidiaryID = "+subsidiaryID+") ";
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()){
				iccidMap.put(rs.getString("imsi"), rs.getString("iccid"));
			}
			
			rs.close();
			
			sql = "SELECT IMSI, MIN(SUBSTR(CALLTIME,1,10)) START_DATE, SUM(DATAVOLUME)/1024/1024 VOLUME_MB "
					+ "FROM HUR_DATA_USAGE "
					+ "WHERE IMSI>'"+imsiStart+"' AND IMSI <='"+imsiEnd+"' "
					+ "AND SUBSTR(CALLTIME,1,10)<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";
			//因Hur_data_usage可能搬移資料，改以日累計作為統計來源
			sql = "SELECT A.IMSI, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB "
					+ "from ("
					+ "			SELECT A.serviceid , NEWIMSI IMSI "
					+ "			from ( "
					+ "						SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "						FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "						WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "						AND b.serviceid=c.serviceid "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "						UNION ALL "
					+ "						SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "						WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "						AND B.SERVICEID=C.SERVICEID "
					+ "						AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "						AND a.oldvalue <> a.newvalue) A, "
					+ "					("
					+ "						SELECT SERVICEID,MAX(COMPLETEDATE) COMPLETEDATE "
					+ "						FROM ( "
					+ "									SELECT c.serviceid, c.servicecode, '' OldIMSI, a.fieldvalue NewIMSI, b.COMPLETEDATE, b.orderid, 'New' OrderType "
					+ "									FROM NEWSERVICEORDERINFO a, serviceorder b, service c "
					+ "									WHERE a.fieldid=3713 AND a.orderid=b.orderid "
					+ "									AND b.serviceid=c.serviceid "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(b.completedate,'yyyymmdd')>='20070205' "
					+ "									UNION ALL "
					+ "									SELECT c.SERVICEID, C.servicecode, a.oldvalue OldIMSI, A.NEWVALUE imsi, A.COMPLETEDATE, b.orderid, 'Change' OrderType "
					+ "									FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B, SERVICE C "
					+ "									WHERE A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
					+ "									AND B.SERVICEID=C.SERVICEID "
					+ "									AND TO_CHAR(c.dateactivated,'yyyymmdd')>='20070205' "
					+ "									AND TO_CHAR(a.completedate,'yyyymmdd')>='20070205' "
					+ "									AND a.oldvalue <> a.newvalue )"
					+ "						GROUP BY SERVICEID) B"
					+ "			WHERE A.SERVICEID = B.SERVICEID AND A.COMPLETEDATE = B.COMPLETEDATE ) A  , "
					+ "			HUR_CURRENT_DAY B "
					+ "WHERE A.SERVICEID = B.SERVICEID "
					+ "AND A.serviceid in (select serviceid from service where subsidiaryID = "+subsidiaryID+") "
					+ "AND B.DAY >= '20180118' AND B.DAY<='"+today+"' "
					+ "GROUP BY IMSI "
					+ "order by START_DATE DESC ";*/
			
			//20180606 add
			sql = "select a.serviceid,a.imsi,a.iccid ,MIN(C.DAY) START_DATE, SUM(C.VOLUME)/1024/1024 VOLUME_MB,d.COUNTRY " 
					+ "from imsi a,service b,HUR_CURRENT_DAY c,("
					+ "			Select serviceid,wm_concat(MCCMNC) COUNTRY "
					+ "			from ("
					+ "				select distinct serviceid,mccmnc"
					+ "				from HUR_CURRENT_DAY"
					+ " 			)	"
					+ "			GROUP by SERVICEID 		) d " 
					+ "where a.serviceid = b.serviceid and a.serviceid = c.serviceid(+) and a.serviceid = d.serviceid(+) " 
					+ "and b.subsidiaryID = 79 " 
					+ "AND ((c.DAY >= '20180118' AND c.DAY<='"+today+"') or c.day is null) "
					+ "group by a.serviceid,a.imsi,a.iccid,d.COUNTRY " 
					+ "order by iccid ";
			
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
/*				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
*/			
				Map<String,Object> m = new HashMap<String,Object>();
				/*String imsi = rs.getString("IMSI");
				String iccid =  iccidMap.get(imsi);
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", imsi);
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",iccid);*/
				
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				m.put("MCCMNC",rs.getString("COUNTRY"));
				data.add(m);
				total+= volume;
			}
			//createFile(csvName,csvContent);
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("ICCID","ICCID","25"));
			head.add(mapSetting("IMSI","IMSI","25"));
			head.add(mapSetting("START_DATE","START_DATE","15"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB","15"));
			head.add(mapSetting("MCCMNC","MCCMNC","15"));

			createSheetFile(csvName,head,data,total);
			String mailReceiver = props.getProperty("FineMifi.recevier");
			String subject = "FineMifi daily report-"+today.replace("/", "");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Dear FineMifi colleagues,\n"
					+ "\n"
					+ "Please see the daily report.\n"
					+ "Thank you very much.\n"
					+ "\n"
					+ "Sim2travel Inc.";
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "FineMifi_Report", mailReceiver,csvName);
			
			
			//20180817
			total = 0;
			sql = "select a.serviceid,a.imsi,a.iccid ,MIN(C.DAY) START_DATE, SUM(C.VOLUME)/1024/1024 VOLUME_MB,d.COUNTRY " 
					+ "from imsi a,service b,HUR_CURRENT_DAY c,("
					+ "			Select serviceid,wm_concat(MCCMNC) COUNTRY "
					+ "			from ("
					+ "				select distinct serviceid,mccmnc"
					+ "				from HUR_CURRENT_DAY"
					+ " 			)	"
					+ "			GROUP by SERVICEID 		) d " 
					+ "where a.serviceid = b.serviceid and a.serviceid = c.serviceid(+) and a.serviceid = d.serviceid(+) " 
					+ "and b.priceplanid = 174 " 
					+ "AND ((c.DAY >= '20180118' AND c.DAY<='"+today+"') or c.day is null) "
					+ "group by a.serviceid,a.imsi,a.iccid,d.COUNTRY " 
					+ "order by iccid ";
			
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				Map<String,Object> m = new HashMap<String,Object>();
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				m.put("MCCMNC",rs.getString("COUNTRY"));
				data.add(m);
				total+= volume;
			}
			
			csvName = "FineMifi-data-priceplan174-"+today.replace("/", "")+".xls";

			createSheetFile(csvName,head,data,total);			
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "FineMifi_Report_priceplan_174", mailReceiver,csvName);
			
			
			//20180817
			total = 0;
			sql = "select a.serviceid,a.imsi,a.iccid ,MIN(C.DAY) START_DATE, SUM(C.VOLUME)/1024/1024 VOLUME_MB,d.COUNTRY " 
					+ "from imsi a,service b,HUR_CURRENT_DAY c,("
					+ "			Select serviceid,wm_concat(MCCMNC) COUNTRY "
					+ "			from ("
					+ "				select distinct serviceid,mccmnc"
					+ "				from HUR_CURRENT_DAY"
					+ " 			)	"
					+ "			GROUP by SERVICEID 		) d " 
					+ "where a.serviceid = b.serviceid and a.serviceid = c.serviceid(+) and a.serviceid = d.serviceid(+) " 
					+ "and b.priceplanid = 180 " 
					+ "AND ((c.DAY >= '20180118' AND c.DAY<='"+today+"') or c.day is null) "
					+ "group by a.serviceid,a.imsi,a.iccid,d.COUNTRY " 
					+ "order by iccid ";
			
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				Map<String,Object> m = new HashMap<String,Object>();
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				m.put("MCCMNC",rs.getString("COUNTRY"));
				data.add(m);
				total+= volume;
			}
			csvName = "FineMifi-data-priceplan180-"+today.replace("/", "")+".xls";

			createSheetFile(csvName,head,data,total);			
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "FineMifi_Report_priceplan_180", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	
	//20180612 add
	public void yunYoBoReport() throws Exception{
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf3.format(c.getTime());
		
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		double total = 0;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB,ICCID,NAME";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "YunYoBo-data"+today.replace("/", "")+".xls";
			
			st = conn.createStatement();
			
			/*sql = "SELECT A.IMSI,A.ICCID, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB " + 
					"from imsi a,HUR_CURRENT_DAY b, service c " + 
					"WHERE a.serviceid = B.serviceid and a.serviceid = c.serviceid " + 
					"and c.priceplanid in ("+props.getProperty("YunYoBo.pricePlanID", "175,176")+") " +
					"and b.day <= '"+today+"' " +
					"group by c.priceplanid,a.imsi,A.ICCID " + 
					"order by c.priceplanid asc,START_DATE DESC ";*/
			
			/*sql = "select IMSI,ICCID,START_DATE,VOLUME_MB,b.NAME "
					+ "from ( "
					+ "SELECT A.IMSI,A.ICCID, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB ,c.priceplanid "
					+ "from imsi a,HUR_CURRENT_DAY b, service c  "
					+ "WHERE a.serviceid = B.serviceid and a.serviceid = c.serviceid   "
					+ "and c.priceplanid in ("+props.getProperty("YunYoBo.pricePlanID", "175,176,178,179")+") "
					+ "and b.day <= '"+today+"'  "
					+ "group by c.priceplanid,a.imsi,A.ICCID  "
					+ ") a,PRICEPLAN b "
					+ "where a.priceplanid = b.priceplanid "
					+ "order by a.priceplanid asc,START_DATE DESC";*/
			
			///20180807 add
			sql = "select IMSI,ICCID,START_DATE,VOLUME_MB,b.NAME,c.COUNTRY "
					+ "from ("
					+ "			SELECT A.SERVICEID,A.IMSI,A.ICCID, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB ,c.priceplanid "
					+ "			from imsi a,HUR_CURRENT_DAY b, service c "
					+ "			WHERE a.serviceid = B.serviceid and a.serviceid = c.serviceid "
					+ "			and c.priceplanid in ("+props.getProperty("YunYoBo.pricePlanID", "175,176,178,179,181,186,187,188")+") "
					+ "			and b.day <= '"+today+"'  "
					+ "			group by c.priceplanid,a.SERVICEID,a.imsi,A.ICCID "
					+ "		) a,PRICEPLAN b ,(	"
			+ "								Select serviceid,wm_concat(MCCMNC) COUNTRY "
			+ "								from ( "
			+ "										select distinct serviceid,substr(B.MCCMNC,0,3)||B.COUNTRY||'('||B.NETWORK||')' mccmnc "
			+ "										from HUR_CURRENT_DAY A,hur_MCCMNC B "
			+ "										where substr(A.MCCMNC,0,3) = substr(B.MCCMNC,0,3) "
			+ "										and substr(A.MCCMNC,4) = B.NETWORK "
			+ "										order by mccmnc ) "
			+ "								GROUP by SERVICEID "
			+ "								) c "
			+ "			where a.priceplanid = b.priceplanid and a.SERVICEID = c.SERVICEID "
			+ "			order by b.NAME asc,START_DATE DESC ";
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
			
				Map<String,Object> m = new HashMap<String,Object>();
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				m.put("NAME",rs.getString("NAME"));
				m.put("COUNTRY",rs.getString("COUNTRY"));
				data.add(m);
				total+= volume;
			}
			//createFile(csvName,csvContent);
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("IMSI","IMSI"));
			head.add(mapSetting("START_DATE","START_DATE"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
			head.add(mapSetting("ICCID","ICCID","30"));
			head.add(mapSetting("NAME","NAME","20"));
			head.add(mapSetting("COUNTRY","COUNTRY"));
		
			createSheetFile(csvName,head,data,total);
			String mailReceiver = props.getProperty("YunYoBo.recevier");
			String subject = "YunYoBo daily report-"+today.replace("/", "");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Dear YunYoBo colleagues,\n"
					+ "\n"
					+ "Please see the daily report.\n"
					+ "Thank you very much.\n"
					+ "\n"
					+ "Sim2travel Inc.";
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "YunYoBo_Report", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	
	//20180612 add
		public void CCTReport() throws Exception{

			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
			String today = sdf3.format(c.getTime());
			
			String sql = "";
			Statement st = null;
			ResultSet rs = null;
			double total = 0;
			try {
				String csvContent = "IMSI, START_DATE, VOLUME_MB,ICCID";
				//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
				String csvName = "CCT-data"+today.replace("/", "")+".xls";
				
				st = conn.createStatement();
				
				sql = ""
						+ "SELECT A.IMSI,A.ICCID, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB "
						+ "from imsi a,HUR_CURRENT_DAY b, service c   "
						+ "WHERE a.serviceid = B.serviceid and a.serviceid = c.serviceid    "
						+ "and c.subsidiaryID in ("+props.getProperty("CCT.subsiDiaryID", "81")+")  "
						+ "and b.day <= '"+today+"'   "
						+ "group by c.serviceid,a.imsi,A.ICCID   "
						+ "order by START_DATE DESC "
						+ "";
				
				logger.info("Execute SQL : "+sql);
				rs = st.executeQuery(sql);
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
				while(rs.next()){
					if(!"".equals(csvContent))
						csvContent+="\n";
					csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
						FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
				
					Map<String,Object> m = new HashMap<String,Object>();
					double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
					m.put("IMSI", rs.getString("IMSI"));
					m.put("START_DATE", rs.getString("START_DATE"));
					m.put("VOLUME_MB", FormatDouble(volume, null));
					m.put("ICCID",rs.getString("ICCID"));
					data.add(m);
					total+= volume;
				}
				//createFile(csvName,csvContent);
				
				List<Map<String,String>> head = new ArrayList<Map<String,String>>();
				head.add(mapSetting("IMSI","IMSI"));
				head.add(mapSetting("START_DATE","START_DATE"));
				head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
				head.add(mapSetting("ICCID","ICCID"));
			
				createSheetFile(csvName,head,data,total);
				String mailReceiver = props.getProperty("CCT.recevier");
				String subject = "CCT daily report-"+today.replace("/", "");
				if(testMode || mailReceiver == null || "".equals(mailReceiver)){
					mailReceiver = props.getProperty("default.recevier");
					//subject = "Joy default Report";
				}
				
				String mailContent = "Dear CCT colleagues,\n"
						+ "\n"
						+ "Please see the daily report.\n"
						+ "Thank you very much.\n"
						+ "\n"
						+ "Sim2travel Inc.";
				
				//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
				sendMailwithAuthenticator(subject,mailContent, "CCT_Report", mailReceiver,csvName);
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (st != null)
						st.close();
				} catch (Exception e) {	}
			}
		}
	
	private static void applicationDailyReport() throws SQLException {
		String sql = "select SERVICEID,MAX(VERIFIED_DATE) VERIFIED_DATE ,TYPE  " 
				+ " from CRM_APPLICATION "  
				+ " where DATE_FORMAT(VERIFIED_DATE, '%Y%m%d') = DATE_FORMAT(date_sub(curdate(),interval 1 day), '%Y%m%d') " 
				+ " group by SERVICEID,TYPE "
				+ " order by VERIFIED_DATE desc ";
		
		

		Statement st = null;
		Statement st2 = null;
		ResultSet rs = null;
		
		Map<String,Map<String,String>> resultMap = new HashMap<String,Map<String,String>>();
		
		try {
			conn3 = DriverManager.getConnection("jdbc:mysql://10.42.1.199:3306/CRM_DB?characterEncoding=utf8", "crmuser", "crm");
			st = conn3.createStatement();
			logger.info("Execute:"+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				
				Map<String,String> m = new HashMap<String,String>();
				m.put("VERIFIED_DATE", rs.getString("VERIFIED_DATE"));
				m.put("TYPE", rs.getString("TYPE"));
				resultMap.put(rs.getString("serviceid"), m);
				
			}
			
			String serviceids = "";
			
			for(String serviceid:resultMap.keySet()) {
				if(!"".equals(serviceids))
					serviceids += ",";
				serviceids+= serviceid;
			}
			if(!"".equals(serviceids)){
				//分成"還在使用中的"、"serviceCoder記錄中都以退租最大的"，取一(nvl(沒有正使用,找最大))，配出serviceCode
				String sql2 = "select A.serviceid,A.servicecode "
						+ "from service a,( "
						+ "select nvl(b.serviceid,a.serviceid) serviceid "
						+ "from (select servicecode,MAX(serviceid) serviceid "
						+ "from service "
						+ "group by servicecode ) A, "
						+ "(select servicecode,serviceid "
						+ "from service "
						+ "where DATECANCELED is null ) B "
						+ "where A.servicecode = B.servicecode(+) "
						+ "and A.serviceid in "
						+ "("+serviceids+"))b "
						+ "where a.serviceid = b.serviceid ";
				
				st2 = conn.createStatement();
				rs = null;
				logger.info("Execute:"+sql2);
				rs = st2.executeQuery(sql2);
				
				while(rs.next()) {
					String serviceid = rs.getString("SERVICEID");
					Map<String,String> m = resultMap.get(serviceid);
					m.put("MSISDN", rs.getString("SERVICECODE"));
					resultMap.put(serviceid, m);
				}
			}
			
			
			String applicationDate = sdf2.format(new Date(new Date().getTime()-24*60*60*1000));
			String mailContent = applicationDate+"申請書登記結果("+resultMap.size()+"筆)\n";
			
			mailContent += "S2T_MSISDN,VERIFIED_DATE,TYPE\n";
			for(String serviceid : resultMap.keySet()) {
				Map<String,String> m = resultMap.get(serviceid);
				mailContent += m.get("MSISDN")+"("+serviceid+"),"+m.get("VERIFIED_DATE")+","+m.get("TYPE")+"\n";
			}
			
			String mailReceiver = props.getProperty("App.recevier");
			String subject = applicationDate+"application result.";
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
			}
			sendMail(subject, mailContent, "Dailyreport_SERVER", mailReceiver, null);
			
		}finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {	}
			try {
				if (st != null)
					st.close();
			} catch (Exception e) {	}
			try {
				if (conn3 != null)
					conn3.close();
			} catch (Exception e) {	}
		}
	}
	
	//20180612 add
	public void FanTravelReport() throws Exception{
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
		String today = sdf3.format(c.getTime());
		
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		double total = 0;
		try {
			String csvContent = "IMSI, START_DATE, VOLUME_MB,ICCID,NAME";
			//String csvName = "Joy-TWdata"+today.replace("/", "")+".csv";
			String csvName = "FanTravel-data"+today.replace("/", "")+".xls";
			
			st = conn.createStatement();
			
			///20180807 add
			sql = "select IMSI,ICCID,START_DATE,VOLUME_MB,b.NAME,c.COUNTRY "
					+ "from ("
					+ "			SELECT A.SERVICEID,A.IMSI,A.ICCID, MIN(B.DAY) START_DATE, SUM(B.VOLUME)/1024/1024 VOLUME_MB ,c.priceplanid "
					+ "			from imsi a,HUR_CURRENT_DAY b, service c "
					+ "			WHERE a.serviceid = B.serviceid and a.serviceid = c.serviceid "
					+ "			and c.priceplanid in ("+props.getProperty("FanTravel.pricePlanID", "184,185")+") "
					+ "			and b.day <= '"+today+"'  "
					+ "			group by c.priceplanid,a.SERVICEID,a.imsi,A.ICCID "
					+ "		) a,PRICEPLAN b ,(	"
			+ "								Select serviceid,wm_concat(MCCMNC) COUNTRY "
			+ "								from ( "
			+ "										select distinct serviceid,substr(B.MCCMNC,0,3)||B.COUNTRY||'('||B.NETWORK||')' mccmnc "
			+ "										from HUR_CURRENT_DAY A,hur_MCCMNC B "
			+ "										where substr(A.MCCMNC,0,3) = substr(B.MCCMNC,0,3) "
			+ "										and substr(A.MCCMNC,4) = B.NETWORK "
			+ "										order by mccmnc ) "
			+ "								GROUP by SERVICEID "
			+ "								) c "
			+ "			where a.priceplanid = b.priceplanid and a.SERVICEID = c.SERVICEID "
			+ "			order by b.NAME asc,START_DATE DESC ";
			
			logger.info("Execute SQL : "+sql);
			rs = st.executeQuery(sql);
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				if(!"".equals(csvContent))
					csvContent+="\n";
				csvContent+=rs.getString("IMSI")+","+rs.getString("START_DATE")+","+
					FormatDouble(Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB")), null);
			
				Map<String,Object> m = new HashMap<String,Object>();
				double volume = Double.valueOf(rs.getString("VOLUME_MB")==null?"0":rs.getString("VOLUME_MB"));
				m.put("IMSI", rs.getString("IMSI"));
				m.put("START_DATE", rs.getString("START_DATE"));
				m.put("VOLUME_MB", FormatDouble(volume, null));
				m.put("ICCID",rs.getString("ICCID"));
				m.put("NAME",rs.getString("NAME"));
				m.put("COUNTRY",rs.getString("COUNTRY"));
				data.add(m);
				total+= volume;
			}
			//createFile(csvName,csvContent);
			
			List<Map<String,String>> head = new ArrayList<Map<String,String>>();
			head.add(mapSetting("IMSI","IMSI"));
			head.add(mapSetting("START_DATE","START_DATE"));
			head.add(mapSetting("VOLUME_MB","VOLUME_MB"));
			head.add(mapSetting("ICCID","ICCID","30"));
			head.add(mapSetting("NAME","NAME","20"));
			head.add(mapSetting("COUNTRY","COUNTRY"));
		
			createSheetFile(csvName,head,data,total);
			String mailReceiver = props.getProperty("FanTravel.recevier");
			String subject = "FanTravel daily report-"+today.replace("/", "");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Dear FanTravel colleagues,\n"
					+ "\n"
					+ "Please see the daily report.\n"
					+ "Thank you very much.\n"
					+ "\n"
					+ "Sim2travel Inc.";
			
			//sendMail(subject,mailContent, "Joy_Report", mailReceiver,csvName);
			sendMailwithAuthenticator(subject,mailContent, "FanTravel_Report", mailReceiver,csvName);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {	}
		}
	}
	
	private static Map<String,String> mapSetting(String name,String col,String size){
		Map<String, String> m = new HashMap<String,String>();
		
		m.put("name", name);
		m.put("col", col);
		
		if(size!=null)
			m.put("size", size);
		
		return m;
	}
	
	private static Map<String,String> mapSetting(String name,String col){
		return mapSetting(name,col,null);
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
	
	public void connectNobillDB() throws ClassNotFoundException, SQLException{
		String url=props.getProperty("nbill.URL")
				.replace("{{Host}}", props.getProperty("nbill.Host"))
				.replace("{{Port}}", props.getProperty("nbill.Port"))
				.replace("{{ServiceName}}", (props.getProperty("nbill.ServiceName")!=null?props.getProperty("nbill.ServiceName"):""))
				.replace("{{SID}}", (props.getProperty("nbill.SID")!=null?props.getProperty("nbill.SID"):""));
		
		conn=connDB(props.getProperty("nbill.DriverClass"), url, 
				props.getProperty("nbill.UserName"), 
				props.getProperty("nbill.PassWord")
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

	
	/**
	 * Annex Report
	 * 
	 * IMSI、Start date、Usage Date(使用日期)、Country、Volume(MB)小數2位、Price(USD)小數2位
	 */
	public void sendAnnexReport(){
		logger.info("sendAnnexReport...");
		
		Date now = new Date();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(now);
		
		String fileName = "Annex_"+sDate+".xlsx";
		List<Map<String,String>> head = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		List<Map<String,Integer>> size = new ArrayList<Map<String,Integer>>();
		
		
		Map<String,Integer> mz = null;
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 0);
		mz.put("width",25);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 1);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 2);
		mz.put("width",15);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 3);
		mz.put("width",20);
		size.add(mz);
		
		Map<String,String> m = null;
		
		m = new HashMap<String,String>();
		m.put("name", "ICCID");
		m.put("col", "iccid");
		head.add(m);
		
		m = new HashMap<String,String>();
		m.put("name", "IMSI");
		m.put("col", "imsi");
		head.add(m);
		
		m = new HashMap<String,String>();
		m.put("name", "Start Date");
		m.put("col", "sDate");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Usage Date");
		m.put("col", "uDate");
		head.add(m);

		m = new HashMap<String,String>();
		m.put("name", "Country");
		m.put("col", "country");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Volume(MB)");
		m.put("col", "volume");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Price(USD)");
		m.put("col", "price");
		head.add(m);		
		
		Statement st = null;
		ResultSet rs = null;
		
		try{
			st = conn.createStatement();

			String sql = "select A.IMSI,A.START_DATE,B.DAY,C.COUNTRY,round(B.VOLUME/1024/1024,2) VOLUME_MB,B.CHARGE PRICE,D.ICCID "
					+ "from HUR_VOLUME_POCKET A,HUR_CURRENT_DAY B,HUR_MCCMNC C,IMSI D "
					+ "where A.type = 2 and a.serviceid = B.serviceid and substr(B.MCCMNC,0,3) = substr(C.MCCMNC,0,3) and A.IMSI=D.IMSI ";
			
			logger.debug("SQL : "+sql);
			rs = st.executeQuery(sql);
			logger.info("Query end!");
			
			while(rs.next()){
				Map<String,Object> md = new HashMap<String,Object>();
				md.put("iccid", rs.getString("ICCID"));
				md.put("imsi", rs.getString("IMSI"));
				md.put("sDate", rs.getString("START_DATE"));
				md.put("uDate", rs.getString("DAY"));
				md.put("country", rs.getString("COUNTRY"));
				md.put("volume", rs.getString("VOLUME_MB"));
				md.put("price", rs.getString("PRICE"));
				data.add(md);	
			}
			
			logger.info("Create File "+fileName);
			Workbook wb = createExcel(head,data,"xlsx",size);
			File f = new File(fileName);
			FileOutputStream fo = new FileOutputStream(f);
			wb.write(fo);
			fo.close();
			logger.info("Create File End...");
			
			String subject = "AnnexReport",mailReceiver=props.getProperty("Annex.recevier");
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				//subject = "Joy default Report";
			}
			
			String mailContent = "Annex Report at "+now;
			//sendMail(subject,mailContent, "CRM_Report", mailReceiver,fileName);
			sendMailwithAuthenticator(subject,mailContent, "Annex_Report", mailReceiver,fileName);
			
			
		} catch (SQLException e) {
			ErrorHandle("At set sendAnnexReport Got a SQLException", e);
		} catch (IOException e) {
			ErrorHandle("At set sendAnnexReport Got a IOException", e);
		} catch (AddressException e) {
			ErrorHandle("At set sendAnnexReport Got a AddressException", e);
		} catch (MessagingException e) {
			ErrorHandle("At set sendAnnexReport Got a MessagingException", e);
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
	
	
	
	public void sendUSReport(){
		logger.info("sendUSReport...");
		
		Date now = new Date();
		String sDate = new SimpleDateFormat("yyyyMMdd").format(now);
		
		String fileName = "USPackage_"+sDate+".xlsx";
		List<Map<String,String>> head = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		List<Map<String,Integer>> size = new ArrayList<Map<String,Integer>>();
		
		
		Map<String,Integer> mz = null;
		mz = new HashMap<String,Integer>();
		mz.put("number", 0);
		mz.put("width",15);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 1);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 2);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 4);
		mz.put("width",25);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 5);
		mz.put("width",25);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 6);
		mz.put("width",20);
		size.add(mz);
		
		mz = new HashMap<String,Integer>();
		mz.put("number", 7);
		mz.put("width",15);
		size.add(mz);
		
		Map<String,String> m = null;
		
		m = new HashMap<String,String>();
		m.put("name", "Msisdn");
		m.put("col", "msisdn");
		head.add(m);
		
		m = new HashMap<String,String>();
		m.put("name", "Package Start Date");
		m.put("col", "psDate");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Package End Date");
		m.put("col", "peDate");
		head.add(m);

		m = new HashMap<String,String>();
		m.put("name", "Alerted");
		m.put("col", "alerted");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Addon Service Start Date");
		m.put("col", "asDate");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "Addon Service End Date");
		m.put("col", "aeDate");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "In Period Volume(MB)");
		m.put("col", "iVolume");
		head.add(m);
		
		m= new HashMap<String,String>();
		m.put("name", "In Period Days");
		m.put("col", "iDays");
		head.add(m);
		
		Statement st = null;
		ResultSet rs = null;
		
		
		
		try{
			st = conn.createStatement();
			
			
			
			String sql= "";
					
			/*//統計每月有在美國使用總天數與總量，MO日期、CD天數、SU量
			sql = "select A.SERVICEID,substr(A.day,0,6) MO,count(1) CD,sum(A.volume) SU "
					+ "from HUR_CURRENT_DAY A "
					+ "where A.MCCMNC like '310%' "
					+ "group by A.Serviceid,substr(A.day,0,6)";
			
			Map<String,Map<String,Map<String,String>>> totalCount = new HashMap<String,Map<String,Map<String,String>>>();
			
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
			}*/
			
			rs = null;
			
			//統計每月在美國流量包中使用天數與總量，PID、CD天數、SU量
			Map<String,Map<String,String>> subCount = new HashMap<String,Map<String,String>>();
			
			sql=
					"select A.PID,count(1) CD ,sum(B.s) SU "
					+ "from HUR_VOLUME_POCKET A,(	select B.Serviceid,B.day,sum(B.volume) s "
					+ "								from HUR_CURRENT_DAY B "
					+ "								where B.MCCMNC like '310%' and B.day <= "+sDate+" "
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
			
			sql=
					/*"SELECT A.SERVICEID,A.PID,B.FOLLOWMENUMBER CHTMSISDN,A.SERVICEID,A.MCC,A.ALERTED,A.ID,A.CALLER_NAME,A.CUSTOMER_NAME,A.PHONE_TYPE,A.EMAIL,A.CANCEL_REASON, "
					+ "A.START_DATE,A.END_DATE,"
					+ "TO_CHAR(A.CREATE_TIME,'yyyy/MM/dd hh24:mi:ss') CREATE_TIME,TO_CHAR(A.CANCEL_TIME,'yyyy/MM/dd hh24:mi:ss') CANCEL_TIME "
					+ "from HUR_VOLUME_POCKET A,FOLLOWMEDATA B "
					+ "WHERE A.SERVICEID = B.SERVICEID(+) AND A.TYPE=0 AND B.FOLLOWMENUMBER like '886%' "
					+ "ORDER BY A.START_DATE DESC ";*/
					
					//20170704 new
					"SELECT A.SERVICEID,A.PID,B.FOLLOWMENUMBER CHTMSISDN,A.SERVICEID,A.MCC,A.ALERTED,A.ID, A.TYPE,"
					+ "A.START_DATE,A.END_DATE,C.STARTDATE CREATE_TIME,C.ENDDATE CANCEL_TIME,C.SERVICECODE "
					+ "from HUR_VOLUME_POCKET A,FOLLOWMEDATA B ,ADDONSERVICE_N C "
					+ "WHERE A.SERVICEID = B.SERVICEID(+) and a.serviceid = c.serviceid and C.SERVICECODE = 'SX003'  "
					+ "AND A.TYPE=0 AND B.FOLLOWMENUMBER like '886%' "
					+ "and A.START_DATE>=to_char(C.STARTDATE,'yyyyMMdd') "
					+ "and ( C.ENDDATE is null or A.START_DATE<=to_char(C.ENDDATE,'yyyyMMdd')) "
					+ "ORDER BY CREATE_TIME DESC ";
			
			logger.debug("SQL : "+sql);
			rs = st.executeQuery(sql);
			logger.info("Query end!");

			while(rs.next()){
				String pid = rs.getString("PID");
				
				int inDay = (subCount.get(pid)!=null?Integer.parseInt(subCount.get(pid).get("DAY")):0);
				double inVolume = (subCount.get(pid)!=null?Double.parseDouble(subCount.get(pid).get("VOLUME")):0.d);
				
				Map<String,Object> md = new HashMap<String,Object>();
				md.put("msisdn", rs.getString("CHTMSISDN"));
				md.put("psDate", rs.getString("START_DATE"));
				md.put("peDate", rs.getString("END_DATE"));
				md.put("alerted", rs.getString("ALERTED"));
				md.put("asDate", rs.getString("CREATE_TIME"));
				md.put("aeDate", rs.getString("CANCEL_TIME"));
				md.put("iVolume", inVolume);
				md.put("iDays", inDay);
				data.add(md);	
				
			}
			
			

		
			String mailReceiver = props.getProperty("US.recevier");
			String subject = "美國流量包 Report";
			if(testMode || mailReceiver == null || "".equals(mailReceiver)){
				mailReceiver = props.getProperty("default.recevier");
				subject = "美國流量包 Deafult Report";
			}
			
			logger.info("Create File "+fileName);
			Workbook wb = createExcel(head,data,"xlsx",size);
			File f = new File(fileName);
			FileOutputStream fo = new FileOutputStream(f);
			wb.write(fo);
			fo.close();
			logger.info("Create File End...");

			String mailContent = "US PacketReport at "+now;
			//sendMail(subject,mailContent, "CRM_Report", mailReceiver,fileName);
			sendMailwithAuthenticator(subject,mailContent, "USPacketReport", mailReceiver,fileName);
			
			
			//sendMail("美國流量包Report", report, "DVRS Report", "Galen.Kao@sim2travel.com,douglas.chuang@sim2travel.com,yvonne.lin@sim2travel.com,ranger.kao@sim2travel.com");
			//sendHTMLMail(subject, report, "USPacketReport", mailReceiver );
			//sendHTMLMail("美國流量包Report", report, "DVRS Report", "ranger.kao@sim2travel.com");
			

				

		} catch (SQLException e) {
			ErrorHandle("At set sendUSReport Got a SQLException", e);
		} catch (UnsupportedEncodingException e) {
			ErrorHandle("At set sendUSReport Got a UnsupportedEncodingException", e);
		} catch (FileNotFoundException e) {
			ErrorHandle("At set sendUSReport Got a FileNotFoundException", e);
		} catch (AddressException e) {
			ErrorHandle("At set sendUSReport Got a AddressException", e);
		} catch (MessagingException e) {
			ErrorHandle("At set sendUSReport Got a MessagingException", e);
		} catch (IOException e) {
			ErrorHandle("At set sendUSReport Got a IOException", e);
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
		if(cont==null){
			cont="";
		}
		if(e!=null){
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			//send mail
			errorMsg=s.toString();
		}else{
			logger.error(cont);
			errorMsg="";
		}
		logger.error(cont+"\n"+errorMsg);
		sendErrorMail(cont+"\n"+errorMsg);
	}
	
	
	static String errorReceviver,errorSubject,errorContent;
	
	static void sendErrorMail(String msg){

		errorReceviver=props.getProperty("mail.errorReceviver");
		errorSubject="DailyReport Warnning Mail";
		errorContent="Error :"+msg+"<br>\n"
				+ "Error occurr time: "+DateFormat()+"<br>\n"
				+ "Error Msg : "+errorMsg;	
		
		String [] cmd=new String[3];
		cmd[0]="/bin/bash";
		cmd[1]="-c";
		cmd[2]= "/bin/echo \""+errorContent+"\" | /bin/mail -s \""+errorSubject+"\" -r DailyReport_ALERT "+errorReceviver;

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
	
	public void sendMailwithAuthenticator(String subject, String content,	String sender, String receiver,String fileName) throws AddressException, MessagingException, IOException {

		Session session = javax.mail.Session.getInstance(props);

		Message message = new MimeMessage(session);
		//message.setHeader("Disposition-Notification-To", "ranger.kao@sim2travel.com");//回條參數
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(receiver));
		message.setSubject(subject);
		
		if(fileName!=null ){
			BodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			
			messageBodyPart.setText(content);
			multipart.addBodyPart(messageBodyPart);
			
			
			/*MimeBodyPart  filepart = new MimeBodyPart ();
			filepart.attachFile(fileName);
			filepart.setFileName(fileName);*/
			BodyPart filePart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileName);
			filePart.setDataHandler(new DataHandler(source));
			filePart.setFileName(fileName);	
			
			
			multipart.addBodyPart(filePart);
			
			message.setContent(multipart);
			//message.setText(content);
		}else{
			message.setText(content);
		}
		
		
		String ports = props.getProperty("mail.smtp.port").trim();
		int port = Integer.parseInt(ports);
		Transport transport = session.getTransport(props.getProperty("mail.smtp.protocol"));
		transport.connect(props.getProperty("mail.smtp.host").trim(), 
				port, 
				props.getProperty("mail.smtp.username").trim(), 
				props.getProperty("mail.smtp.password").trim());
	    transport.sendMessage(message, message.getAllRecipients());
	    System.out.println("Send mail finished.");
		
	}	

}
