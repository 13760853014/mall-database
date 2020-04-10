package com.jianke.sync;

import com.alibaba.fastjson.JSON;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class H20121012 {
    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     *
     * @param filePath
     */
    public static void readTxtFile(String filePath) {
        try {
            int count = 1;
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    //System.out.println(lineTxt);
                    sync(lineTxt);
                    count ++;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void main(String argv[]) {
        String filePath = "C:\\Users\\chenguiquan\\Desktop\\药房网产品.txt";
        readTxtFile(filePath);
    }

    private static void sync(String id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> formEntity = new HttpEntity(getHeaders());
        try {
            Thread.sleep(3000);
            ResponseEntity obj = restTemplate.exchange("http://mgmt-gateway.internal.jianke.com/mall-open-sync/channel-products/" + id, HttpMethod.GET, formEntity, Object.class);
            HttpEntity<String> httpEntity1 = new HttpEntity(JSON.toJSONString(obj.getBody()),getHeaders());
            //restTemplate.exchange("http://mgmt-gateway.internal.jianke.com/mall-open-sync/channel-products/" + id, HttpMethod.PUT, httpEntity1, Object.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(id);
        }
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String authorization = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2NvdW50X2lkIjoiRjJENzREMEYtRDc5NC00NURFLTk3RjktRDREQUNBNEU5RkQ1IiwidXNlcl9uYW1lIjoiY2hlbmd1aXF1YW4iLCJzY29wZSI6WyJvcGVuaWQiXSwiYWNjb3VudF9jb2RlIjoiMzU3MTEiLCJleHAiOjE1ODY1OTYzNzAsImRpc3BsYXlfbmFtZSI6IumZiOahguaziSIsImF1dGhvcml0aWVzIjpbInByb21vOmNvbWJpbmF0aW9uOnZpZXciLCJwcm9tbzpwcmljZTptb25pdG9yIiwicHJvbW86cHJpY2U6aW5zZXJ0IiwicHJvbW86YXVkaXQ6dmlldyIsInByb21vOmFkbWluIiwicHJvbW86cHJpY2U6dmlldyIsInByb21vOmNvbWJpbmF0aW9uOmVkaXQiLCJwcm9tbzpnaWZ0OmVkaXQiLCJwcm9tbzpjb21iaW5hdGlvbjppbmZvOmVkaXQiLCJwcm9tbzppdGVtOnVwbG9hZDppdGVtOmJhdGNoIiwicHJvbW86YXVkaXQ6YXVkaXQ6bGV2ZWwyIiwicHJvbW86YXVkaXQ6YXVkaXQ6bGV2ZWwxIiwicHJvbW86YXVkaXQ6YXVkaXQ6bGV2ZWwzIiwicHJvbW86cHJpY2U6Y2hhbmdlOnJlcG9ydDp2aWV3IiwicHJvbW86Y29tYmluYXRpb246aW5mbzp2aWV3IiwicHJvbW86aXRlbTplZGl0IiwicHJvbW86bm9ucHJvbW86ZWRpdCIsInByb21vOm5vbnByb21vOnZpZXciLCJwcm9tbzphY3Rpdml0eTpsb2c6dmlldyIsInByb21vOml0ZW06dmlldyIsInByb21vOnByb2ZpdDplZGl0IiwicHJvbW86cmViYXRlOmVkaXQiLCJwcm9tbzpyZWJhdGU6dmlldyIsInByb21vOnByb2ZpdDp2aWV3IiwicHJvbW86cHJpY2U6cHVyY2hhc2VMaW1pdDpwcmljZVNldHRpbmc6dmlldyIsInByb21vOnByaWNlOmVkaXQiLCJwcm9tbzpwcmljZTp1cGxvYWQ6YmF0Y2giLCJwcm9tbzpwcmljZTpjaGFuZ2U6c3VtbWFyeTp2aWV3IiwicHJvbW86aXRlbTpkb3dubG9hZDp0ZW1wbGF0ZSIsInByb21vOmdpZnQ6dmlldyJdLCJqdGkiOiIyNmM5N2RkZC0wZGI4LTRiMWUtYmE5Ni0wMTRmZTJkZDM0YzMiLCJjbGllbnRfaWQiOiJhY21lIiwic3RhZmZfbnVtYmVyIjoiMjY5OSJ9.g64I1m32BOkoJ3tad2wVbVrsh_JN-sDjQeO0ZOZGkd4jc9Bpt6gbsCy28aDNACaX_M57_mR_7Nt38riOV5I0BEOoQg6t27ywXbVS8WbayqGml7J5INFA_MgMWbkTLaLm6GAmG84Y0aj2CEt-cOWZLINPdIUKaYCOvvbnGFyZj8E";
        headers.set("Authorization", "Bearer " + authorization);
        return headers;
    }

}