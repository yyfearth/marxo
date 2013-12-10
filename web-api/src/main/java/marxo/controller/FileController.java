package marxo.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import marxo.entity.FileInfo;
import marxo.entity.MongoDbAware;
import marxo.exception.EntityNotFoundException;
import marxo.exception.RequestParameterException;
import marxo.serialization.MarxoObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Controller
@RequestMapping(value = "file{:s?}")
public class FileController extends BasicController implements MongoDbAware {

	static MarxoObjectMapper marxoObjectMapper = new MarxoObjectMapper();

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public FileInfo uploadFile(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileInfo fileInfo = null;

		if (file != null && !file.isEmpty()) {
			fileInfo = new FileInfo();
			fileInfo.originalFilename = file.getOriginalFilename();
			fileInfo.size = file.getSize();
			fileInfo.contentType = file.getContentType();

			try (InputStream stream = file.getInputStream()) {
				GridFSFile gridFSFile = gridFsTemplate.store(stream, fileInfo.id.toString(), fileInfo.contentType, fileInfo);
				logger.debug(String.format("%s is saved", gridFSFile));
			} catch (IOException e) {
				throw new IOException(String.format("Cannot save %s", fileInfo));
			}
		}

		if (fileInfo == null) {
			throw new RequestParameterException(String.format("Request contains no data"));
		}

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/file/{id}").build().expand(fileInfo.id.toString()).toUri();
		response.setHeader("Localtion", location.toString());

		return fileInfo;
	}

	@RequestMapping(value = "{fileIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void downloadFile(@PathVariable String fileIdString, HttpServletResponse response) throws Exception {
		Assert.isTrue(ObjectId.isValid(fileIdString));
		ObjectId fileId = new ObjectId(fileIdString);

		GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(fileIdString)));

		if (gridFSDBFile == null) {
			throw new EntityNotFoundException("File", fileId);
		}

		response.addHeader("Content-Type", gridFSDBFile.getContentType());
		response.addHeader("Content-Length", String.valueOf(gridFSDBFile.getLength()));
		response.addHeader("File-Meta", gridFSDBFile.getMetaData().toString());

		gridFSDBFile.writeTo(response.getOutputStream());
	}

	@RequestMapping(value = "{fileIdString:[\\da-fA-F]{24}}/download", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void forceDownloadFile(@PathVariable String fileIdString, HttpServletResponse response) throws Exception {
		Assert.isTrue(ObjectId.isValid(fileIdString));
		ObjectId fileId = new ObjectId(fileIdString);

		GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(fileIdString)));

		if (gridFSDBFile == null) {
			throw new EntityNotFoundException("File", fileId);
		}

		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Length", String.valueOf(gridFSDBFile.getLength()));
		response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", gridFSDBFile.getMetaData().get("originalFilename")));

		gridFSDBFile.writeTo(response.getOutputStream());
	}

	@RequestMapping(value = "{fileIdString:[\\da-fA-F]{24}}/info", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public FileInfo getInfo(@PathVariable String fileIdString) throws Exception {
		Assert.isTrue(ObjectId.isValid(fileIdString));
		ObjectId fileId = new ObjectId(fileIdString);

		GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(fileIdString)));

		if (gridFSDBFile == null) {
			throw new EntityNotFoundException("File", fileId);
		}

		return mappingConverter.read(FileInfo.class, gridFSDBFile.getMetaData());
	}
}
