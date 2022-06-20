package iee.yh.onlineoffice.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 本类通过使用shell脚本来调用python的人脸识别程序
 * @author yanghan
 * @date 2022/5/1
 */
@Component
public class FaceRecongnitionPythonUtils {
    public FaceRecongnitionPythonUtils(){

    }

    private static String DIR_PATH = "/Users/yanghan/Desktop/Project/project2/OnlineOfiice/src/main/resources/";

    /**
     * 人脸识别
     * @param path 用户上传的图像
     * @param save 用户脸部模型存储位置
     * @return
     */
    public static boolean runRecongnition(String path,String save){
        Process p;
        String cmd= DIR_PATH+"Recongnition.sh" +" "+ path + " " + save;//利用CMD命令调用python，包含两个参数
        String res = "";
        try{
            p = Runtime.getRuntime().exec(cmd);
            InputStream fis=p.getInputStream();
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            int len = 0;
            char[] chars = new char[1024];
            while((len = br.read(chars))!=-1){
                res += new String(chars,0,len);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(res))
            return false;
        return "success".equals(res.substring(0,res.length()-1)) ? true : false;
    }

    /**
     * 存储脸部模型
     * @param path 图像路径
     * @param save 存储模型路径
     * @param userId 用户id
     * @return
     * @throws IOException
     */
    public static String runSaveModel(String path,String save ,int userId) throws IOException {
        String cmd= DIR_PATH+"SaveFaceModel.sh" + " " + path + " " + save + " " + userId;//利用CMD命令调用python，包含两个参数
        Process p;
        try{
            p = Runtime.getRuntime().exec(cmd);
            InputStream fis=p.getInputStream();
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            int len = 0;
            char[] chars = new char[1024];
            String res = "";
            while((len = br.read(chars))!=-1){
                res += new String(chars,0,len);
            }
            //去除最后一个换行符
            if (!StringUtils.isEmpty(res))
                return res.substring(0,res.length()-1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
