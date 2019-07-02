import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import java.io.*;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 * 抓取B站弹幕
 */
public class GrabData {
    //public static String TMP_COOKIES="CURRENT_FNVAL=16";
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            getMessage(httpClient);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void getMessage(HttpClient httpClient) throws IOException {
        //String dataUrl="https://api.bilibili.com/x/v2/dm/recent?pn="+cursor+"&ps="+limit;
        String dataUrl="https://api.bilibili.com/x/v1/dm/list.so?oid=100023105";
        PostMethod postMethod=new PostMethod();
        GetMethod getMethod = new GetMethod(dataUrl);
        postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        httpClient.executeMethod(getMethod);

        //get response body stream
        InputStream stream = getMethod.getResponseBodyAsStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InflaterInputStream iis = new InflaterInputStream(stream, new Inflater(true));
        if(iis.markSupported()){
            iis.mark(0);
        }
        if(iis.markSupported()){
            iis.reset();
        }
        while (iis.available() != 0){
            buffer.write(iis.read());
        }

        iis.close();
        //convert to string, (XML format)
        String msg = new String(buffer.toByteArray(), "utf-8");

        //convert XML TO json
        JSONObject xmlJSONObj = XML.toJSONObject(msg);
        String jsonStr = xmlJSONObj.toString(4);
        //write json to local file
        writeContent(jsonStr);

        //get DanMu only
        JSONArray contents = xmlJSONObj.getJSONObject("i").getJSONArray("d");
        for(int i = 0; i < contents.length(); i++){
            JSONObject jobj = contents.getJSONObject(i);
            String danmu;

           try{
               danmu = jobj.getString("content");

            }catch (Exception e)
            {
                danmu = String.valueOf(jobj.getInt("content"));
            };
            System.out.println(danmu);
            copyDanMu(danmu);
        }

    }


    public  static void  writeContent(String data){
        try{
            File file =new File("allContent.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(),false);
            BufferedWriter bw= new BufferedWriter(fileWritter);
            bw.write(data);
            bw.newLine();
            bw.flush();
            bw.close();
           // System.out.println("Done");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public  static void copyDanMu(String data){
        try{
            File file =new File("DanMu.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bw= new BufferedWriter(fileWritter);
            bw.write(data);
            bw.newLine();
            bw.flush();
            bw.close();
            // System.out.println("Done");
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
