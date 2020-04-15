package com.plansolve.farm.service.common;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * FileName: FileServiceImpl
 * Author: 高一平
 * Date: 2018/2/27 11:47
 * Description: 文件的保存和删除服务
 */

@Service
public class FileService {

    private String rootDefaultPath = this.getClass().getClassLoader().getResource("").getPath();

    /**
     * 获取默认文件保存目录
     *
     * @return
     */
    private String getRootDefaultPath() {
        return rootDefaultPath;
    }

    /**
     * 获取32位UUID随机数
     *
     * @return
     */
    private String getRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 保存文件
     *
     * @param dir  文件保存文件夹（子路径）
     * @param file 所要保存的文件
     * @return 文件名
     */
    public String saveFileByDefaultPath(String dir, MultipartFile file) throws IOException {
        return saveFileByPath(getRootDefaultPath(), dir, getRandomUUID(), file);
    }

    /**
     * 保存文件
     *
     * @param dir      文件保存文件夹（子路径）
     * @param fileName 文件名
     * @param file     所要保存的文件
     * @return 文件名
     */
    public String saveFileByDefaultPath(String dir, String fileName, MultipartFile file) throws IOException {
        return saveFileByPath(getRootDefaultPath(), dir, fileName, file);
    }

    /**
     * 保存文件
     *
     * @param rootPath 文件保存路径（父路径）
     * @param dir      文件保存文件夹（子路径）
     * @param file     所要保存的文件
     * @return 文件名
     */
    public String saveFileByPath(String rootPath, String dir, MultipartFile file) throws IOException {
        return saveFileByPath(rootPath, dir, getRandomUUID(), file);
    }

    /**
     * 保存文件
     *
     * @param rootPath 文件保存路径（父路径）
     * @param dir      文件保存文件夹（子路径）
     * @param fileName 文件名
     * @param file     所要保存的文件
     * @return 文件名
     */
    public String saveFileByPath(String rootPath, String dir, String fileName, MultipartFile file) throws IOException {
        if (!(fileName != null && fileName.length() > 0)) {
            fileName = getRandomUUID();
        }
        // 文件后缀名
        /*String ext = FilenameUtils.getExtension(file.getOriginalFilename());*/
        String fileOriginalFilename = file.getOriginalFilename();
        String ext = fileOriginalFilename.substring(fileOriginalFilename.indexOf(".") + 1, fileOriginalFilename.length());

        if (ext != null && ext.length() > 0) {
            fileName = fileName.replace("." + ext, "") + "." + ext;
        }
        rootPath = dealPath(rootPath);
        dir = dealDir(dir, fileName);
        fileName = dealFileName(fileName);

        rootPath = rootPath + dir;

        File rootDir = new File(rootPath);
        if (rootDir.exists() == false || rootDir.isDirectory() == false) {
            rootDir.mkdirs();
        }

        file.transferTo(new File(rootPath + fileName));
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param dir      文件保存文件夹（子路径）
     * @param fileName 文件名
     * @return 文件名
     */
    public String deleteFileByDefaultPath(String dir, String fileName) {
        return deleteFileByPath(getRootDefaultPath(), dir, fileName);
    }

    /**
     * 删除文件
     *
     * @param rootPath 文件保存路径（父路径）
     * @param dir      文件保存文件夹（子路径）
     * @param fileName 文件名
     * @return 文件名
     */
    public String deleteFileByPath(String rootPath, String dir, String fileName) {
        rootPath = dealPath(rootPath);
        dir = dealDir(dir, fileName);
        fileName = dealFileName(fileName);
        rootPath = rootPath + dir;
        File file = new File(rootPath + fileName);
        if (file.exists()) {
            file.delete();
        }
        return fileName;
    }

    /**
     * 格式化路径
     *
     * @param rootPath
     */
    private String dealPath(String rootPath) {
        if (rootPath != null && rootPath.length() > 0) {
            if (rootPath.endsWith("/") == false) {
                rootPath = rootPath + "/";
            }
        } else {
            rootPath = getRootDefaultPath();
        }
        return rootPath;
    }

    /**
     * 格式化子路径
     *
     * @param dir
     * @return
     */
    private String dealDir(String dir, String fileName) {
        if (dir != null && dir.length() > 0) {
            if (dir.endsWith("/") == false) {
                dir = dir + "/";
            }
            if (dir.startsWith("/") == true) {
                dir = dir.substring(1, dir.length());
            }
        } else {
            dir = "";
        }
        int index = fileName.lastIndexOf("/") + 1;
        dir = dir + fileName.substring(0, index);
        return dir;
    }

    /**
     * 获取文件名
     *
     * @param fileName
     */
    private String dealFileName(String fileName) {
        int index = fileName.lastIndexOf("/") + 1;
        fileName = fileName.substring(index, fileName.length());
        return fileName;
    }

}
