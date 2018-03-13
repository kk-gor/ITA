package ua.itatool.utils;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.itatool.database.model.Product;

/**
 * Created by djdf.crash on 09.03.2018.
 */

public class ParserHelper {

    private static final String FILE_NAME ="GiacenzeUA.CSV";
    private static final String CSV_SPLIT_BY =";";

    public static List<Product> parseFile(Context ctx, Map<String, String> messageText){
        String csvFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+FILE_NAME;
        File file = new File(csvFile);

        long syncModifiedFile = PreferenceManager.getDefaultSharedPreferences(ctx).getLong("sync_modified_file", 0);

        BufferedReader br = null;

        List<Product> productList = new ArrayList<>();

        try {

            if (file.exists()){
                long modifiedFile = file.lastModified();
                if (modifiedFile != syncModifiedFile) {
                    PreferenceManager.getDefaultSharedPreferences(ctx).edit().putLong("sync_modified_file", modifiedFile).apply();
                    br = new BufferedReader(new FileReader(file));
                    productList = parserBufferedReader(br);
                }
            }else {
                messageText.put("MESSAGE_ERROR", "Не знайденный файл \""+FILE_NAME+"\" в папці: Download");
            }

        } catch (IOException e) {
            messageText.put("MESSAGE_ERROR", e.getMessage());
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return productList;
    }

    public static List<Product> parseUrlFile(Context ctx, String stringUrl, String login, String password, Map<String, String> messageText){

        long syncModifiedFile = PreferenceManager.getDefaultSharedPreferences(ctx).getLong("sync_modified_file", 0);


        FTPClient ftpClient = new FTPClient();

        BufferedReader br = null;
        List<Product> productList = new ArrayList<>();

        try {
            ftpClient.setDefaultTimeout(30*1000);
            ftpClient.setConnectTimeout(30*1000);
            ftpClient.connect(stringUrl);
            ftpClient.enterLocalPassiveMode();
            boolean isLogin = ftpClient.login(login, password);
            if (!isLogin){
                messageText.put("MESSAGE_ERROR", "Помилка авторизації на сервері");
                return productList;
            }else if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
                ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
                FTPFile[] listFiles = ftpClient.listFiles();
                for (FTPFile ftpFile : listFiles) {
                    if (ftpFile.getName().equals(FILE_NAME)){
                        long timestamp = ftpFile.getTimestamp().getTimeInMillis();
                        if (timestamp != syncModifiedFile){
                            br = new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream(ftpFile.getName())));
                            productList = parserBufferedReader(br);
                            PreferenceManager.getDefaultSharedPreferences(ctx).edit().putLong("sync_modified_file", timestamp).apply();
                        }else {
                            messageText.put("MESSAGE_ERROR", "Відсутні нові дані");
                        }
                        break;
                    }
                }
            }
            ftpClient.disconnect();
        } catch (IOException e) {
            messageText.put("MESSAGE_ERROR", e.getMessage());
        }finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


        return productList;
    }

    private static List<Product> parserBufferedReader(BufferedReader br) throws IOException {
        String line;
        List<Product> productList = new ArrayList<>();
        while ((line = br.readLine()) != null) {

            String[] split = line.split(CSV_SPLIT_BY);

            String article = split[0].trim();
            String count = split[1].trim();

            productList.add(new Product(article, count));
        }
        return productList;
    }
}
