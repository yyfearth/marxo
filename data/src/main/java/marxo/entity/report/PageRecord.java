package marxo.entity.report;

import marxo.entity.action.Content;
import marxo.tool.Loggable;

public class PageRecord extends Record implements Loggable {
	public int submissionsCount = 0;
	public int viewCount = 0;

	public static PageRecord getInstance(Content content) {
		assert content.getType().equals(Content.Type.PAGE);

		PageRecord pageRecord = new PageRecord();

		pageRecord.viewCount = content.getViewCount();
		pageRecord.submissionsCount = content.submissions.size();

		logger.debug(String.format("%s records %s", content, pageRecord));

		return pageRecord;
	}
}
