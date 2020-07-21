package io.renren.modules.test.utils;

import io.renren.common.exception.RRException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileSplitUtils {
    public static List<File> splitDataToSaveFile(int rows, File sourceFile, String targetDirectoryPath) {
        long startTime = System.currentTimeMillis();
        List<File> fileList = new ArrayList<>();
        System.out.println("开始分割文件");
        File targetFile = new File(targetDirectoryPath);
        if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
            return null;
        }
        if (targetFile.exists()) {
            if (!targetFile.isDirectory()) {
                return null;
            }
        } else {
            targetFile.mkdirs();
        }

        try (FileInputStream fileInputStream = new FileInputStream(sourceFile);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            StringBuilder stringBuilder = new StringBuilder();
            String lineStr;
            int lineNo = 1, fileNum = 1;
            while ((lineStr = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineStr).append("\r\n");
                if (lineNo % rows == 0) {
                    File targetFolder = new File(targetDirectoryPath + File.separator + fileNum + File.separator);
                    if(!targetFolder.exists()) {
                        targetFolder.mkdir();
                    }
                    File file = new File(targetDirectoryPath + File.separator + fileNum + File.separator  + sourceFile.getName());

                    writeFile(stringBuilder.toString(), file);
                    //清空文本
                    stringBuilder.delete(0, stringBuilder.length());
                    fileNum++;
                    fileList.add(file);
                }
                lineNo++;
            }
            if ((lineNo - 1) % rows != 0) {
                File targetFolder = new File(targetDirectoryPath + File.separator + fileNum + File.separator);
                if(!targetFolder.exists()) {
                    targetFolder.mkdir();
                }
                File file = new File(targetDirectoryPath + File.separator + fileNum  + File.separator  +  sourceFile.getName());
                writeFile(stringBuilder.toString(), file);
                fileList.add(file);
            }
            long endTime = System.currentTimeMillis();
//            log.info("分割文件结束，耗时：{}秒", (endTime - startTime) / 1000);
        } catch (Exception e) {
//            log.error("分割文件异常", e);
            throw new RRException("分割文件异常！", e);
        }
        return fileList;
    }

    private static void writeFile(String text, File file) {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter, 1024)
        ) {
            bufferedWriter.write(text);
        } catch (IOException e) {
            throw new RRException("分割文件时写入文件报错", e);
        }
    }

    public static int getFileRowCount(String file_path){
        int linenumber = 0;
        try{
            File file =new File(file_path);
            if(file.exists()){
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);

                while (lnr.readLine() != null){
                    linenumber++;
                }
                //System.out.println("Total number of lines : " + linenumber);
                lnr.close();
            }else{
//                System.out.println("File does not exists!");
                return -1;
            }
        }catch(IOException e){
            throw new RRException("统计文件行数报错", e);
        }
        return linenumber;
    }
}
