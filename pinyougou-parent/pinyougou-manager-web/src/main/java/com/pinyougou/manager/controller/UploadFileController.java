package com.pinyougou.manager.controller;

import com.pinyougou.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/upload")
public class UploadFileController {

	@Value("${FILE_SERVER_URL}")
	private String file_server_url;

	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file) {
		try {
			String originalFilename = file.getOriginalFilename();
			int index = originalFilename.lastIndexOf(".");
			String extName = originalFilename.substring(index + 1);
			FastDFSClient client = new FastDFSClient("classpath:properties/fdfs_client.conf");
			String url = file_server_url + client.uploadFile(file.getBytes(), extName);
			return new Result(true,url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}

}
