package cn.trt.jde;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.POST;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;

public class JDEClient {
	private static Logger logger = Logger.getLogger(JDEClient.class);
	private String url;
	
	/**����ϵͳ�����û�WS
	 * @param cActionCode �½� A �޸� C
	 * @param szLongAddressNumber ���ͻ���
	 * @param szAlphaName �ͻ�����
	 * @param szPhoneAreaCode2  ����
	 * @param szPhoneNumber2 �绰��
	 * @return
	 */
	public String createUser(String szLongAddressNumber,String szAlphaName,String szSearchType){
		logger.debug("TRTWSClient createUser request parm>>>>>-szLongAddressNumber:" + szLongAddressNumber + "-szAlphaName:"
				+ szAlphaName+"szSearchType:"+szSearchType);
		
		String returnCode = "";
		StringBuffer soap = new StringBuffer();
		soap.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:add=\"urn:iwaysoftware:jde/services/CALLBSFN/AddressBook/AddressBookMasterMBF\">");
		soap.append("<soapenv:Header/>");
		soap.append("<soapenv:Body>");
		soap.append("<add:jdeRequest type=\"callmethod\"   xmlns:add=\"urn:iwaysoftware:jde/services/CALLBSFN/AddressBook/AddressBookMasterMBF\">");
		soap.append(" <add:callMethod name=\"AddressBookMasterMBF\" >");
		soap.append("<add:params>");
		soap.append("<add:param name=\"cActionCode\">").append("A").append("</add:param>");
		soap.append("<add:param name=\"cUpdateMasterFile\">").append("1").append("</add:param>");
		soap.append("<add:param name=\"szLongAddressNumber\">").append(szLongAddressNumber).append("</add:param>");
		soap.append("<add:param name=\"szAlphaName\">").append(szAlphaName).append("</add:param>");
		soap.append("<add:param name=\"szSearchType\">").append(szSearchType).append("</add:param>");
		soap.append("<add:param name=\"szBusinessUnit\">").append("W10").append("</add:param>");
		soap.append("</add:params>");
		soap.append(" <add:onError abort=\"yes\">");
		soap.append(" </add:onError>");
		soap.append(" </add:callMethod>");
		soap.append(" </add:jdeRequest>");
		soap.append(" </soapenv:Body>");
		soap.append(" </soapenv:Envelope>");
		try
		{
			returnCode = Call(url, soap.toString(), "AddressBookMasterMBF", "ResponseCode");
		}
		catch (Exception e)
		{
//			throw new RuntimeException("returnCode δ��ȡֵ", e);
			e.printStackTrace();
		}
//		logger.debug("����ֵ222������"+returnCode);
		return returnCode;
		
	}
	
	/**����ϵͳ�޸��û���ϢWS
	 * @param employeeNumber  Ա�����
	 * @param displayName  Ա������
	 * @param telephoneNumber  Ա���칫����
	 * @param mobile  Ա���ֻ�����
	 * @return   ���������û�
	 */
	public String modifyUser(String cActionCode,String szLongAddressNumber,String szAlphaName,String szPhoneAreaCode2,String szPhoneNumber2){
		cActionCode = "C";
		logger.debug("TRTWSClient createUser request parm>>>>>cActionCode:"
				+ cActionCode + "-szLongAddressNumber:" + szLongAddressNumber + "-telephoneNumber:" + szPhoneAreaCode2+szPhoneNumber2 + "-szAlphaName:"
				+ szAlphaName);
		String returnCode="";
		String telephoneNumber = szPhoneAreaCode2+szPhoneNumber2;
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(url));
			call.setOperationName("modifyAccount");
			call.addParameter("szLongAddressNumber", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("szAlphaName", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("szPhoneAreaCode2", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("szPhoneNumber2", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			returnCode = (String) call.invoke(new Object[]{cActionCode,szLongAddressNumber,szAlphaName,telephoneNumber});
			System.out.println("�޸ķ���ֵ��"+returnCode);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		logger.debug("�޸ĳɹ�");
		return returnCode;
	}
	

	public JDEClient() {
		this.url = "http://10.8.147.177:8001/WP_DEMO_SOA/Proxy_Services/TA_JDE/AddressBookMasterMBF_PS?WSDL";
	}
	
	public JDEClient(String url){
		this.url = url;
	}
	public static void main(String[] args) {
		JDEClient jdeCli = new JDEClient();
		jdeCli.createUser("100001231", "����", "E");
//		jdeCli.modifyUser("", "", "", "", "123");
//		jdeCli.restoreUser("01");
//		jdeCli.suspendUser("02");
	}
	
	
	
	public static String Call(String wsUrl, String soapRequest, String action, String reponse)
			throws Exception
		{
			System.out.println((new StringBuilder("soapRequest : ")).append(soapRequest).toString());
			String str = null;
			URI uri = new URI(wsUrl);
			URL url = uri.toURL();
			HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpUrlConnection.addRequestProperty("SOAPAction", (new StringBuilder(String.valueOf(wsUrl))).append("/").append(action).toString());
			httpUrlConnection.setDoOutput(true);
//			httpUrlConnection.setRequestMethod("GET");
//			System.out.println(">״̬��>"+httpUrlConnection.getResponseCode());
			OutputStream os = httpUrlConnection.getOutputStream();
			PrintWriter out = new PrintWriter(os);
			out.println(soapRequest);
			out.flush();
			os.close();
			out.close();
			StringBuilder sb = new StringBuilder();
			System.out.println((new StringBuilder("ResponseCode : ")).append(httpUrlConnection.getResponseCode()).toString());
			if (200 == httpUrlConnection.getResponseCode())//HTTP_OK
			{	System.out.println("�����200������");
				InputStream is = httpUrlConnection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				for (String line = br.readLine(); line != null; line = br.readLine())
				{
					sb.append(line);
				}
				is.close();
			}
			System.out.println("�ܿ�200����");
			httpUrlConnection.disconnect();
			String soapResponse = sb.toString();
			System.out.println((new StringBuilder("soapResponse : ")).append(soapResponse).toString());
			Pattern pattern = Pattern.compile((new StringBuilder("<")).append(reponse).append(">(.*?)</").append(reponse).append(">").toString());
			for (Matcher matcher = pattern.matcher(soapResponse); matcher.find();)
			{
				str = matcher.group(0);
			}

			str = str.substring(str.indexOf(">") + 1, str.lastIndexOf("<"));
			return str;
		}
	
	
	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		JDEClient.logger = logger;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
