package ticket.server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ticket.server.model.apk.ApkVersion;
import ticket.server.service.ApkVersionService;

@RestController
@RequestMapping("apkVersion")
@PropertySource({ "classpath:/META-INF/ticket.properties" })
public class ApkVersionController {

	@Autowired
	ApkVersionService apkVersionService;

	@Autowired
	private Environment env;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ApkVersion findApkVersion() {
		ApkVersion apkVersion = apkVersionService.findById(new Long(1));
		return apkVersion;
	}

	@CrossOrigin
	@RequestMapping(value = "/apk/{fileName}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable String fileName)
			throws IOException {
		File image = new File(env.getRequiredProperty("apkDir") + File.separator + fileName);
		InputStream in = new FileInputStream(image);
		return ResponseEntity.ok().contentLength(in.available())
				.header("Content-Disposition", "attachment; filename=" + fileName)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(in));
	}

}
