package marxo.controller;

import com.google.common.io.Files;
import marxo.entity.FileInfo;
import marxo.entity.MongoDbAware;
import marxo.exception.EntityNotFoundException;
import marxo.exception.RequestParameterException;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping(value = "file")
public class FileController extends BasicController implements MongoDbAware {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public FileInfo uploadFile(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileInfo fileInfo = null;

		if (file != null && !file.isEmpty()) {
			fileInfo = new FileInfo();

			// todo: use configurable directory for files.
			File file1 = new File(fileInfo.id.toString());
			if (file1.exists()) {
				throw new IOException(String.format("File [%s] already exists", file1.getAbsolutePath()));
			}
			try {
				file.transferTo(file1);
				logger.debug(String.format("File is saved to [%s]", file1.getAbsolutePath()));
			} catch (IOException | IllegalStateException e) {
				throw new IOException(String.format("Cannot transfer file to %s", file1.getAbsolutePath()));
			}

			fileInfo.originalFilename = file.getOriginalFilename();
			fileInfo.size = file.getSize();
			fileInfo.contentType = file.getContentType();

			fileInfo.save();
		}

		if (fileInfo == null) {
			throw new RequestParameterException(String.format("Request contains no data"));
		}

		response.addHeader("Localtion", String.format("/file/%s", fileInfo.id));
		return fileInfo;
	}

	@RequestMapping(value = "{fileIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public byte[] downloadFile(@PathVariable String fileIdString, HttpServletResponse response) throws Exception {
		Assert.isTrue(ObjectId.isValid(fileIdString));
		ObjectId fileId = new ObjectId(fileIdString);

		FileInfo fileInfo = FileInfo.get(fileId);

		if (fileInfo == null) {
			throw new EntityNotFoundException(FileInfo.class, fileId);
		}

		File file = new File(fileInfo.id.toString());

		response.addHeader("Content-Type", fileInfo.contentType);
		response.addHeader("Content-Length", fileInfo.size.toString());
		response.addHeader("Content-Disposition", "attachment; " + fileInfo.originalFilename);

		return Files.toByteArray(file);
	}

	@RequestMapping(value = "{fileIdString:[\\da-fA-F]{24}}/info", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void getInfo(@PathVariable String fileIdString) {

	}
}
